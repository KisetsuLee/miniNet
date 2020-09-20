package com.lee.manager;

import com.lee.Server;
import com.lee.session.Managers;
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
public class SessionManagerTest {
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
    void testStopSession() throws ExecutionException, InterruptedException {
        Future<?> session = sessionManager.createSession(200, 5000);
        Assertions.assertNull(session.get());
        Assertions.assertEquals(1, sessionManager.getSessionCount());
        TimeUnit.SECONDS.sleep(10);
        Assertions.assertEquals(0, sessionManager.getSessionCount());
    }
}
