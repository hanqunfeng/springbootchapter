package org.example.base.cfdemo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 类说明：get和join方法的区别
 */
public class JoinAndGet {
    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
//           int i = 1/0;
           return 100;
        });
        //join不用捕获异常
        System.out.println(future.join());

        //get需要捕获异常
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
