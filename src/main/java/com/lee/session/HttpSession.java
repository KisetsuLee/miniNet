package com.lee.session;

import com.lee.api.RestfulService;
import com.lee.manager.SessionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Lzj
 * Date: 2020-09-17
 * Description:
 */
public class HttpSession implements Session {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 接口服务
    private final RestfulService service = new RestfulService();
    // Session唯一id
    private final String id;
    // session的状态
    private int state;
    // Session的管理器
    private SessionManager sessionManager;
    // Session过期时间(时间点)，比如，设置10秒，这个值就是当前时间+10秒后的UNIX时间
    private long expiredTime = -1;

    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    public HttpSession(String id) {
        this.id = id;
    }

    public HttpSession(String id, SessionManager sessionManager) {
        this.id = id;
        this.sessionManager = sessionManager;
    }

    @Override
    public void start() {
        service.startSession(this);
    }

    @Override
    public void stop() {
        service.stopSession(this);
    }

    @Override
    public void registerManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getId() {
        return id;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public String getExpiredFormatTime() {
        if (expiredTime < 0) return "不过期";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(expiredTime));
    }

    public String getRemainingFormatTime() {
        if (expiredTime < 0) return "不过期";
        return (getExpiredTime() - System.currentTimeMillis()) / 1000 / 60 + "分" +
                (getExpiredTime() - System.currentTimeMillis()) / 1000 % 60 + "秒";
    }

    public void resetExpiredTime(long sessionTime) {
        this.expiredTime = System.currentTimeMillis() + sessionTime;
    }
}
