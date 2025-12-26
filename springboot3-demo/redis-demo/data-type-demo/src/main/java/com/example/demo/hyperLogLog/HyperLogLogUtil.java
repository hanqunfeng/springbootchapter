package com.example.demo.hyperLogLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * Created by hanqf on 2025/12/19 10:29.
 */

@Component
public class HyperLogLogUtil {
    @Autowired
    protected StringRedisTemplate redisTemplate;

    public void demo(){
        redisTemplate.opsForHyperLogLog().add("hll1", "1", "2", "3", "4", "5");
    }
}
