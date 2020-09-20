package com.lee;

import com.alibaba.fastjson.JSON;
import com.lee.generate.ActionType;
import com.lee.generate.DeliverySessionCreationType;
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
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8081), 10);
            server.createContext("/", new MyHttpHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            logger.info("Server started on port 8081");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                TimeUnit.SECONDS.sleep(1);
                if ("POST".equals(httpExchange.getRequestMethod())) {
                    DeliverySessionCreationType requestBody = getRequestBody(httpExchange);
                    if (requestBody.getAction().equals(ActionType.START)) {
                        logger.info("[Session{}] - 连接，当前Session - [{}]个", requestBody.getDeliverySessionId(), count.getAndIncrement());
                    } else if (requestBody.getAction().equals(ActionType.STOP)) {
                        logger.info("[Session{}] - 断开，服务器当前Session - [{}]个", requestBody.getDeliverySessionId(), count.getAndIncrement());
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                }
                httpExchange.close();
            } catch (InterruptedException | IOException e) {
                logger.error("", e);
            }
        }

        private DeliverySessionCreationType getRequestBody(HttpExchange httpExchange) throws IOException {
            InputStream requestBody = httpExchange.getRequestBody();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return JSON.parseObject(sb.toString(), DeliverySessionCreationType.class);
        }
    }
}
