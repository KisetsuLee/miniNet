package com.lee;

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
 * Description:
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
    public void createSession(long id) {
        String sessionId = String.valueOf(id);
        if (sessions.containsKey(sessionId)) throw new RuntimeException("Session id重复，创建失败");
        HttpSession httpSession = new HttpSession(sessionId, this);
        threadPool.submit(() -> {
            httpSession.start();
            addSession(httpSession);
        });
    }

    // 创建一个新的Session
    public void createSession(long id, long sessionTime) {
        String sessionId = String.valueOf(id);
        if (sessions.containsKey(sessionId)) throw new RuntimeException("Session id重复，创建失败");
        HttpSession httpSession = new HttpSession(sessionId, this);
        threadPool.submit(() -> {
            httpSession.start();
            addSession(httpSession);
            addSessionTimer(httpSession, sessionTime);
        });
    }

    // 添加一个session的Timer
    private void addSessionTimer(HttpSession httpSession, long sessionTime) {
        if (sessionTime <= 0) {
            httpSession.resetExpiredTime(0);
            logger.info("Session{}设置为永久", httpSession.getId());
            return;
        }
        httpSession.resetExpiredTime(sessionTime);
        SessionStopTask stopTask = new SessionStopTask(httpSession);
        timer.schedule(stopTask, sessionTime);

        logger.info("Session{}将会在{}关闭，剩余时间{}", httpSession.getId(),
                httpSession.getExpiredFormatTime(), httpSession.getRemainingFormatTime());

        sessionStopTasks.put(httpSession.getId(), stopTask);
    }

    // 移除一个已经存在的session
    public void removeSession(String sessionId) {
        Session session = sessions.get(sessionId);
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
        if (!sessionStopTasks.containsKey(session.getId())) {
            throw new RuntimeException("Session" + session.getId() + "不存在");
        }
        TimerTask timerTask = sessionStopTasks.remove(session.getId());
        timerTask.cancel();
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
    }

    // 从集合里移除一个session
    private void deleteSession(Session session) {
        sessions.remove(session.getId());
    }

    // stop session的定时器任务
    private class SessionStopTask extends TimerTask {
        private final Session session;

        private SessionStopTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            SessionManager.this.removeSession(session.getId());
            SessionManager.this.sessionStopTasks.remove(session.getId());
            logger.info("sessions数量为：{}", SessionManager.this.sessions.size());
            logger.info("sessionTimers数量为：{}", SessionManager.this.sessionStopTasks.size());
        }
    }
}
