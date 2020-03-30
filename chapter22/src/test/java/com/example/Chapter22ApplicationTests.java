package com.example;

import com.example.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Chapter22ApplicationTests {

    @Autowired
    private RedisService redisService;

    @Test
    void cacheKey() {
        redisService.deleteAllkey();
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));
        redisService.deleteAllkey();
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));
        redisService.deleteByKey("qqq");
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));
        System.out.println(redisService.getValueByKey("qqq"));

    }

}
