package com.lee.session;

import com.alibaba.fastjson.JSON;
import com.lee.generate.ActionType;
import com.lee.generate.DeliverySessionCreationType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author: Lzj
 * Date: 2020-09-17
 * Description:
 */
public class HttpSession implements Session {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String id;
    private BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();

    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    public HttpSession(String id) {
        this.id = id;
    }

    @Override
    public String start() {
        DeliverySessionCreationType deliverySessionCreationType = new DeliverySessionCreationType();
        deliverySessionCreationType.setDeliverySessionId(123L);
        deliverySessionCreationType.setVersion("1.0.0");
        deliverySessionCreationType.setAction(ActionType.START);
        String jsonString = JSON.toJSONString(deliverySessionCreationType);

        HttpPost post = new HttpPost("http://127.0.0.1:8081/nbi/deliverysession?id=123");
        StringEntity entity;
        try {
            entity = new StringEntity(jsonString);
            post.setHeader("Content-type", "application/json");
            post.setEntity(entity);
            CloseableHttpResponse execute = getHttpClient().execute(post);
            logger.trace("收到的响应头：{}", execute.getStatusLine());
            if (execute.getStatusLine().getStatusCode() == 200) {
                execute.close();
                return id;
            }
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String stop() {
        DeliverySessionCreationType deliverySessionCreationType = new DeliverySessionCreationType();
        deliverySessionCreationType.setDeliverySessionId(123L);
        deliverySessionCreationType.setVersion("1.0.0");
        deliverySessionCreationType.setAction(ActionType.STOP);
        String jsonString = JSON.toJSONString(deliverySessionCreationType);

        HttpPost post = new HttpPost("http://127.0.0.1:8081/nbi/deliverysession?id=123");
        StringEntity entity;
        try {
            entity = new StringEntity(jsonString);
            post.setHeader("Content-type", "application/json");
            post.setEntity(entity);
            CloseableHttpResponse execute = getHttpClient().execute(post);
            logger.trace("收到的响应头：{}", execute.getStatusLine());
            if (execute.getStatusLine().getStatusCode() == 200) {
                execute.close();
                return id;
            }
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
