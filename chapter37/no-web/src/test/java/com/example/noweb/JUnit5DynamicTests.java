package com.example.noweb;

import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * <p>动态测试</p>
 * Created by hanqf on 2020/8/25 16:38.
 */


public class JUnit5DynamicTests {

    /**
     * 动态测试允许声明和运行在运行时生成的测试用例。
     *
     * 与在编译时定义固定数量的测试用例的静态测试相反，动态测试允许我们在运行时动态定义测试用例。
     *
     * 动态测试可以通过使用*@TestFactory注释的工厂方法生成。
     *
     * 工厂方法必须返回Stream，Collection，Iterable或Iterator。
    */
    @TestFactory
    Collection dynamicTests() {
        return Arrays.asList(
                dynamicTest("simple dynamic test", new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        assertEquals(1,2,"值不相等！");
                    }
                }),
                dynamicTest("My Executable Class", new MyExecutable()),
                dynamicTest("Exception Executable", () -> {
                    throw new Exception("Exception Example");
                }),
                dynamicTest("simple dynamic test-2", () -> assertTrue(true))
        );
    }

}

class MyExecutable implements Executable {

    @Override
    public void execute() throws Throwable {
        System.out.println("Hello World!");
    }


}
