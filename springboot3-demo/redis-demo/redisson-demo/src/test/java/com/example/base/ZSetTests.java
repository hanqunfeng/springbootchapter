package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

/**
 * ZSet — Redisson 操作对象与代码示例
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class ZSetTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RScoredSortedSet<V> —— 带 score 的有序集合（排行榜）
     * <p>
     * 对应 Redis：ZSet
     * 按 score 排序（升序）
     */
    @Test
    void testRScoredSortedSet() {
        RScoredSortedSet<String> leaderboard =
                redisson.getScoredSortedSet("rank:game");

        // 添加元素: ZADD rank:game 100 Alice
        leaderboard.add(100.0, "Alice");
        leaderboard.add(95.5, "Bob");
        leaderboard.add(120.0, "Carol");

        // 查询排名（score 小在前）: ZRANGE rank:game 0 -1 WITHSCORES
        System.out.println("first = " + leaderboard.first());
        System.out.println("last  = " + leaderboard.last());

        // 获取指定区间: ZRANGE rank:game 0 1 WITHSCORES
        Collection<String> top2 =
                leaderboard.valueRange(0, 1);
        System.out.println(top2);

        // 获取分数: ZSCORE rank:game Alice
        Double score = leaderboard.getScore("Alice");
        System.out.println("Alice score = " + score);

    }

    /**
     * RLexSortedSet —— 字典序排序集合
     * <p>
     * 按 member 字典序排序
     * score 固定为 0
     */
    @Test
    void testRLexSortedSet() {
        RLexSortedSet dictSet =
                redisson.getLexSortedSet("dict:words");

        // 添加: ZADD dict:words 0 apple
        dictSet.add("apple");
        dictSet.add("banana");
        dictSet.add("apricot");

        // 字典序遍历: ZRANGE dict:words 0 -1
        System.out.println(dictSet.readAll());

        // 范围查询: ZRANGEBYLEX dict:words [a (b
        Collection<String> range =
                dictSet.range("a", true, "b", false);
        System.out.println(range);   // [apple, apricot]

    }

    /**
     * RPriorityQueue<V> —— 优先队列
     * <p>
     * 最小堆语义（默认）
     * <p>
     * 🔎 内部映射
     * 底层基于 ZSet：
     * score = priority
     * member = encoded value
     */
    @Test
    void testRPriorityQueue() {
        RPriorityQueue<Integer> queue =
                redisson.getPriorityQueue("pq:demo");

        // 入队: ZADD pq:demo 10 "10"
        queue.add(10);
        queue.add(1);
        queue.add(5);

        // 出队（最小值优先）: ZPOPMIN pq:demo
        System.out.println(queue.poll()); // 1
        System.out.println(queue.poll()); // 5
        System.out.println(queue.poll()); // 10

    }

    /**
     * RPriorityDeque<V> —— 双端优先队列
     * <p>
     * 支持从最小或最大优先级弹出
     */
    @Test
    void testRPriorityDeque() {
        RPriorityDeque<Integer> deque =
                redisson.getPriorityDeque("pdq:demo");

        deque.add(10);
        deque.add(1);
        deque.add(5);

        // 最小: pollFirst -> ZPOPMIN
        System.out.println(deque.pollFirst()); // 1

        // 最大: pollLast  -> ZPOPMAX
        System.out.println(deque.pollLast());  // 10

    }


}
