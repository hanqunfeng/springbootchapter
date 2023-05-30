package org.example.learnjuc.threadPool;

import java.util.Vector;
import java.util.concurrent.*;

public class Application {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        Thread.sleep(10*1000);

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU核数 " + cores);

        int requestNum = 100;
        Vector<Long> wholeTimeList = new Vector<Long>();
        Vector<Long> runTimeList = new Vector<Long>();

        for (int i = 0; i < requestNum; i++) {
            threadPool.execute(new CPUTask(runTimeList, wholeTimeList));
//            threadPool.execute(new IOTask(runTimeList, wholeTimeList));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(20, TimeUnit.SECONDS);

        long wholeTime = 0;
        for (int i = 0; i < wholeTimeList.size(); i++) {
            wholeTime = wholeTimeList.get(i) + wholeTime;
        }

        long runTime = 0;
        for (int i = 0; i < runTimeList.size(); i++) {
            runTime = runTimeList.get(i) + runTime;
        }

        System.out.println("平均每个线程整体花费时间： " + wholeTime / wholeTimeList.size());
        System.out.println("平均每个线程执行花费时间： " + runTime / runTimeList.size());
    }
}
