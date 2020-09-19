package com.lee.session;

import com.lee.manager.SessionManager;
import com.lee.manager.SessionRejectHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class Managers {
    // Session并发池排队队列大小
    private static int queueSize = 5;

    // 线程池工厂方法
    public static SessionManager newFixedSessionManager(int corePoolSize) {
        return new SessionManager(corePoolSize, corePoolSize,
                0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize), new SessionThreadFactory(), new SessionRejectHandler());
    }

    /**
     * Session的线程工厂
     */
    private static class SessionThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SessionThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "session-" + "01" + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
