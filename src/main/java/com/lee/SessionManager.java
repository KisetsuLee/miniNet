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
    private SessionPoolExecutor threadPool;

    // Session容器
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    // 构造方法，创建生产session的线程池
    public SessionManager(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                          BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this.threadPool = new SessionPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    // 创建一个新的Session
    public void createSession(long id) {
        String sessionId = String.valueOf(id);
        if (sessions.containsKey(sessionId)) throw new RuntimeException("Session id重复，创建失败");
        HttpSession httpSession = new HttpSession(sessionId, this);
        threadPool.submit(httpSession);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void setConcurrency(int num) {
        threadPool.setCorePoolSize(num);
    }

    // 防止多线程创建同一个id时发生关系
    public void addSession(String sessionId, HttpSession httpSession) {
        if (sessions.containsKey(sessionId)) throw new RuntimeException("Session id重复，创建失败");
        sessions.put(sessionId, httpSession);
    }
}
