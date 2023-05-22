package org.example.learnjuc.sync;

import java.util.Random;
import java.util.concurrent.Phaser;

public class PhaserDemo {
    public static void main(String[] args) {
        final Phaser phaser = new Phaser() {
            //重写该方法来增加阶段到达动作
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                // 参与者数量，去除主线程
                int staffs = registeredParties - 1;
                switch (phase) {
                    case 0:
                        System.out.println("大家都到公司了，出发去公园，人数：" + staffs);
                        break;
                    case 1:
                        System.out.println("大家都到公园门口了，出发去餐厅，人数：" + staffs);
                        break;
                    case 2:
                        System.out.println("大家都到餐厅了，开始用餐，人数：" + staffs);
                        break;

                }

                // 判断是否只剩下主线程（一个参与者），如果是，则返回true，代表终止
                return registeredParties == 1;
            }
        };

        // 注册主线程 ———— 让主线程全程参与
        phaser.register();
        final StaffTask staffTask = new StaffTask();

        // 3个全程参与团建的员工
        for (int i = 0; i < 3; i++) {
            // 添加任务数
            phaser.register();
            new Thread(() -> {
                try {
                    staffTask.step1Task();
                    //到达后等待其他任务到达
                    phaser.arriveAndAwaitAdvance();

                    staffTask.step2Task();
                    phaser.arriveAndAwaitAdvance();

                    staffTask.step3Task();
                    phaser.arriveAndAwaitAdvance();

                    staffTask.step4Task();
                    // 完成了，注销离开
                    phaser.arriveAndDeregister();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 两个不聚餐的员工加入
        for (int i = 0; i < 2; i++) {
            phaser.register();
            new Thread(() -> {
                try {
                    staffTask.step1Task();
                    phaser.arriveAndAwaitAdvance();

                    staffTask.step2Task();
                    System.out.println("员工【" + Thread.currentThread().getName() + "】回家了");
                    // 完成了，注销离开
                    phaser.arriveAndDeregister();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        while (!phaser.isTerminated()) {
            int phase = phaser.arriveAndAwaitAdvance();
            if (phase == 2) {
                // 到了去餐厅的阶段，又新增4人，参加晚上的聚餐
                for (int i = 0; i < 4; i++) {
                    phaser.register();
                    new Thread(() -> {
                        try {
                            staffTask.step3Task();
                            phaser.arriveAndAwaitAdvance();

                            staffTask.step4Task();
                            // 完成了，注销离开
                            phaser.arriveAndDeregister();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
    }

    static final Random random = new Random();

    static class StaffTask {
        public void step1Task() throws InterruptedException {
            // 第一阶段：来公司集合
            String staff = "员工【" + Thread.currentThread().getName() + "】";
            System.out.println(staff + "从家出发了……");
            Thread.sleep(random.nextInt(5000));
            System.out.println(staff + "到达公司");
        }

        public void step2Task() throws InterruptedException {
            // 第二阶段：出发去公园
            String staff = "员工【" + Thread.currentThread().getName() + "】";
            System.out.println(staff + "出发去公园玩");
            Thread.sleep(random.nextInt(5000));
            System.out.println(staff + "到达公园门口集合");

        }

        public void step3Task() throws InterruptedException {
            // 第三阶段：去餐厅
            String staff = "员工【" + Thread.currentThread().getName() + "】";
            System.out.println(staff + "出发去餐厅");
            Thread.sleep(random.nextInt(5000));
            System.out.println(staff + "到达餐厅");

        }

        public void step4Task() throws InterruptedException {
            // 第四阶段：就餐
            String staff = "员工【" + Thread.currentThread().getName() + "】";
            System.out.println(staff + "开始用餐");
            Thread.sleep(random.nextInt(5000));
            System.out.println(staff + "用餐结束，回家");
        }
    }
}

