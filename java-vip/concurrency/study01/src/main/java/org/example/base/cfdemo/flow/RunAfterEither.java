package org.example.base.cfdemo.flow;


import org.example.tools.SleepTools;

import java.util.concurrent.CompletableFuture;

/**
 * 类说明：取最快运行后执行类
 */
public class RunAfterEither {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            SleepTools.second(2);
            return "s1";
        }).runAfterEither(CompletableFuture.supplyAsync(() -> {
            SleepTools.second(1);
            return "s2";
        }), () -> System.out.println("hello world"));
        SleepTools.second(3);
    }
}
