package com.lee;

import com.lee.session.HttpSession;

/**
 * Author: Lzj
 * Date: 2020-09-18
 * Description:
 */
public class RestfulService {

    public String startSession(HttpSession httpSession) {
        return new StartActionType("http://127.0.0.1:8081/nbi/deliverysession?id=" + httpSession.getId(), httpSession).action();
    }

    public String stopSession(HttpSession httpSession) {
        return new StopActionType("http://127.0.0.1:8081/nbi/deliverysession?id=" + httpSession.getId(), httpSession).action();
    }
}
