package org.example.tl;

import org.example.tools.SleepTools;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Mark老师
 * 类说明：
 */
public class ThreadLocalMemoryLeak {
    private static final int TASK_LOOP_SIZE = 500;

    /*线程池*/
    final static ThreadPoolExecutor poolExecutor
            = new ThreadPoolExecutor(5, 5, 1,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());

    static class LocalVariable {
        private byte[] a = new byte[1024*1024*5];/*5M大小的数组*/
    }

    ThreadLocal<LocalVariable> threadLocalLV;

    public static void main(String[] args) throws InterruptedException {
        SleepTools.ms(4000);
        for (int i = 0; i < TASK_LOOP_SIZE; ++i) {
            poolExecutor.execute(new Runnable() {
                public void run() {
                    SleepTools.ms(500);
//
//                    LocalVariable localVariable = new LocalVariable();
//
//
//                    ThreadLocalMemoryLeak oom = new ThreadLocalMemoryLeak();
//                    oom.threadLocalLV = new ThreadLocal<>();
//                    oom.threadLocalLV.set(new LocalVariable());
//
//                   oom.threadLocalLV.remove();

                    System.out.println("use local varaible");

                }
            });

            SleepTools.ms(100);
        }
        System.out.println("pool execute over");
    }

}
