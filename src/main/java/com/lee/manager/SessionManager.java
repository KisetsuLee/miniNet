package com.lee.manager;

import com.lee.session.HttpSession;
import com.lee.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description: session的控制器，管理session从创建到结束的生命周期
 */
public class SessionManager {
    private final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    // 任务执行器（异步并发创建session）
    private final SessionPoolExecutor threadPool;

    // Session容器
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    // Session定时任务Timer
    private final Timer timer = new Timer("httpSession-stop-timer");

    // Session定时任务
    private final ConcurrentHashMap<String, TimerTask> sessionStopTasks = new ConcurrentHashMap<>();

    // 构造方法，创建生产session的线程池
    public SessionManager(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                          BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this.threadPool = new SessionPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler);
    }

    // 创建一个新的Session
    public Future<?> createSession(long id) {
        HttpSession httpSession = checkSessionIdAndGetNewOne(id);
        return threadPool.submit(() -> newSession(httpSession));
    }

    // 创建一个新的Session
    public Future<?> createSession(long id, long sessionTime) {
        HttpSession httpSession = checkSessionIdAndGetNewOne(id);
        return threadPool.submit(() -> newSession(sessionTime, httpSession));
    }

    private HttpSession checkSessionIdAndGetNewOne(long id) {
        String sessionId = String.valueOf(id);
        if (sessions.containsKey(sessionId)) throw new RuntimeException("Session" + id + " id重复，创建失败");
        return new HttpSession(sessionId, this);
    }

    private void newSession(HttpSession httpSession) {
        httpSession.start();
        addSession(httpSession);
    }

    private void newSession(long sessionTime, HttpSession httpSession) {
        httpSession.start();
        addSession(httpSession);
        addSessionTimer(httpSession, sessionTime);
    }

    // 添加一个session的Timer
    private void addSessionTimer(HttpSession httpSession, long sessionTime) {
        if (sessionTime <= 0) {
            httpSession.resetExpiredTime(0);
            logger.info("Session{}设置存活时间为永久，不会被自动关闭", httpSession.getId());
            return;
        }
        httpSession.resetExpiredTime(sessionTime);
        SessionStopTask stopTask = new SessionStopTask(httpSession);
        timer.schedule(stopTask, sessionTime);

        logger.info("Session{}设置的存活时间为{}秒，将会在{}关闭，剩余时间{}", httpSession.getId(), sessionTime / 1000,
                httpSession.getExpiredFormatTime(), httpSession.getRemainingFormatTime());

        sessionStopTasks.put(httpSession.getId(), stopTask);
    }

    // 移除一个已经存在的session
    public void removeSession(String sessionId) {
        if (!sessions.containsKey(sessionId)) throw new RuntimeException("Session id不存在，删除失败");
        Session session = sessions.get(sessionId);
        if (sessionStopTasks.containsKey(session.getId())) {
            TimerTask timerTask = sessionStopTasks.remove(session.getId());
            timerTask.cancel();
        }
        session.stop();
        deleteSession(session);
    }

    // 重新设置session的过期时间
    public void resetSessionExpiredTime(String sessionId, long sessionTime) {
        HttpSession session = (HttpSession) sessions.get(sessionId);
        if (session == null) {
            throw new RuntimeException("重置的Session" + sessionId + "不存在");
        }
        resetSessionExpiredTime(session, sessionTime);
    }

    // 重新设置session的过期时间
    public void resetSessionExpiredTime(HttpSession session, long sessionTime) {
        if (sessionStopTasks.containsKey(session.getId())) {
            TimerTask timerTask = sessionStopTasks.remove(session.getId());
            timerTask.cancel();
        }
        logger.info("Session{}重置了过期时间", session.getId());
        addSessionTimer(session, sessionTime);
    }

    // 设置创建session并发数
    public void setConcurrency(int num) {
        threadPool.setCorePoolSize(num);
    }

    // 获取已经创建的session数量
    public int getSessionCount() {
        return sessions.size();
    }

    // 添加一个session到集合里
    private void addSession(Session session) {
        if (sessions.containsKey(session.getId())) throw new RuntimeException("Session id重复，创建失败");
        sessions.put(session.getId(), session);
        logger.info("创建Session{}成功，当前存活的Session个数为{}个", session.getId(), getSessionCount());
    }

    // 从集合里移除一个session
    private void deleteSession(Session session) {
        sessions.remove(session.getId());
        logger.info("删除Session{}成功，当前存活的Session个数为{}个", session.getId(), getSessionCount());
    }

    // 打印所有session信息
    public void getSessionsStatus() {
        sessions.forEach((k, v) -> {
            System.out.println("SessionId: " + v.getId() + " 过期时间: " + ((HttpSession) v).getExpiredFormatTime() + " 剩余时间：" + ((HttpSession) v).getRemainingFormatTime());
        });
    }

    // stop session的定时器任务class
    private class SessionStopTask extends TimerTask {
        private final Session session;

        private SessionStopTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            logger.info("Session{}存活时间到期，将会被自动删除", session.getId());
            SessionManager.this.removeSession(session.getId());
//            SessionManager.this.sessionStopTasks.remove(session.getId());
        }
    }

    // 关闭session管理器，结束掉所有的session，调用正常的stop方法
    public void shutdown() {
        logger.info("sessionManager即将关闭");
        timer.cancel();
        setConcurrency(2 * getSessionCount());
        sessionStopTasks.clear();
        sessions.forEach((k, v) -> threadPool.submit(() -> removeSession(k)));
        threadPool.shutdown();
    }


}
