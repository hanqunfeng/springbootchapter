package com.example;

import com.example.redisbloom.RedisBloomFilterTool2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 *
 * Created by hanqf on 2025/12/21 16:46.
 */

@SpringBootTest
public class BloomFilterTests {
    @Autowired
    RedisBloomFilterTool2 redisBloomFilterTool;

    @Test
    public void reserve() {
        redisBloomFilterTool.reserve("testb", 0.01, 1000);
    }

    @Test
    public void add() {
        System.out.println(redisBloomFilterTool.add("testb", "1"));

    }

    @Test
    public void addBatch() {
        final List<Long> testb = redisBloomFilterTool.addBatch("testb", "2", "3", "4", "5","100");
        System.out.println(testb);
        if(testb.get(0).equals(0L)){
            System.out.println("已存在");
        }else {
            System.out.println("不存在");
        }

    }

    @Test
    public void exists() {
        System.out.println(redisBloomFilterTool.exists("testb", "1"));

    }

    @Test
    public void insert() {
        System.out.println(redisBloomFilterTool.insert("testb", 10000, 0.01, "6", "7"));
    }

    @Test
    public void mexists() {
        System.out.println(redisBloomFilterTool.mexists("testb", "6", "7", "8"));
    }

    @Test
    public void card() {
        System.out.println(redisBloomFilterTool.card("testb"));
    }

    @Test
    void info() {
        System.out.println(redisBloomFilterTool.info("testb"));
    }
}
