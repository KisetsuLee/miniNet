package com.lee.manager;

import com.lee.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lee
 * @Date 2020/9/19
 */
public class TestSessionManager {
    private final SessionManager sessionManager = Managers.newFixedSessionManager(10);

    @BeforeAll
    static void startServer() {
        Server.main(null);
    }

    @Test
    void testCreateSession() throws InterruptedException, ExecutionException {
        Future<?> session = sessionManager.createSession(100);
        Assertions.assertNull(session.get());
        Assertions.assertEquals(1, sessionManager.getSessionCount());
    }

    @Test
    void testConcurrencyCreateSession() throws ExecutionException, InterruptedException {
        List<Future> list = new ArrayList<>();
        sessionManager.setConcurrency(20);
        for (int i = 0; i < 20; i++) {
            list.add(sessionManager.createSession(i));
        }
        for (Future future : list) {
            future.get();
        }
        Assertions.assertEquals(20, sessionManager.getSessionCount());
    }

    @Test
    void testRemoveSession() throws ExecutionException, InterruptedException {
        Future<?> session = sessionManager.createSession(200, 5000);
        session.get();
        sessionManager.removeSession("200");
    }

    @Test
    void testDelayStopSession() throws ExecutionException, InterruptedException {
        Future<?> session = sessionManager.createSession(300, 2000);
        Assertions.assertNull(session.get());
        Assertions.assertEquals(1, sessionManager.getSessionCount());
        TimeUnit.SECONDS.sleep(5);
        Assertions.assertEquals(0, sessionManager.getSessionCount());
    }

    @Test
    void testResetExpiredTime() throws ExecutionException, InterruptedException {
        Future<?> session = sessionManager.createSession(400, 2000);
        session.get();
        Assertions.assertEquals(1, sessionManager.getSessionCount());
        sessionManager.resetSessionExpiredTime("400", 5000);
        TimeUnit.SECONDS.sleep(2);
        Assertions.assertEquals(1, sessionManager.getSessionCount());
        TimeUnit.SECONDS.sleep(8);
        Assertions.assertEquals(0, sessionManager.getSessionCount());
    }

    @Test
    void testShutDown() throws ExecutionException, InterruptedException {
        Future<?> session = sessionManager.createSession(500, 2000);
        session.get();
        Assertions.assertEquals(1, sessionManager.getSessionCount());
        sessionManager.shutdown();
        TimeUnit.SECONDS.sleep(3);
        Assertions.assertEquals(0, sessionManager.getSessionCount());
    }
}
