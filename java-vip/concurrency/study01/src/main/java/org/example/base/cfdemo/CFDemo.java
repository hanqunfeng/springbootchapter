package org.example.base.cfdemo;

import org.example.tools.SleepTools;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 类说明：CompletableFuture使用范例
 */
public class CFDemo {

    static class GetResult extends Thread {
        CompletableFuture<Integer> f;
        GetResult(String threadName, CompletableFuture<Integer> f) {
            super(threadName);
            this.f = f;
        }
        @Override
        public void run() {
            try {
                System.out.println("waiting result.....");
                System.out.println(this.getName() + ": " + f.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final CompletableFuture<Integer> f = new CompletableFuture<>();

        new GetResult("Client1", f).start();
        new GetResult("Client2", f).start();
        System.out.println("sleeping");
        SleepTools.second(2);
        //f.complete(100);
        f.completeExceptionally(new Exception());
        System.in.read();
    }
}
