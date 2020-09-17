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
        HttpSession httpSession2 = new HttpSession("rinima2");
        String start = httpSession.start();
        sessions.put(start, httpSession);
        String stop = httpSession.stop();
        sessions.remove(stop);
        String start2 = httpSession2.start();
        sessions.put(start2, httpSession2);
        String stop2 = httpSession2.stop();
        sessions.remove(stop2);
    }
}
