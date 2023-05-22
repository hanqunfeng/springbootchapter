package org.example.learnjuc.readwritelock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class TulingReadWriteLock implements ReadWriteLock {

    private final ReadLock readLock= new ReadLock();
    private final WriteLock writeLock= new WriteLock();



    private final static Sync sync = new Sync();


    @Override
    public Lock readLock() {


        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    public static class ReadLock implements Lock{
        public void lock() {
            sync.acquireShared(1);
        }

        public void unlock() {
            sync.releaseShared(1);
        }

        //TODO
        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }


        @Override
        public Condition newCondition() {
            return null;
        }
    }

    public static class WriteLock implements Lock{
        public void lock() {
            sync.acquire(1);
        }

        public void unlock() {
            sync.release(1);
        }

        //TODO
        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }


    private static class Sync extends AbstractQueuedSynchronizer {
        private int readers = 0;
        private int writers = 0;

        protected boolean tryAcquire(int arg) {
            int state = getState();
            Thread currentThread = Thread.currentThread();
            if (readers > 0 && getExclusiveOwnerThread() != currentThread) {
                // 有读锁且当前线程不是持有写锁的线程，不能获取写锁   读写互斥
                return false;
            } else if (writers == 0) {
                // 无读锁和写锁，尝试获取写锁
                if(compareAndSetState(state, state + arg)){
                    setExclusiveOwnerThread(currentThread);
                    writers++;
                    return true;
                }
                return false;
            } else if (getExclusiveOwnerThread() == currentThread) {
                // 当前线程已经持有写锁，增加写锁计数器
                setState(state + arg);
                return true;
            } else {
                // 有读锁或者其他线程持有写锁，不能获取写锁
                return false;
            }
        }

        protected boolean tryRelease(int arg) {
            int state = getState() - arg;
            if (getExclusiveOwnerThread() != Thread.currentThread()) {
                // 当前线程不持有写锁，不能释放写锁
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (writers == 1) {
                // 写锁计数器减一，释放锁
                writers--;
                free = true;
                setExclusiveOwnerThread(null);
            }

            setState(state);
            return free;
        }

        protected int tryAcquireShared(int arg) {
            int state = getState();
            Thread currentThread = Thread.currentThread();
            if (writers > 0 && getExclusiveOwnerThread() != currentThread) {
                // 有写锁且当前线程不是持有写锁的线程，不能获取读锁
                return -1;
            } else if (readers == 0) {
                // 无读锁，尝试获取读锁
                if (compareAndSetState(state, state + arg)) {
                    // 获取读锁成功，读锁计数器增加
                    readers++;
                    return 1;
                }
            } else if (getExclusiveOwnerThread() == currentThread) {
                // 当前线程持有写锁，可以获取读锁
                if (compareAndSetState(state, state + arg)) {
                    // 获取读锁成功，读锁计数器增加
                    readers++;
                    return 1;
                }
            } else {
                // 已经有读锁，可以获取读锁
                if (compareAndSetState(state, state + arg)) {
                    // 获取读锁成功，读锁计数器增加
                    readers++;
                    return 1;
                }
            }
            // 获取读锁失败
            return -1;
        }

        protected boolean tryReleaseShared(int arg) {
            int state = getState() - arg;
            if (readers == 0) {
                // 读锁计数器已经为0，不能再释放读锁
                throw new IllegalMonitorStateException();
            }
            readers--;
            setState(state);
            return true;
        }
    }


}



