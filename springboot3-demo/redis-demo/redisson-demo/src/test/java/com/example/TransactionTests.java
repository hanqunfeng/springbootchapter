package com.example;

import org.junit.jupiter.api.Test;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 事务测试
 * Created by hanqf on 2026/1/10 21:16.
 */

@SpringBootTest
public class TransactionTests {

    @Autowired
    private RedissonClient redisson;

    @Test
    void testTransaction() {
        String key = "stock:1001";

        // 初始化库存
        redisson.getBucket(key).set(10);

        RTransaction tx = redisson.createTransaction(TransactionOptions.defaults());
        try {
            // 在事务内部获取当前值，避免并发问题
            Object stockObj = tx.getBucket(key).get();

            // 类型安全检查
            int currentStock;
            if (stockObj instanceof Integer) {
                currentStock = (Integer) stockObj;
            } else {
                System.out.println("库存数据类型错误或不存在");
                tx.rollback();
                return;
            }

            if (currentStock <= 0) {
                System.out.println("库存不足，无法扣减");
                tx.rollback();
                return;
            }

            // 扣减库存
            tx.getBucket(key).set(currentStock - 1);
            tx.commit();
            System.out.println("库存扣减成功，当前库存：" + (currentStock - 1));

        } catch (Exception e) {
            System.err.println("事务执行失败，正在回滚: " + e.getMessage());
            try {
                tx.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("事务回滚失败: " + rollbackEx.getMessage());
            }
        }
    }
}
