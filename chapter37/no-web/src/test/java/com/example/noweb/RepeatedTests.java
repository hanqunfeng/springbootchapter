package com.example.noweb;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>重复测试用例</p>
 * Created by hanqf on 2020/8/27 15:21.
 */

@SpringBootTest
public class RepeatedTests {
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
     * @RepeatedTest(3) 重复测试3次，相当于重复完整执行3次该测试方法，同时@BeforeEach和@AfterEach也会执行
     *
     * @Test不要再次声明，否则该测试方法会单独再执行一次
    */
    @RepeatedTest(3)
    void test1() {
        System.out.println("test1");
    }

    @RepeatedTest(value = 2,name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("我的测试1")
    void demo1() {
        System.out.println("demo1");
    }

    @RepeatedTest(value = 2,name = RepeatedTest.LONG_DISPLAY_NAME)
    void demo2() {
        System.out.println("demo2");
    }

    /**
     * 默认方法名称就是 RepeatedTest.SHORT_DISPLAY_NAME，所以可以不用声明
    */
    @RepeatedTest(value = 2,name = RepeatedTest.SHORT_DISPLAY_NAME)
    void demo3() {
        System.out.println("demo3");
    }

    /**
     * 重复测试时可以指定一个参数，用于获取重复元数据，比如重复次数，等等
    */
    @RepeatedTest(value = 5)
    void demo4(RepetitionInfo repetitionInfo) {
        System.out.println("当前的次数 #" + repetitionInfo.getCurrentRepetition());
        System.out.println("demo4");
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
