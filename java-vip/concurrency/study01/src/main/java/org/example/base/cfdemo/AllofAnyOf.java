package org.example.base.cfdemo;

import org.example.tools.SleepTools;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 类说明：allOf和anyOf的区别
 */
public class AllofAnyOf {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Random rand = new Random();
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000 + rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("future1完成");
            return 100;
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000 + rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("future2完成");
            return "abc";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000 + rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("future3完成");
            return "123abc";
        });
        CompletableFuture.allOf(future1,future2,future3).thenRun(()->{
            System.out.println("All done!");
        });

//        CompletableFuture<Object> f = CompletableFuture.anyOf(future1,future2,future3);
//        System.out.println(f.get());

        SleepTools.second(5);
    }
}
