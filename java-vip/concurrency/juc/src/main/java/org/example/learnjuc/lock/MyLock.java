package org.example.learnjuc.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author Fox
 */
public class MyLock extends AbstractQueuedSynchronizer {

    /**
     * 尝试加锁
     */
    @Override
    protected boolean tryAcquire(int unused) {
        //cas 加锁  state=0 时可以有一个线程获得锁
        if (compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }

        return false;
    }

    @Override
    protected boolean tryRelease(int unused) {
        //释放锁
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    public void lock() {
        acquire(1);
    }

    public boolean tryLock() {
        return tryAcquire(1);
    }

    public void unlock() {
        release(1);
    }

    public boolean isLocked() {
        return isHeldExclusively();
    }


    public static void main(String[] args) {
        MyLock lock = new MyLock();

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    System.out.println("获得锁:" + Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 解锁
                    lock.unlock();
                    System.out.println("释放锁:" + Thread.currentThread().getName());
                }
            }).start();
        }
    }
}
