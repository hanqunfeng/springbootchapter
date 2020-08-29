package com.example.noweb;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * <p>扩展测试用例</p>
 * Created by hanqf on 2020/8/29 20:09.
 *
 * @ExtendWith 可以声明在类和方法上
 *
 * BeforeAllCallBack //(1)
 *     @BeforeAll  //(2)
 *         BeforeEachCallBack  //(3)
 *             @BeforeEach //(4)
 *                 BeforeTestExecutionCallBack //(5)
 *                     @Test   //(6)
 *                     TestExecutionExceptionHandler   //(7)
 *                 BeforeTestExecutionCallBack //(8)
 *             @AfterEach //(9)
 *         AfterEachCallBack  //(10)
 *     @AfterAll  //(11)
 * AfterAllCallBack //(1)
 *
 *
 * 步骤  接口/注解	                                                            描述
 * 1	interface org.junit.jupiter.api.extension.BeforeAllCallback	            在执行容器的所有测试之前执行扩展代码
 * 2	@annotation org.junit.jupiter.api.BeforeAll	                            在执行容器的所有测试之前执行用户代码
 * 3	interface org.junit.jupiter.api.extension.BeforeEachCallback	        在执行每个测试之前执行的扩展代码
 * 4	@annotation org.junit.jupiter.api.BeforeEach	                        在执行每个测试之前执行的用户代码
 * 5	interface org.junit.jupiter.api.extension.BeforeTestExecutionCallback	在执行测试之前立即执行的扩展代码
 * 6	@annotation org.junit.jupiter.api.Test	                                用户代码的实际测试方法
 * 7	interface org.junit.jupiter.api.extension.TestExecutionExceptionHandler	处理在测试期间抛出的异常的扩展代码
 * 8	interface org.junit.jupiter.api.extension.AfterTestExecutionCallback	测试执行后立即执行的扩展代码及其相应的异常处理程序
 * 9	@annotation org.junit.jupiter.api.AfterEach	                            在每次测试执行后执行的用户代码
 * 10	interface org.junit.jupiter.api.extension.AfterEachCallback	            每次测试执行后执行的扩展代码
 * 11	@annotation org.junit.jupiter.api.AfterAll	                            在执行容器的所有测试之后执行用户代码
 * 12	interface org.junit.jupiter.api.extension.AfterAllCallback	            在执行容器的所有测试之后执行扩展代码
 */

@ExtendWith({BeforeAllCallbackExecution.class,AfterEachCallbackExecution.class,TimingExtension.class,IgnoreIOExceptionExtension.class})
public class ExecutionTests {

    @BeforeAll
    static void beforeAll(){
        System.out.println("@BeforeAll");
    }

    @Test
    void test1(){
        System.out.println("@Test1");
    }

    @Test
    void test2(){
        System.out.println("@Test2");
    }
}

class BeforeAllCallbackExecution implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        System.out.println("BeforeAllCallback");
        Class<?> requiredTestClass = context.getRequiredTestClass();
        String testClassName = requiredTestClass.getName();
        System.out.println("testClassName==" + testClassName);
    }
}

class AfterEachCallbackExecution implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.out.println("AfterEachCallback");
        Class<?> requiredTestClass = context.getRequiredTestClass();
        String testClassName = requiredTestClass.getName();
        System.out.println("testClassName==" + testClassName);
        System.out.println(context.getTestClass().get().getName());

        Method requiredTestMethod = context.getRequiredTestMethod();
        String testMethodName = requiredTestMethod.getName();
        System.out.println("testMethodName==" + testMethodName);

        System.out.println(context.getTestMethod().get().getName());
    }
}

/**
 * 数据可以共享：context.getStore().put/get/remove
*/
class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {


    private static final String START_TIME = "start time";

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        long startTime = getStore(context).remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println(String.format("Method [%s] took %s ms.", testMethod.getName(), duration));
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}

/**
 * 异常处理扩展，这里对IOException不做任何处理
*/
class IgnoreIOExceptionExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {

        if (throwable instanceof IOException) {
            return;
        }
        throw throwable;
    }
}
