package com.lee;

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
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class StartActionType implements ActionTypeAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpPost post;

    private HttpSession httpSession;

    public StartActionType(String uri, HttpSession httpSession) {
        this.post = new HttpPost(uri);
        this.httpSession = httpSession;
    }

    @Override
    public String action() {
        try {
            URI uri = post.getURI();
            String query = uri.getQuery();
            long id = Long.parseLong(query.split("=")[1]);
            buildRequestBody(id);
            CloseableHttpResponse execute = httpSession.getHttpClient().execute(post);
            if (execute.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("创建Session失败");
            }
            logger.trace("收到的响应头：{}", execute.getStatusLine());
            execute.close();
            return String.valueOf(id);
        } catch (IOException e) {
            logger.error("创建Session失败", e);
            throw new RuntimeException(e);
        }
    }

    private void buildRequestBody(long id) throws UnsupportedEncodingException {
        DeliverySessionCreationType deliverySessionCreationType = new DeliverySessionCreationType();
        deliverySessionCreationType.setDeliverySessionId(id);
        deliverySessionCreationType.setAction(ActionType.START);
        String jsonString = JSON.toJSONString(deliverySessionCreationType);

        StringEntity entity;
        entity = new StringEntity(jsonString);
        post.setHeader("Content-type", "application/json");
        post.setEntity(entity);
    }
}
