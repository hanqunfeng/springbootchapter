package org.example.learnjuc.designpattern.producerconsumer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserServiceDemo {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        UserServiceDemo userService = new UserServiceDemo();

        //串行
        executorService.submit(() -> {
            try {
                // 发送邮件
                userService.sendEmail("xxxx@163.com");
                // 发送短信
                userService.sendSMS("186xxxxxxxx");

                System.out.println("用户注册成功");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        });

        //并行
        //创建异步任务用于发送邮件
//        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
//            try {
//                // 发送邮件
//                userService.sendEmail("xxxx@163.com");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        //创建异步任务用于发送短信
//        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
//            try {
//                // 发送短信
//                userService.sendSMS("186xxxxxxxx");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//         //等待所有异步任务执行完成
//        CompletableFuture.allOf(future1, future2).join();
//        System.out.println("用户注册成功");


    }

    private void sendEmail(String email) throws Exception {
        // 发送邮件
        System.out.println("发送邮件 " + email);
        Thread.sleep(3000);
        System.out.println("邮件发送成功");
    }

    private void sendSMS(String mobile) throws Exception {
        // 发送短信
        System.out.println("发送短信 " + mobile);
        Thread.sleep(4000);
        System.out.println("短信发送成功");
    }
}
