package com.lee.session;

/**
 * Author: Lzj
 * Date: 2020-09-16
 * Description:
 */
public interface Session {
    /**
     * 发送start请求给服务器,收到响应后，开始session，
     * @return
     */
    String start();

    /**
     * 发送stop请求给服务器,收到响应后，关闭session
     * @return
     */
    String stop();
}
