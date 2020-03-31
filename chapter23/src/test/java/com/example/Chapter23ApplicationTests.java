package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class Chapter23ApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    //启用 Spring Boot 项目后 Redis 客户端输入命令
    //publish topic1 msg
    @Test
    void testPublish() {
        redisTemplate.convertAndSend("topic1","hello word");
    }

}
