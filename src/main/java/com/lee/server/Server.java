package com.lee.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Server {
    public static void main(String[] args) {
        HttpServer server;
        try {
            // 利用线程池加快并发情况下的响应速度
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server = HttpServer.create(new InetSocketAddress("localhost", 8081), 0);
            server.createContext("/", new MyHttpHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println(" Server started on port 8081");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(200, 0);
        }
    }
}
