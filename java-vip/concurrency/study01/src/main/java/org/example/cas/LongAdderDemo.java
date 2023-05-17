package org.example.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 类说明：LongAdder,原子类以及同步锁性能测试
 */
public class LongAdderDemo {
    private static final int MAX_THREADS = 20;
    private static final int TASK_COUNT = 400;
    private static final int TARGET_COUNT = 100000000;

    /*三个不同类型的long有关的变量*/
    private AtomicLong acount = new AtomicLong(0L);
    private LongAdder lacount = new LongAdder();
    private long count = 0;

    /*控制线程同时进行*/
    private static CountDownLatch cdlsync = new CountDownLatch(TASK_COUNT);
    private static CountDownLatch cdlatomic = new CountDownLatch(TASK_COUNT);
    private static CountDownLatch cdladdr = new CountDownLatch(TASK_COUNT);


    /*普通long的同步锁测试方法*/
    protected synchronized long inc() {
        return ++count;
    }

    protected synchronized long getCount() {
        return count;
    }

    /*普通long的同步锁测试任务*/
    public class SyncTask implements Runnable {
        protected String name;
        protected long starttime;
        LongAdderDemo out;

        public SyncTask(long starttime, LongAdderDemo out) {
            this.starttime = starttime;
            this.out = out;
        }

        @Override
        public void run() {
            long v = out.getCount();
            while (v < TARGET_COUNT) {
                v = out.inc();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("SyncTask spend:" + (endtime - starttime) + "ms" );
            cdlsync.countDown();
        }
    }

    /*普通long的执行同步锁测试*/
    public void testSync() throws InterruptedException {
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long starttime = System.currentTimeMillis();
        SyncTask sync = new SyncTask(starttime, this);
        for (int i = 0; i < TASK_COUNT; i++) {
            exe.submit(sync);
        }
        cdlsync.await();
        exe.shutdown();
    }

    /*原子型long的测试任务*/
    public class AtomicTask implements Runnable {
        protected String name;
        protected long starttime;

        public AtomicTask(long starttime) {
            this.starttime = starttime;
        }

        @Override
        public void run() {
            long v = acount.get();
            while (v < TARGET_COUNT) {
                v = acount.incrementAndGet();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("AtomicTask spend:" + (endtime - starttime) + "ms" );
            cdlatomic.countDown();
        }
    }


    /*原子型long的执行测试*/
    public void testAtomic() throws InterruptedException {
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long starttime = System.currentTimeMillis();
        AtomicTask atomic = new AtomicTask(starttime);
        for (int i = 0; i < TASK_COUNT; i++) {
            exe.submit(atomic);
        }
        cdlatomic.await();
        exe.shutdown();
    }

    /*LongAdder的测试任务*/
    public class LongAdderTask implements Runnable {
        protected String name;
        protected long startTime;

        public LongAdderTask(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void run() {
            long v = lacount.sum();
            while (v < TARGET_COUNT) {
                lacount.increment();
                v = lacount.sum();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("LongAdderTask spend:" + (endtime - startTime) + "ms");
            cdladdr.countDown();
        }

    }

    /*LongAdder的执行测试*/
    public void testLongAdder() throws InterruptedException {
        ExecutorService exe = Executors.newFixedThreadPool(MAX_THREADS);
        long startTime = System.currentTimeMillis();
        LongAdderTask longAdderTask = new LongAdderTask(startTime);
        for (int i = 0; i < TASK_COUNT; i++) {
            exe.submit(longAdderTask);
        }
        cdladdr.await();
        exe.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        LongAdderDemo demo = new LongAdderDemo();
        //demo.testSync();
        //demo.testAtomic();
        //demo.testLongAdder();
    }
}
