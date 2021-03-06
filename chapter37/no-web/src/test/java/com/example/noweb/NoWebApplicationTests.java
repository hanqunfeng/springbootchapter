package com.example.noweb;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import static org.junit.jupiter.api.condition.JRE.JAVA_9;

/**
 * 类说明
 *
 * @RunWith(SpringRunner.class) JUnit4 需要声明该注解，Junit5 只需要声明@SpringBootTest
 *
 * @TestMethodOrder(MethodOrderer.OrderAnnotation.class) 指定测试方法执行顺序基于@Order中的值，值越小越先执行
 * MethodOrderer.OrderAnnotation.class : 使用@Order中的值，，没有声明@Order，默认值是Integer.MAX_VALUE / 2
 * MethodOrderer.Alphanumeric.class : 使用字母顺序
 * MethodOrderer.Random.class : 使用随机顺序
 *
 * 如果不声明@TestMethodOrder，默认按方法的字母顺序执行
 *
 * @Tag 该注解可以用在方法或类上，用于标记测试组
 * mvn -Dgroups="test2,test4" clean package 指定要执行的测试用例，相同Tag名称的方法或类会被执行
 * mvn -DexcludedGroups="test1,test2" clean package 指定不执行的测试用例
 *
 * 也可以通过mvn插件来声明：参考：https://blog.csdn.net/cyan20115/article/details/106549237
 * <plugin>
 *    <groupId>org.apache.maven.plugins</groupId>
 *    <artifactId>maven-surefire-plugin</artifactId>
 *    <version>3.0.0-M3</version>
 *    <configuration>
 *        <groups>demo1,test4</groups>
 *        <excludedGroups>test3</excludedGroups>
 *    </configuration>
 * </plugin>
 *
 * @Tag标签不能为空或 null。
 * 不能包含空格。不能包含ISO控制字符。
 * 不能包含以下任何保留字符:
 * ,逗号 (左括号 )右括号 &与 |竖线 !非
 *
 *
 * 条件注解，有许多条件注解，可以看一下源码包，比如存在某个JVM系统属性、环境变量时，等等
 * @EnabledXXXXX : 符合条件时执行
 * @EnabledOnOs(OS.MAC)
 * @EnabledOnJre({ JAVA_9, JAVA_8 })
 * @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
 * @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")
 *
 * @DisabledXXXXX : 符合条件时不执行
 * @DisabledOnOs(OS.MAC)
 * @DisabledOnJre(JAVA_8)
 * @DisabledIfSystemProperty(named = "ci-server", matches = "true")
 * @DisabledIfEnvironmentVariable(named = "ENV", matches = ".*development.*")
*/

@Tag("NoWebApplicationTests")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoWebApplicationTests {
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
     * 一定最后一个执行了
    */
    @Tags({@Tag("test1"),@Tag("demo1")})
    @Order(Integer.MAX_VALUE)
    @Test
    void test1() {
        System.out.println("test1");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    /**
     * 可以同时指定多个tag，也可以用@Tags({@Tag("test1"),@Tag("demo1")})
    */
    @Tag("test2")
    @Tag("demo1")
    @Order(1)
    @Test
    void test2() {
        System.out.println("test2");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    /**
     * @Disabled 不测试该方法，相当于JUnit4中的 @Ignore
    */
    @Disabled("不测试的原因，可以不设置")
    @Test
    void test3() {
        System.out.println("test3");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    @Tag("test4")
    @Order(2)
    @Test
    void test4() {
        System.out.println("test4");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }


    @Test
    void test5() {
        System.out.println("test5");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @EnabledOnJre({ JAVA_9, JAVA_8 })
    void test6() {
        System.out.println("test5");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

    @Test
    @DisabledOnOs(OS.MAC)
    void test7() {
        System.out.println("test5");
        int a = 1;
        int b = 1;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }


    @Test
    @DisabledOnJre(JAVA_8)
    void test8() {
        System.out.println("test5");
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
