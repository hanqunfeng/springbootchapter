package org.example.base.cfdemo.flow;


import org.example.tools.SleepTools;

import java.util.concurrent.CompletableFuture;

/**
 * 类说明：取最快转换类
 */
public class ApplyToEither {
    public static void main(String[] args) {
        String result = CompletableFuture.supplyAsync(() -> {
            SleepTools.second(1);
            return "s1";
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            SleepTools.second(2);
            return "hello world";
        }), s -> s).join();
        System.out.println(result);
    }
}
