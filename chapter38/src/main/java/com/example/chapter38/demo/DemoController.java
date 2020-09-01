package com.example.chapter38.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 * Created by hanqf on 2020/9/1 15:49.
 */

@RestController
public class DemoController {

    @Autowired
    private AsyncService service;

    @GetMapping("/")
    public String index() throws ExecutionException, InterruptedException {
        Future<String> stringFuture = service.asyncMethodWithReturnType();
        service.asyncMethodWithVoidReturnType();
        return stringFuture.get();
    }

    /**
     * <p>这里会异步捕获void方法的异常</p>
     *
     * @param
     * @return java.lang.String
     * @author hanqf
     * 2020/9/1 16:24
     */
    @GetMapping("/err")
    public String err() throws ExecutionException, InterruptedException, TimeoutException {
        Future<String> stringFuture = service.asyncMethodWithReturnType();
        service.asyncMethodWithVoidReturnTypeError(1, 2);
        return stringFuture.get();
    }

}
