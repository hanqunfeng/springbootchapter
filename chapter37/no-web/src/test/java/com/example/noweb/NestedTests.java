package com.example.noweb;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>嵌套测试类</p>
 * Created by hanqf on 2020/8/27 15:08.
 */

@SpringBootTest
@DisplayName("这是一个嵌套类的顶层类")
public class NestedTests {

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


    @Test
    void test1() {
        System.out.println("test1");
    }

    @Test
    void demo1() {
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


    /**
     * 嵌套测试类，外层中声明的@BeforeAll，@BeforeEach等方法，对嵌套类依然有效
    */
    @DisplayName("嵌套类1")
    @Nested
    class NestdeClass1{

        /**
         * 每个测试方法执行前执行,相当于JUnit4中的 @Before
         */
        @BeforeEach
        void beforeEach(){
            System.out.println("NestdeClass1 beforeEach...");
        }


        @Test
        void test1() {
            System.out.println("NestdeClass1 test1");
        }

        @Test
        void demo1() {
            System.out.println("NestdeClass1 demo1");
        }

        /**
         * 每个测试方法执行后执行,相当于JUnit4中的 @After
         */
        @AfterEach
        void afterEach(){
            System.out.println("NestdeClass1 afterEach...");
        }


        @DisplayName("嵌套类2")
        @Nested
        class NestdeClass2{

            /**
             * 每个测试方法执行前执行,相当于JUnit4中的 @Before
             */
            @BeforeEach
            void beforeEach(){
                System.out.println("NestdeClass2 beforeEach...");
            }


            @Test
            void test1() {
                System.out.println("NestdeClass2 test1");
            }

            @Test
            void demo1() {
                System.out.println("NestdeClass2 demo1");
            }

            /**
             * 每个测试方法执行后执行,相当于JUnit4中的 @After
             */
            @AfterEach
            void afterEach(){
                System.out.println("NestdeClass2 afterEach...");
            }


        }
    }
}
