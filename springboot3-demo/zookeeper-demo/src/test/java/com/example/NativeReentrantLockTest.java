package com.example;

/**
 * 原生 ZooKeeper 实现的可重入分布式互斥锁 测试用例
 * Created by hanqf on 2025/9/17 16:39.
 */

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class NativeReentrantLockTest {

    private static final String CONNECT_STRING = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT = 10000;
    private static final String LOCK_BASE_PATH = "/locks/testReentrantSingle";
    private ZooKeeper zk;
    private static CountDownLatch connectedSignal = new CountDownLatch(1);

    @BeforeEach
    public void setUp() throws Exception {
        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
                System.out.println("连接建立");
            }
        });
        System.out.println("等待连接建立...");
        connectedSignal.await();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (zk != null) zk.close();
    }

    @Test
    public void testReentrantBehaviorSingleThread() throws Exception {
        NativeReentrantLock lock = new NativeReentrantLock(zk, LOCK_BASE_PATH);

        // 第一次 acquire，获得锁
        assertTrue(lock.acquire(2, TimeUnit.SECONDS));
        assertTrue(lock.isHeldByCurrentClient());

        // 再次 acquire，重入（不再创建新节点），计数+1
        assertTrue(lock.acquire(1, TimeUnit.SECONDS));
        assertTrue(lock.isHeldByCurrentClient());

        // release 一次后仍然持有
        lock.release();
        assertTrue(lock.isHeldByCurrentClient());

        // release 第二次后真正释放
        lock.release();
        assertFalse(lock.isHeldByCurrentClient());
    }

    @Test
    public void testMutualExclusionBetweenThreads() throws Exception {
        final int THREADS = 5;
        ExecutorService es = Executors.newFixedThreadPool(THREADS);
        CountDownLatch startLatch = new CountDownLatch(THREADS);
        CountDownLatch finishLatch = new CountDownLatch(THREADS);

        // 用来记录同一时间持有锁的线程计数，验证互斥性
        final AtomicIntegerHolder concurrentHolders = new AtomicIntegerHolder();

        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            futures.add(es.submit(() -> {
                // ZooKeeper 的所有操作都是基于**会话（session）**的，一个 ZooKeeper client 实例对应一个会话。
                // 这个会话里所有创建的 ephemeral（临时）节点，都会在会话结束（断开或超时）时被自动删除。
                // 如果使用一个会话来模拟多个客户端，它们创建的临时节点在 ZooKeeper 看起来都是“一个客户端”的节点，锁的互斥性会被破坏。
                ZooKeeper zkLocal = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, ev -> {});
                NativeReentrantLock lockLocal = new NativeReentrantLock(zkLocal, LOCK_BASE_PATH);
                startLatch.countDown();
                startLatch.await();

                // 每个线程尝试获取锁，拿到后 sleep 200ms 模拟临界区
                boolean got = lockLocal.acquire(5, TimeUnit.SECONDS);
                if (!got) {
                    zkLocal.close();
                    finishLatch.countDown();
                    return false;
                }
                try {
                    // 进入临界区：并发持有计数++，校验互斥（应该始终为1）
                    int cur = concurrentHolders.incrementAndGet();
                    if (cur != 1) {
                        // 互斥被破坏
                        System.err.println("Mutual exclusion violated, concurrent holders=" + cur);
                    }
                    // 保证在临界区停留一段时间
                    Thread.sleep(200);
                    concurrentHolders.decrementAndGet();
                } finally {
                    lockLocal.release();
                    zkLocal.close();
                }
                finishLatch.countDown();
                return true;
            }));
        }

        // 等待完成（设置较长超时）
        assertTrue(finishLatch.await(30, TimeUnit.SECONDS));
        // 验证所有线程都成功获得并释放锁
        for (Future<Boolean> f : futures) {
            assertTrue(f.get());
        }
        es.shutdownNow();
    }

    // 简单的线程安全整数 holder
    static class AtomicIntegerHolder {
        private final java.util.concurrent.atomic.AtomicInteger v = new java.util.concurrent.atomic.AtomicInteger(0);
        public int incrementAndGet() { return v.incrementAndGet(); }
        public int decrementAndGet() { return v.decrementAndGet(); }
    }
}

