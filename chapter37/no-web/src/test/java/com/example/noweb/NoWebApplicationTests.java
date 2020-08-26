package com.example.noweb;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class) JUnit4 需要声明该注解
@SpringBootTest
class NoWebApplicationTests {
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
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    @Test
    void test2() {
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    /**
     * @Disabled 不测试该方法，相当于JUnit4中的 @Ignore
    */
    @Disabled
    @Test
    void test3() {
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
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
