package com.example;

import com.example.service.TestRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class   Chapter22ApplicationTests {

    @Autowired
    private TestRedisService testRedisService;

    @Test
    void cacheKey() {
        testRedisService.deleteAllkey();
        System.out.println(testRedisService.getValueByKey("aaa"));
        System.out.println(testRedisService.getValueByKey("qqq"));
        System.out.println(testRedisService.getValueByKey2("ccc"));
        System.out.println(testRedisService.getValueByKey3("ddd"));

        testRedisService.deleteByKey("qqq");
        System.out.println(testRedisService.getValueByKey("aaa"));
        System.out.println(testRedisService.getValueByKey("qqq"));
        System.out.println(testRedisService.getValueByKey2("ccc"));
        System.out.println(testRedisService.getValueByKey3("ddd"));

        testRedisService.deleteCache("aaa");

    }

}
