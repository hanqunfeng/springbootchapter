package com.example.noweb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.MATCH_ALL;

/**
 * <p>参数化测试用例</p>
 * Created by hanqf on 2020/8/27 16:15.
 *
 * 参考：https://blog.csdn.net/ryo1060732496/article/details/80823696
 */


public class ParameterizedTests {

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
     * 基于提供的参数列表，每个参数执行一次完整测试用例
     * @ValueSource 支持所有基本数据类型，看一下源码就知道了，比如整型 ints = {1,2,3}
    */
    @ParameterizedTest
    @ValueSource(strings = {"张三","李四","wangwu"})
    void test1(String name) {
        System.out.println("test1=" + name);
    }

    /**
     * @EnumSource 使用枚举对象中的每个对象作为参数
    */
    @ParameterizedTest
    @EnumSource(TimeUnit.class)
    void testWithEnumSource(TimeUnit timeUnit) {
        assertNotNull(timeUnit);
    }

    /**
     * @EnumSource 使用枚举对象中的指定的对象名称作为参数
    */
    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, names = { "DAYS", "HOURS" })
    void testWithEnumSourceInclude(TimeUnit timeUnit) {
        assertTrue(EnumSet.of(TimeUnit.DAYS, TimeUnit.HOURS).contains(timeUnit));
    }

    /**
     * 排除指定的对象名称
    */
    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, mode = EXCLUDE, names = { "DAYS", "HOURS" })
    void testWithEnumSourceExclude(TimeUnit timeUnit) {
        assertFalse(EnumSet.of(TimeUnit.DAYS, TimeUnit.HOURS).contains(timeUnit));
    }

    /**
     * 正则匹配对象名称
    */
    @ParameterizedTest
    @EnumSource(value = TimeUnit.class, mode = MATCH_ALL, names = "^(M|N).+SECONDS$")
    void testWithEnumSourceRegex(TimeUnit timeUnit) {
        String name = timeUnit.name();
        assertTrue(name.startsWith("M") || name.startsWith("N"));
        assertTrue(name.endsWith("SECONDS"));
    }

    /**
     * @MethodSource 允许引用测试类或外部类的一个或多个工厂方法
    */
    @ParameterizedTest
    @MethodSource({"stringProvider"})
    void testWithSimpleMethodSource(String argument) {
        assertNotNull(argument);
    }

    /**
     * 方法不能接受任何参数
     * 测试类中的方法必须是静态的
     * 如果测试用例只需要一个参数，可以返回参数类型实例的 Stream(流)，比如该方法
    */
    static Stream<String> stringProvider() {
        return Stream.of("foo", "bar");
    }


    /**
     * 多个参数
    */
    @ParameterizedTest
    @MethodSource("stringIntAndListProvider")
    void testWithMultiArgMethodSource(String str, int num, List<String> list) {
        assertEquals(3, str.length());
        assertTrue(num >=1 && num <=2);
        assertEquals(2, list.size());
    }

    static Stream<Arguments> stringIntAndListProvider() {
        return Stream.of(
                Arguments.of("foo", 1, Arrays.asList("a", "b")),
                Arguments.of("bar", 2, Arrays.asList("x", "y"))
        );
    }


    /**
     * csv格式数据，逗号分割数据，内部可以使用单引号标识一个数据
    */
    @ParameterizedTest
    @CsvSource({ "foo, 1", "bar, 2", "'baz, qux', 3" })
    void testWithCsvSource(String first, int second) {
        System.out.println("first=" + first + " # secode=" + second);
        assertNotNull(first);
        assertNotEquals(0, second);
    }

    /**
     * 使用外部csv文件作为数据源，并且跳过第一行
     * resources = "/two-column.csv" : 基于classPath路径
     * numLinesToSkip = 1 : 跳过第一行
     * encoding = "UTF-8" : 编码
    */
    @ParameterizedTest
    @CsvFileSource(resources = "/two-column.csv", numLinesToSkip = 1,encoding = "UTF-8")
    void testWithCsvFileSource(String first, int second) {
        System.out.println("first=" + first + " # secode=" + second);
        assertNotNull(first);
        assertNotEquals(0, second);
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
