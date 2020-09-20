package com.lee.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class SessionRejectHandler implements RejectedExecutionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void rejectedExecution(Runnable session, ThreadPoolExecutor executor) {
        logger.error("超过最大并发数，Session已经被丢弃");
    }
}
