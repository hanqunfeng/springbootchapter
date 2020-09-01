package com.example.chapter38.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * <p></p>
 * Created by hanqf on 2020/9/1 15:46.
 *
 * 异步方法的返回值只支持如下四种：具体查看AsyncExecutionAspectSupport.doSubmit()
 * void
 * java.util.concurrent.Future
 * org.springframework.util.concurrent.ListenableFuture
 * java.util.concurrent.CompletableFuture
 */

@Service
public class AsyncService {

    /**
     * <p>没有返回值的异步方法</p>
     *
     * @author hanqf
     * 2020/9/1 16:29
     */
    @Async
    public void asyncMethodWithVoidReturnType() {
        System.out.println("asyncMethodWithVoidReturnType Execute method asynchronously. "
                + Thread.currentThread().getName());

    }

    /**
     * <p>有返回值的异步方法</p>
     *
     * @return java.util.concurrent.Future<java.lang.String>
     * @author hanqf
     * 2020/9/1 16:29
     */
    @Async
    public Future<String> asyncMethodWithReturnType() {
        System.out.println("asyncMethodWithReturnType Execute method asynchronously - "
                + Thread.currentThread().getName());
        try {
            //休眠5秒，模拟业务执行时间
            Thread.sleep(5000);
            System.out.println("asyncMethodWithReturnType return");
            return new AsyncResult<String>("hello world !!!!");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException error");
        }

        return null;
    }

    /**
     * <p>人为制造异常，这里主要测试异步全局异常处理器，因为只有void方法的异常才会被处理</p>
     *
     * @param a
     * @param b
     * @author hanqf
     * 2020/9/1 16:24
     */
    @Async
    public void asyncMethodWithVoidReturnTypeError(int a, int b) {
        System.out.println("asyncMethodWithVoidReturnTypeError Execute method asynchronously. "
                + Thread.currentThread().getName());
        int err = 1 / 0;

    }
}
