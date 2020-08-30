package com.example.noweb;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * <p>springboot 测试用例</p>
 * Created by hanqf on 2020/8/28 17:50.
 *
 * org.springframework.util.Assert ，提供了很多常用的断言方法，但其功能比JUnit5或者AssertJ等第三方断言类库提供的断言方法要少很多
 *
 */

//@SpringBootTest //需要用的springboot上下文时才需要引入注解
public class SpringBootTests {

    @Test
    void assertTests() {

        //org.springframework.util.Assert 对象
        String str = "userName";
        Assert.hasLength(str,"empty");
        Assert.notNull(str,"null");
        Assert.isInstanceOf(String.class,"类型错误");
    }

}
