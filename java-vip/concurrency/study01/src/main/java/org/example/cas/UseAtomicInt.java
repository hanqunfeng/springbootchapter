package org.example.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *类说明：演示基本类型的原子操作类
 */
public class UseAtomicInt {
    static AtomicInteger ai = new AtomicInteger(10);

    public static void main(String[] args) {
        ai.getAndIncrement();
        ai.incrementAndGet();
        //ai.compareAndSet();
        ai.addAndGet(24);
    }
}
