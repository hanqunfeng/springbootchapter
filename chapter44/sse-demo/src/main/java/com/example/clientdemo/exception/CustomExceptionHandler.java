package com.example.clientdemo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.stream.Collectors;

/**
 * <h1>全局异常处理器</h1>
 * Created by hanqf on 2020/10/29 11:18.
 */

@ControllerAdvice
public class CustomExceptionHandler {

    //对SSE连接超时异常进行全局处理，前端onmessage会收到"timeout!!"的数据
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseBody
    public String handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        return SseEmitter.event().data("timeout!!").build().stream()
                .map(d -> d.getData().toString())
                .collect(Collectors.joining());
    }

}
