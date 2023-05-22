package org.example.learnjuc.sync;

import java.util.concurrent.Exchanger;

public class ExchangerDemo {
    private static Exchanger exchanger = new Exchanger();
    static String goods = "电脑";
    static String money = "$4000";
    public static void main(String[] args) throws InterruptedException {

        System.out.println("准备交易，一手交钱一手交货...");
        // 卖家
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("卖家到了，已经准备好货：" + goods);
                try {
                    String money = (String) exchanger.exchange(goods);
                    System.out.println("卖家收到钱：" + money);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(3000);

        // 买家
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("买家到了，已经准备好钱：" + money);
                    String goods = (String) exchanger.exchange(money);
                    System.out.println("买家收到货：" + goods);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
