package org.example.base.cfdemo.flow;

import java.util.concurrent.CompletableFuture;

/**
 * 类说明：变换类
 */
public class ThenApply {
    public static void main(String[] args) {
        String result = CompletableFuture.supplyAsync(() -> "hello")
                .thenApply(s -> s + " world").join();
        System.out.println(result);
    }
}
