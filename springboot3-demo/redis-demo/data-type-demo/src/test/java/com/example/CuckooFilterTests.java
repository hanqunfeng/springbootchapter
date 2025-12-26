package com.example;

import com.example.redisbloom.RedisCuckooFilterTool2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/22 17:19.
 */

@SpringBootTest
public class CuckooFilterTests {
    @Autowired
    RedisCuckooFilterTool2 redisCuckooFilterTool;

    @Test
    void reserve() {
        redisCuckooFilterTool.reserve("cuckoo:demo", 1000);
    }

    @Test
    void add() {
        System.out.println(redisCuckooFilterTool.add("cuckoo:demo", "1"));
    }

    @Test
    void addNx() {
        System.out.println(redisCuckooFilterTool.addNx("cuckoo:demo", "1"));
    }

    @Test
    void exists() {
        System.out.println(redisCuckooFilterTool.exists("cuckoo:demo", "1"));
    }

    @Test
    void mexists() {
        System.out.println(redisCuckooFilterTool.mexists("cuckoo:demo", "1", "2", "3"));
    }

    @Test
    void count() {
        System.out.println(redisCuckooFilterTool.count("cuckoo:demo", "1"));
    }

    @Test
    void delete() {
        System.out.println(redisCuckooFilterTool.delete("cuckoo:demo", "1"));
    }

    @Test
    void insert() {
        System.out.println(redisCuckooFilterTool.insert("cuckoo:demo", "1", "2", "3"));
    }

    @Test
    void insertNx() {
        System.out.println(redisCuckooFilterTool.insertNx("cuckoo:demo", "1", "2", "3"));
    }

    @Test
    void info() {
        System.out.println(redisCuckooFilterTool.info("cuckoo:demo"));
    }
}
