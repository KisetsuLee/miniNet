package com.lee.client;

import com.lee.SessionManager;
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
        // 多线程发起请求
        for (int i = 0; i < 20; i++) {
            sessionManager.createSession(10000 + i);
        }
        TimeUnit.SECONDS.sleep(10);
        sessionManager.removeSession("10001");
    }
}
