package com.lee.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
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
        public void handle(HttpExchange httpExchange) {
            try {
                TimeUnit.SECONDS.sleep(0);
                System.out.println("有一个客户端连接了");
                httpExchange.sendResponseHeaders(200, 0);
                System.out.println(httpExchange.getRequestURI().getPath());
                System.out.println(httpExchange.getRequestURI().getQuery());
                if ("POST".equals(httpExchange.getRequestMethod())) {
                    InputStream requestBody = httpExchange.getRequestBody();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    System.out.println(sb);
                }
                Headers requestHeaders = httpExchange.getRequestHeaders();
                for (Map.Entry<String, List<String>> stringListEntry : requestHeaders.entrySet()) {
                    System.out.println(stringListEntry.getKey() + " " + stringListEntry.getValue());
                }
                httpExchange.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
