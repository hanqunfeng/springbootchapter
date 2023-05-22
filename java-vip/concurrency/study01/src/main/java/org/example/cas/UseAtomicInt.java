package org.example.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *类说明：演示基本类型的原子操作类
 */
public class UseAtomicInt {
    static AtomicInteger ai = new AtomicInteger(10);

    public static void main(String[] args) {
        ai.getAndIncrement();
        final int i = ai.incrementAndGet();
        System.out.println(ai.compareAndSet(i, 10));
        ai.addAndGet(24);

    }
}
