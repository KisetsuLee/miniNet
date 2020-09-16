package com.lee.client;

import com.lee.Connection;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpRequestExecutor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {
    private static ConcurrentHashMap<String, Connection> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, HttpException {
        BasicHttpClientConnectionManager singleConnectionManager = new BasicHttpClientConnectionManager();
        HttpClientContext context = HttpClientContext.create();

        // lower level
        HttpRoute route = new HttpRoute(new HttpHost("127.0.0.1", 8081));
        ConnectionRequest connectionRequest = singleConnectionManager.requestConnection(route, null);
        HttpClientConnection conn = connectionRequest.get(10, TimeUnit.SECONDS);
        singleConnectionManager.connect(conn, route, 1000, context);
//        singleConnectionManager.routeComplete(conn, route, context); // 空方法

        HttpRequestExecutor exeRequest = new HttpRequestExecutor();
//        context.setTargetHost((new HttpHost("127.0.0.1", 8081)));
        HttpGet get = new HttpGet("http://127.0.0.1:8081/");

        HttpResponse response1 = exeRequest.execute(get, conn, context);
        System.out.println(response1.getStatusLine().getStatusCode());

        HttpResponse response2 = exeRequest.execute(get, conn, context);
        System.out.println(response2.getStatusLine().getStatusCode());

//        singleConnectionManager.releaseConnection(conn, null, 1, TimeUnit.SECONDS);

        // high level
//        CloseableHttpClient client = HttpClients.custom().setConnectionManager(singleConnectionManager).build();
//        CloseableHttpResponse response = client.execute(get);
//        System.out.println(response.getStatusLine().getStatusCode());

//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://127.0.0.1:8081/ttt");
//        try {
//            CloseableHttpResponse response = httpclient.execute(httpPost);
//            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println(statusCode);
//            TimeUnit.SECONDS.sleep(1);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
