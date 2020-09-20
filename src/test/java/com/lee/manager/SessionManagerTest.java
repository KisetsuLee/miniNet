package com.lee.manager;

import com.lee.Server;
import com.lee.session.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lee
 * @Date 2020/9/19
 */
public class SessionManagerTest {
    private SessionManager sessionManager = Managers.newFixedSessionManager(10);

    @BeforeAll
    static void startServer() {
        Server.main(null);
    }

    @Test
    void test() throws InterruptedException {
        Future<?> session = sessionManager.createSession(100);
        try {
            Assertions.assertNull(session.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(1, sessionManager.getSessionCount());
    }
}
