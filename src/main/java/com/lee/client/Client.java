package com.lee.client;

import com.lee.session.HttpSession;
import com.lee.session.Session;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {
    private static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        HttpSession httpSession = new HttpSession("rinima");
        String start = httpSession.start();
        if (start != null) {
            sessions.put(start, httpSession);
        }
        System.out.println(sessions.size());
        String stop = httpSession.stop();
        if (stop != null) {
            sessions.remove(stop);
        }
        System.out.println(sessions.size());
    }
}
