package com.lee;

import com.lee.session.HttpSession;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class RestfulService {

    public String startSession(String id, HttpSession httpSession) {
        return new StartActionType("http://127.0.0.1:8081/nbi/deliverysession?id=" + id, httpSession).action();
    }

    public String stopSession(String id, HttpSession httpSession) {
        return new StopActionType("http://127.0.0.1:8081/nbi/deliverysession?id=" + id, httpSession).action();
    }
}
