package com.lee;

import com.lee.session.HttpSession;
import com.lee.session.Session;

import java.util.concurrent.*;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class SessionManager {

    // 任务执行器（异步并发创建session）
    private final SessionPoolExecutor threadPool;

    // Session容器
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

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
            SessionManager.this.addSession(httpSession);
        });
    }

    // 移除一个已经存在的session
    public void removeSession(String sessionId) {
        Session session = sessions.get(sessionId);
        session.stop();
        deleteSession(session);
    }

    // 设置并发数
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
}
