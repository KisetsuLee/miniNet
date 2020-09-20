package com.lee;

import com.lee.manager.Managers;
import com.lee.manager.SessionManager;

import java.util.Scanner;

/**
 * Author: Lzj
 * Date: 2020-09-15
 * Description:
 */
public class Client {

    private static long sessionId = 100000;


    public static void main(String[] args) throws InterruptedException {
        final SessionManager sessionManager = Managers.newFixedSessionManager(10);
        sessionManager.setConcurrency(20);
        // client进入操作
        System.out.println("请输入固定指令指令执行session操作：");
        System.out.println("查看Session: ls");
        System.out.println("添加Session: ");
        System.out.println("1.添加单个session：add sessionId timeout(负数表示不过期） -> add 1000 2000（创建一个id为1000，过期时间为2秒的session）");
        System.out.println("2.并发添加多个session：add session个数 -> add 10（创建10个Session，默认不过期）");
        System.out.println("修改Session过期时间: ");
        System.out.println("modify sessionId timeout -> modify 1000 5000(负数表示不过期)（修改Id为1000的Session过期时间为5秒后）");
        System.out.println("q退出程序，退出前会将所有Session关闭");

        Scanner sc = new Scanner(System.in);
        String line;
        while (true) {
            try {
                line = sc.nextLine();
                if (line.startsWith("add")) {
                    String[] s = line.split(" ");
                    if (s.length == 2) { //并发创建Session
                        int num = Integer.parseInt(s[1]);
                        sessionManager.setConcurrency(num);
                        for (int i = 0; i < num; i++) {
                            sessionManager.createSession(sessionId++);
                        }
                    } else if (s.length == 3) {
                        long id = Long.parseLong(s[1]);
                        long time = Long.parseLong(s[2]);
                        sessionManager.createSession(id, time);
                    } else {
                        System.out.println("请输入正确的add指令");
                    }
                } else if (line.startsWith("modify")) {
                    String[] s = line.split(" ");
                    if (s.length == 3) {
                        String id = s[1];
                        long time = Long.parseLong(line.split(" ")[2]);
                        sessionManager.resetSessionExpiredTime(id, time);
                    } else {
                        System.out.println("请输入正确的modify指令");
                    }
                } else if ("ls".equals(line)) {
                    System.out.println("当前Session个数" + sessionManager.getSessionCount() + "个");
                    sessionManager.getSessionsStatus();
                } else if ("q".equals(line)) {
                    break;
                } else {
                    System.out.println("指令不存在");
                }
            } catch (Exception e) {
                System.out.println("指令错误: " + e.getMessage());
            }
        }
        sessionManager.shutdown();
    }
}
