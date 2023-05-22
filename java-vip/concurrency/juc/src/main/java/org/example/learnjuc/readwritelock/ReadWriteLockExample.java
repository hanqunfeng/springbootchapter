package org.example.learnjuc.readwritelock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();//读锁
    private final Lock writeLock = lock.writeLock();//写锁
    private final String[] data = new String[10];

    public void write(int index, String value) {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+"获取写锁");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            read(2);
            data[index] = value;
        } finally {
            System.out.println(Thread.currentThread().getName()+"释放写锁");
            writeLock.unlock();
        }
    }

    public String read(int index) {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+"获取读锁");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return data[index];
        } finally {
            System.out.println(Thread.currentThread().getName()+"释放读锁");
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        ReadWriteLockExample rwl = new ReadWriteLockExample();
        // 测试读读，读写，写写场景
        new Thread(()->{
            //rwl.read(2);
            rwl.write(2,"rwl");
          //  rwl.read(2);
        }).start();
//        new Thread(()->rwl.read(2)).start();
//        new Thread(()->rwl.write(2,"rwl")).start();
//        new Thread(()->rwl.write(2,"rwl")).start();


    }
}
