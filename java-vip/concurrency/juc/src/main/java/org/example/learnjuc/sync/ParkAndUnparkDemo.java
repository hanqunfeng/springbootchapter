package org.example.learnjuc.sync;

import java.util.concurrent.locks.LockSupport;


public class ParkAndUnparkDemo {
    public static void main(String[] args) {
        ParkAndUnparkThread myThread = new ParkAndUnparkThread(Thread.currentThread());
        myThread.start();

        System.out.println("before park");
        // 获取许可
        LockSupport.park();
        System.out.println("after park");
    }
}

class ParkAndUnparkThread extends Thread {
    private Object object;

    public ParkAndUnparkThread(Object object) {
        this.object = object;
    }

    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("before unpark");
        // 释放许可
        LockSupport.unpark((Thread) object);
        System.out.println("after unpark");
    }
}
