package com.lee.api;

import com.alibaba.fastjson.JSON;
import com.lee.generate.ActionType;
import com.lee.generate.DeliverySessionCreationType;
import com.lee.session.HttpSession;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description: 请求服务器的接口，记录session发送时间,发送url,和body,结果,连接信息
 */
public class RestfulService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String SERVER_HOST = "http://127.0.0.1:8081";

    public void startSession(HttpSession httpSession) {
        try {
            String uri = SERVER_HOST + "/nbi/deliverysession?id=" + httpSession.getId();
            String body = buildRequestBody(httpSession, ActionType.START);
            String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(System.currentTimeMillis()));
            executeRequest(httpSession, uri, body);
            logger.info("Session{}创建成功！请求开始时间[{}],请求url[{}],请求体[{}],Session状态[新建]", httpSession.getId(), startTime, uri, body);
        } catch (IOException e) {
            logger.error("创建Session" + httpSession.getId() + "请求失败", e);
            throw new RuntimeException("创建Session" + httpSession.getId() + "请求失败", e);
        }
    }

    public void stopSession(HttpSession httpSession) {
        try {
            String uri = SERVER_HOST + "/nbi/deliverysession?id=" + httpSession.getId();
            String body = buildRequestBody(httpSession, ActionType.STOP);
            String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(System.currentTimeMillis()));
            executeRequest(httpSession, uri, body);
            logger.info("Session{}停止成功！请求开始时间[{}],请求url[{}],请求体[{}],Session状态[停止]", httpSession.getId(), startTime, uri, body);
        } catch (IOException e) {
            logger.error("停止Session" + httpSession.getId() + "请求失败", e);
            throw new RuntimeException("停止Session" + httpSession.getId() + "请求失败", e);
        }
    }

    private void executeRequest(HttpSession httpSession, String uri, String bodyString) throws IOException {
        HttpPost post = new HttpPost(uri);
        StringEntity entity = new StringEntity(bodyString);
        post.setHeader("Content-type", "application/json");
        post.setEntity(entity);
        CloseableHttpResponse execute = httpSession.getHttpClient().execute(post);
        if (execute.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Session请求失败");
        }
        execute.close();
    }


    private String buildRequestBody(HttpSession httpSession, ActionType action) {
        DeliverySessionCreationType deliverySessionCreationType = new DeliverySessionCreationType();
        deliverySessionCreationType.setDeliverySessionId(Long.parseLong(httpSession.getId()));
        deliverySessionCreationType.setAction(action);
        deliverySessionCreationType.setStartTime(System.currentTimeMillis());
        deliverySessionCreationType.setStopTime(httpSession.getExpiredTime());
        deliverySessionCreationType.setVersion("1.0.0");
        return JSON.toJSONString(deliverySessionCreationType);
    }
}
