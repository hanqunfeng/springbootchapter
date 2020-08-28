package com.example.noweb;

import org.junit.jupiter.api.Test;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * <p>断言测试用例</p>
 * Created by hanqf on 2020/8/28 14:29.
 *
 * 所有JUnit5断言都是 org.junit.jupiter.api.Assertions 中的静态方法
 */


public class AssertionsTests {

    //标准断言
    @Test
    void standardAssertions() {

        int a = 1;
        int b = 1;
        int c = 2;
        assertEquals(a, b);
        assertEquals(a, b, "不相等提示信息");
        assertNotEquals(a,c,"相等提示信息");

        assertTrue(a==b, String.format("%d != %d", a, b));
        assertTrue(a==b, () -> String.format("%d != %d", a, b));

        String str = "firstName";
        String str1 = "firstName";
        String str2 = new String("firstName");
        assertNull(null, String.format("%s is null", null));
        assertNotNull(str,() -> {
            return String.format("%s is not null", str);
        });

        //assertSame 是对象比较，使用 == 比较，比较的是内存地址
        assertTrue(str == str1,"Object same");
        assertSame(str,str1,"Object same");
        assertNotSame(str,str2,"Object not same");

        //数组比较
        int[] arrays1 = {1,2,3};
        int[] arrays2 = {1,2,3};
        assertArrayEquals(arrays1,arrays2,"数组不相等");




        //验证是否抛出指定的异常
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("a message");
        });
        assertEquals("a message", exception.getMessage());

        //验证没有抛出异常
        assertDoesNotThrow(() -> {
            //do something
        });

    }

    //分组断言
    @Test
    void groupedAssertions() {
        // 一组断言，任何一个断言失败都会导致测试失败
        String str = "firstName";
        assertAll("strHead",
                () -> assertNotNull(str,"str not null"),
                () -> assertEquals("firstName", str)

        );
    }

    //超时断言
    @Test
    void timeOutAssertions() {
        //验证2分钟内测试任务是否执行完毕
        assertTimeout(ofMinutes(2), () -> {
            // Perform task that takes less than 2 minutes.
        });

        //验证2分钟内测试任务是否执行完毕，执行成功则获取任务的返回值
        String actualResult = assertTimeout(ofMinutes(2), () -> {
            return "a result";
        });
        assertEquals("a result", actualResult);

    }

    //以下两个方法区别在于超时提示信息不一样
    //assertTimeout 会提示预期于实际值
    //assertTimeoutPreemptively 只会提示超出预期值

    @Test
    void timeoutExceeded() {
        // The following assertion fails with an error message similar to:
        // execution exceeded timeout of 10 ms by 91 ms
        assertTimeout(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100);
        });
    }

    @Test
    void timeoutExceededWithPreemptiveTermination() {
        // The following assertion fails with an error message similar to:
        // execution timed out after 10 ms
        assertTimeoutPreemptively(ofMillis(10), () -> {
            // Simulate task that takes more than 10 ms.
            Thread.sleep(100);
        });
    }


}
