package com.example;

import com.example.demo.string.RedisStringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class StringTests {
    @Autowired
    RedisStringUtil redisStringUtil;

    @Test
    void demo() throws InterruptedException {
        redisStringUtil.setEx("ttl1", "1", 100);
        TimeUnit.SECONDS.sleep(10);
        redisStringUtil.setKeepTtl("ttl1", "2");
    }
    @Test
    void demo2() throws InterruptedException {
        redisStringUtil.set("user","张三");
        System.out.println(redisStringUtil.get("user"));
    }

}
