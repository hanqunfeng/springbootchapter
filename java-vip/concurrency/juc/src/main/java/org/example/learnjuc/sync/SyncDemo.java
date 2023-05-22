package org.example.learnjuc.sync;




import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncDemo {

    private static int counter = 0;
    private final static Lock lock = new ReentrantLock();

    public static void increment() {
        //lock.lock();
        //synchronized (SyncDemo.class){
//        UnsafeFactory.getUnsafe().monitorEnter(lock);
        try {
            counter++;
        }finally {
            //lock.unlock();
//            UnsafeFactory.getUnsafe().monitorExit(lock);
        }

        //}
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                increment();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                increment();
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        //思考： counter=？
        System.out.println(counter);
    }
}
