package com.example;

import com.example.demo.redissug.RedisSuggestTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/25 14:59.
 */

@SpringBootTest
public class RedisSugToolTests {

    @Autowired
    private RedisSuggestTool sugTool;

    private static final String KEY = "sug:search";


    @Test
    void sugAdd() {
        System.out.println(sugTool.sugAdd(KEY, "iphone", 1.0));
        System.out.println(sugTool.sugAdd(KEY, "iphone 15", 1.0));
        System.out.println(sugTool.sugAdd(KEY, "ipad", 50.0, false, "category=tablet"));
    }
    @Test
    void sugGet() {
        System.out.println(sugTool.sugGet(KEY, "iphone", true, true, true, 10));
    }
    @Test
    void sugDel() {
        System.out.println(sugTool.sugDel(KEY, "iphone"));
    }
    @Test
    void sugLen() {
        System.out.println(sugTool.sugLen(KEY));
    }

}
