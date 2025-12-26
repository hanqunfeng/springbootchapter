package com.example;

import com.example.demo.bitmap.BitmapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/18 18:06.
 */

@SpringBootTest
public class BitmapTests {
    @Autowired
    BitmapUtil bitmapUtil;

    @Test
    void demo(){
        bitmapUtil.demo();
    }

    @Test
    void bitCount(){
        final Long limits = bitmapUtil.bitCount("limits");
        System.out.println(limits);
    }
}
