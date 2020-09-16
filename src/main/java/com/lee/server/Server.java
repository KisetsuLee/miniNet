package com.lee.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 * 监听8081端口，处理接收到的所有请求，均返回200状态码
 */
public class Server {
    public static void main(String[] args) {
        HttpServer server;
        try {
            // 利用线程池加快并发情况下的响应速度
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8081), 0);
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
            try {
                System.out.println("有一个客户端连接了");
                TimeUnit.SECONDS.sleep(1);
                httpExchange.sendResponseHeaders(200, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
