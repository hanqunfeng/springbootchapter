package com.example.noweb;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>测试类的生命周期</p>
 * Created by hanqf on 2020/8/27 15:41.
 *
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS) 指定测试类的生命周期
 * TestInstance.Lifecycle.PER_METHOD 默认值，是每个测试方法都使用一个新的实例
 * TestInstance.Lifecycle.PER_CLASS 在同一个测试实例上执行所有测试方法，此时注意实例变量是共享的
 */

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestInstanceTests {

    int count = 0;

    /**
     * 当前类中所有测试方法执行前执行一次,相当于JUnit4中的 @BeforeClass
     *
     * TestInstance.Lifecycle.PER_CLASS 时 @BeforeAll可以声明在非static方法上
     */
    @BeforeAll
    void beforeAll(){
        System.out.println("beforeAll...");
        count++;
    }



    /**
     * 每个测试方法执行前执行,相当于JUnit4中的 @Before
     */
    @BeforeEach
    void beforeEach(){
        System.out.println("beforeEach...");
        count++;
    }


    @Test
    void test1() {
        System.out.println("test1==" + count);
    }

    @Test
    void demo1() {
        System.out.println("demo1==" + count);
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
    void afterAll(){
        System.out.println("afterAll...");
    }
}
