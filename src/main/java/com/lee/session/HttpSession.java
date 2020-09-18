package com.lee.session;

import com.lee.RestfulService;
import com.lee.SessionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Lzj
 * Date: 2020-09-17
 * Description:
 */
public class HttpSession implements Session {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 接口服务
    private RestfulService service = new RestfulService();
    // Session唯一id
    private String id;
    // session的状态
    private int state;
    // httpClient连接管理器
    private BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
    // Session的管理器
    private SessionManager sessionManager;

    public CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    public HttpSession(String id) {
        this.id = id;
    }

    public HttpSession(String id, SessionManager sessionManager) {
        this.id = id;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public String start() {
        String sessionId = service.startSession(id, this);
        sessionManager.addSession(sessionId, this);
        return sessionId;
    }

    @Override
    public String stop() {
        String sessionId = service.stopSession(id, this);
        sessionManager.removeSession(sessionId);
        return sessionId;
    }

    @Override
    public void registerManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String getId() {
        return id;
    }

}
