package com.example.noweb;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * <p>springboot 测试用例</p>
 * Created by hanqf on 2020/8/28 17:50.
 */


public class SpringBootTests {

    @Test
    void assertTests() {

        Assert.hasLength("123","empty");
    }

}
