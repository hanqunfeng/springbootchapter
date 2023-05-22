package org.example.learnjuc.sync;

import lombok.SneakyThrows;


import java.util.concurrent.CountDownLatch;


public class CountDownLatchDemo {
    // begin 代表裁判 初始为 1
    private static CountDownLatch begin = new CountDownLatch(1);

    // end 代表玩家 初始为 8
    private static CountDownLatch end = new CountDownLatch(8);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 1; i <= 8; i++) {
            new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    // 预备状态
                    System.out.println("参赛者"+Thread.currentThread().getName()+ "已经准备好了");
                    // 等待裁判吹哨
                    begin.await();
                    // 开始跑步
                    System.out.println("参赛者"+Thread.currentThread().getName() + "开始跑步");
                    Thread.sleep(3000);
                    // 跑步结束, 跑完了
                    System.out.println("参赛者"+Thread.currentThread().getName()+ "到达终点");
                    // 跑到终点, 计数器就减一
                    end.countDown();
                }
            }).start();
        }
        // 等待 5s 就开始吹哨
        Thread.sleep(5000);
        System.out.println("开始比赛");
        // 裁判吹哨, 计数器减一
        begin.countDown();
        // 等待所有玩家到达终点
        end.await();
        System.out.println("比赛结束");

    }

}
