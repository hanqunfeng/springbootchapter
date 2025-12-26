package com.example;

import com.example.demo.stream.StreamUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/20 16:23.
 */

@SpringBootTest
public class StreamUtilTests {
    @Autowired
    StreamUtil streamUtil;

    @Test
    void demo() {
        streamUtil.demo();
    }
}
