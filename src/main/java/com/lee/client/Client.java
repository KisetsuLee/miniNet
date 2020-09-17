package com.lee.client;

import com.lee.session.HttpSession;
import com.lee.session.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {
    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 多线程发起请求
        for (int i = 0; i < 20; i++) {
            Executors.newSingleThreadExecutor().execute(() -> {
                HttpSession httpSession = new HttpSession("rinima" + 1);
                String start = httpSession.start();
                sessions.put(start, httpSession);
                String stop = httpSession.stop();
                sessions.remove(stop);
            });
        }
        System.out.println(sessions);
    }
}
