package org.example.learnjuc.designpattern.producerconsumer;


import java.util.concurrent.*;

public class UserService {

    //构建阻塞队列，存储消息
    private static final BlockingQueue<User> queue = new LinkedBlockingQueue<>(100);
    //构建消费者线程池
    private static final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();
    static {
        start();
    }

    public static void main(String[] args) throws InterruptedException {
//        User user = new User(1,"fox","186xxxxxxxx","xxxx@163.com");
//        // 将用户信息放入队列
//        queue.put(user);
//        System.out.println("用户注册成功");
    }


    public static void start() {
        // 启动消费者
        consumerExecutor.submit(()->{
            while (true) {
                try {
                    User user = queue.poll(1, TimeUnit.SECONDS);
                    // 处理业务逻辑
                    sendEmail(user.getEmail());
                    sendSMS(user.getMobile());
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static void sendEmail(String email) throws Exception {
        // 发送邮件
        System.out.println("发送邮件 " + email);
        Thread.sleep(3000);
        System.out.println("邮件发送成功");
    }

    private static void sendSMS(String mobile) throws Exception {
        // 发送短信
        System.out.println("发送短信 " + mobile);
        Thread.sleep(4000);
        System.out.println("短信发送成功");
    }


}
