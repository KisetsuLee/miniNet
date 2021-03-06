package com.lee.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description: 增加了错误处理
 */
public class SessionPoolExecutor extends ThreadPoolExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SessionPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            logger.error("", t);
            throw new RuntimeException(t);
        }
        if (r instanceof RunnableFuture) {
            RunnableFuture<?> future = (RunnableFuture<?>) r;
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("", e);
                throw new RuntimeException(e);
            }
        }
    }
}
