package com.lee.session;

import com.lee.SessionManager;

/**
 * Author: Lzj
 * Date: 2020-09-16
 * Description:
 */
public interface Session {
    // 空闲的session
    int IDLE = 0;
    // 有任务运行的session
    int RUN = 1;

    /**
     * 发送start请求给服务器,收到响应后，开始session，
     *
     */
    void start();

    /**
     * 发送stop请求给服务器,收到响应后，关闭session
     *
     */
    void stop();

    /**
     * 注册相应的管理器
     *
     * @param sessionManager
     */
    void registerManager(SessionManager sessionManager);

}
