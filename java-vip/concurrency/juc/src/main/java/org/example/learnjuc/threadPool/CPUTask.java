package org.example.learnjuc.threadPool;

import java.util.List;

/**
 * 作者：周瑜大都督
 */

public class CPUTask implements Runnable {

    //整体执行时间，包括在队列中等待的时间
    List<Long> wholeTimeList;
    //真正执行时间
    List<Long> runTimeList;

    private long initStartTime = 0;


    public CPUTask(List<Long> runTimeList, List<Long> wholeTimeList) {
        initStartTime = System.currentTimeMillis();
        this.runTimeList = runTimeList;
        this.wholeTimeList = wholeTimeList;
    }

    public boolean isPrime(final int number) {
        if (number <= 1)
            return false;


        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0)
                return false;
        }
        return true;
    }


    public int countPrimes(final int lower, final int upper) {
        int total = 0;
        for (int i = lower; i <= upper; i++) {
            if (isPrime(i))
                total++;
        }
        return total;
    }

    public void run() {
        long start = System.currentTimeMillis();

        countPrimes(1, 1000000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();

        long wholeTime = end - initStartTime;
        long runTime = end - start;
        wholeTimeList.add(wholeTime);
        runTimeList.add(runTime);
        System.out.println("单个线程运行时间：" + runTime);
    }
}
