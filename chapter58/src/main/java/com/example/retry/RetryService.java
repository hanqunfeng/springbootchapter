package com.example.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * <h1></h1>
 * Created by hanqf on 2022/1/21 17:17.
 */

@Service
@Slf4j
public class RetryService {

    /**
     * 添加重试注解,当有异常时触发重试机制.默认就是捕获全部异常
     * 设置重试2次,默认是3.延时2000ms再次执行,每次延时提高1.5倍.当返回结果不符合要求时,主动报错触发重试.
     *
     * @param count
     * @return
     * @throws Exception
     */
    @Retryable(value = {Exception.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public String call(Integer count) throws Exception {
        if (count > 0) {
            log.info("Remote RPC call do something... {}", LocalTime.now());
            throw new RemoteAccessException("RPC调用异常");
        }else if(count < 0){
            log.info("IllegalArgumentException... {}", LocalTime.now());
            throw new IllegalArgumentException("参数异常");
        }
        return "SUCCESS";
    }

    /**
     * 回调方法，当@Retryable重试后依旧失败时会调用该回调方法，非必须，不定义时则会后一次调用失败时抛出对应的异常
     * 定义回调,注意异常类型和方法返回值类型要与重试方法一致，同样可以接收方法的参数
     * 不同的异常会调用不同的回调方法
     *
     * @param e retry方法抛出的异常
     * @param count retry方法接收的参数
     * @return
     */
    @Recover
    public String recover(RemoteAccessException e, Integer count) {
        log.error("Remote RPC Call fail", e);
        return "recover SUCCESS RemoteAccessException";
    }

    @Recover
    public String recover(IllegalArgumentException e, Integer count) {
        log.error("IllegalArgumentException", e);
        return "recover SUCCESS IllegalArgumentException";
    }
}
