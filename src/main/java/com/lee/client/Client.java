package com.lee.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {
    public static void main(String[] args) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8081");
        try {
            for (int i = 0; i < 10; i++) {
                CloseableHttpResponse response = httpclient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println(statusCode);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
