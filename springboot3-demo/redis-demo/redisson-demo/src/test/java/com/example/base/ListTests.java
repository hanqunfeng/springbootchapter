package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * List — Redisson 操作对象与代码示例
 * Created by hanqf on 2026/1/9 17:35.
 */

@SpringBootTest
public class ListTests {
    @Autowired
    private RedissonClient redisson;

    /**
     * RList<V> —— 分布式 List
     * <p>
     * 对应 Redis：List
     * 语义：Java List
     * 适用场景: 缓存列表、消息队列
     */
    @Test
    void testRList() {
        RList<String> list = redisson.getList("list:demo", StringCodec.INSTANCE);
        list.clear();

        // add -> RPUSH list:demo A B C
        list.add("A");
        list.add("B");
        list.add("C");

        // get(index) -> LINDEX list:demo 0
        System.out.println(list.get(0));   // A

        // range -> LRANGE list:demo 0 -1
        System.out.println(list.readAll()); // [A, B, C]

        // remove -> LREM list:demo 0 B
        list.remove("B");

        // size -> LLEN list:demo
        System.out.println("size = " + list.size());

    }

    /**
     * RQueue<V> —— FIFO 队列
     * <p>
     * 基于 Redis List
     * 单向入队 / 出队
     */
    @Test
    void testRQueue() {
        RQueue<String> queue = redisson.getQueue("queue:demo", StringCodec.INSTANCE);

        // 入队 -> RPUSH
        queue.offer("task1");
        queue.offer("task2");

        // 出队 -> LPOP
        String task = queue.poll();
        System.out.println(task);

    }

    /**
     * RDeque<V> —— 双端队列
     * <p>
     * 支持头尾双向操作
     */
    @Test
    void testRDeque() {
        RDeque<String> deque = redisson.getDeque("deque:demo", StringCodec.INSTANCE);

        // 头部插入 -> LPUSH
        deque.addFirst("A");

        // 尾部插入 -> RPUSH
        deque.addLast("B");

        // 头部弹出 -> LPOP
        System.out.println(deque.pollFirst());

        // 尾部弹出 -> RPOP
        System.out.println(deque.pollLast());
    }


    /**
     * RBlockingQueue<V> —— 阻塞队列
     * <p>
     * 支持阻塞消费
     * 适用于简单 Worker 模型
     */
    @Test
    void testRBlockingQueue() {
        RBlockingQueue<String> queue = redisson.getBlockingQueue("queue:block", StringCodec.INSTANCE);

        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                System.out.println("Producer: 开始生产任务");
                TimeUnit.SECONDS.sleep(2); // 模拟生产耗时
                queue.put("task1");  // 使用put而不是add，更安全
                System.out.println("Producer: 已添加任务 task1");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                System.out.println("Consumer: 等待消费任务");
                String task = queue.take();  // 阻塞等待
                System.out.println("Consumer: 消费了任务 " + task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 启动线程
        consumer.start();
        producer.start();

        // 等待线程完成
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
