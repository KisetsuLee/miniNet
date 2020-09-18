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
    private final SessionPoolExecutor threadPool;

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
        threadPool.submit(httpSession::start);
    }

    // 移除一个已经存在的session
    public void removeSession(String sessionId) {
        Session session = sessions.get(sessionId);
        session.stop();
    }

    // 设置并发数
    public void setConcurrency(int num) {
        threadPool.setCorePoolSize(num);
    }

    public int getSessionCount() {
        return sessions.size();
    }

    // 添加一个session到集合里
    public void addSession(HttpSession httpSession) {
        if (sessions.containsKey(httpSession.getId())) throw new RuntimeException("Session id重复，创建失败");
        sessions.put(httpSession.getId(), httpSession);
    }

    // 从集合里移除一个session
    public void deleteSession(HttpSession httpSession) {
        sessions.remove(httpSession.getId());
    }
}
