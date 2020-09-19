package com.lee.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 * 监听8081端口，处理接收到的所有请求，均返回200状态码
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final AtomicInteger count = new AtomicInteger(1);

    public static void main(String[] args) {
        HttpServer server;
        try {
            // 利用线程池加快并发情况下的响应速度
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8081), 10);
            server.createContext("/", new MyHttpHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            logger.debug("Server started on port 8081");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                logger.info("有一个客户端连接了" + count.getAndIncrement());
                TimeUnit.SECONDS.sleep(0);
                httpExchange.sendResponseHeaders(200, 10);
                // System.out.println(httpExchange.getRequestURI().getPath());
                // System.out.println(httpExchange.getRequestURI().getQuery());
                if ("POST".equals(httpExchange.getRequestMethod())) {
                    InputStream requestBody = httpExchange.getRequestBody();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    // logger.trace("{}", sb);
                }
                httpExchange.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
