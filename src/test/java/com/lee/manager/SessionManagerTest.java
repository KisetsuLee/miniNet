package com.lee.manager;

import com.lee.session.Managers;
import org.junit.jupiter.api.Test;

/**
 * @Author Lee
 * @Date 2020/9/19
 */
public class SessionManagerTest {
    private SessionManager sessionManager = Managers.newFixedSessionManager(10);

    @Test
    void test() {
        System.out.println(1);
    }
}
