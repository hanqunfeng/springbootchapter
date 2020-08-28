package com.example.noweb;

import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * <p>超时测试用例</p>
 * Created by hanqf on 2020/8/27 15:56.
 */

class TimeoutTests {

    /**
     * 当前类中所有测试方法执行前执行一次,相当于JUnit4中的 @BeforeClass
     */
    @BeforeAll
    static void beforeAll(){
        System.out.println("beforeAll...");
    }



    /**
     * 每个测试方法执行前执行,相当于JUnit4中的 @Before
     */
    @BeforeEach
    void beforeEach(){
        System.out.println("beforeEach...");
    }


    /**
     * @Timeout(3) 指定超时时间为3秒，单位默认秒，超过这个时间没有完成测试方法，则使测试失败
     *
     * @Timeout(value = 3,unit = TimeUnit.SECONDS) 指定时间单位
    */
    @Test
    @Timeout(3)
    void test1() throws InterruptedException {
        Thread.sleep(4000);
        System.out.println("test1");
    }

    /**
     * 也可以用assertTimeout方法指定超时时间，不过这样做有点麻烦了
    */
    @Test
    void demo1() {
        assertTimeout(Duration.ofSeconds(5), () -> {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("delaySecond");
        });
        System.out.println("demo1");

    }


    /**
     * 每个测试方法执行后执行,相当于JUnit4中的 @After
     */
    @AfterEach
    void afterEach(){
        System.out.println("afterEach...");
    }


    /**
     * 当前类中所有测试方法执行前执行一次,相当于JUnit4中的 @AfterClass
     */
    @AfterAll
    static void afterAll(){
        System.out.println("afterAll...");
    }

}
