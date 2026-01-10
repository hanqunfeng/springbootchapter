package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

/**
 * 事务测试
 * Created by hanqf on 2026/1/10 21:16.
 */

@SpringBootTest
public class TransactionTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testTransaction() {
        String key = "stock:1001";

        // 初始化库存
        redisTemplate.opsForValue().set(key, 10);

        // 开启事务支持
//        redisTemplate.setEnableTransactionSupport(true);

        // 执行事务操作
        List<Object> result = redisTemplate.execute(new SessionCallback<>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                try {
                    // 监视key的变化
                    operations.watch(key);

                    // 安全获取当前库存值
                    Object stock = operations.opsForValue().get(key);

                    // 检查库存是否充足
                    if (stock == null || (Integer) stock <= 0) {
                        operations.unwatch(); // 取消监视
                        System.out.println("库存不足，无法扣减");
                        return null;
                    }

                    // 开始事务，这里要注意，multi()方法后面不能有 read 操作
                    operations.multi();
                    operations.opsForValue().decrement(key);

                    // 执行事务并返回结果
                    List<Object> execResult = operations.exec();
                    System.out.println("事务执行成功，扣减库存后结果: " + execResult);
                    return execResult;

                } catch (Exception e) {
                    // 发生异常时取消监视
                    operations.unwatch();
                    System.err.println("事务执行异常: " + e.getMessage());
                    throw new DataAccessException("Redis事务执行失败", e) {
                    };
                }
            }
        });

        if (result != null) {
            System.out.println("最终事务结果: " + result);
        } else {
            System.out.println("事务未执行或执行失败");
        }
    }


}
