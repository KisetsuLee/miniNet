package com.lee;

import com.lee.manager.SessionManager;
import com.lee.session.Managers;

import java.util.concurrent.TimeUnit;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        SessionManager sessionManager = Managers.newFixedSessionManager(10);
        sessionManager.setConcurrency(20);
        // 多线程发起请求
        for (int i = 1; i <= 20; i++) {
            sessionManager.createSession(10000 + i, i * 1000);
        }
        TimeUnit.SECONDS.sleep(60);
        sessionManager.resetSessionExpiredTime("10020", -1);
        TimeUnit.SECONDS.sleep(3);
        sessionManager.shutdown();
    }
}
