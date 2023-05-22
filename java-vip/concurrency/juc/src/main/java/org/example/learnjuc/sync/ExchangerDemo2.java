package org.example.learnjuc.sync;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangerDemo2 {

    private static final Exchanger<String> exchanger = new Exchanger();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String A = "12379871924sfkhfksdhfks";
                    exchanger.exchange(A);
                } catch (InterruptedException e) {
                }
            }
        });

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String B = "32423423jknjkfsbfj";
                    String A = exchanger.exchange(B);
                    System.out.println("A和B数据是否一致：" + A.equals(B));
                    System.out.println("A= "+A);
                    System.out.println("B= "+B);
                } catch (InterruptedException e) {
                }
            }
        });

        threadPool.shutdown();

    }
}
