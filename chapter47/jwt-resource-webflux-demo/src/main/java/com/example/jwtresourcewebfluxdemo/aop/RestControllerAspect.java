package com.example.jwtresourcewebfluxdemo.aop;

import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import com.example.jwtresourcewebfluxdemo.exception.CustomException;
import com.example.jwtresourcewebfluxdemo.exception.CustomExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h1>controller拦截器</h1>
 * Created by hanqf on 2020/11/23 16:08.
 */

@Component
//标识是一个Aspect代理类
@Aspect
//如果有多个切面拦截相同的切点，可以用@Order指定执行顺序
//@Order(2)
@Slf4j
public class RestControllerAspect {
    @Pointcut("execution(* com.example.jwtresourcewebfluxdemo.controller.*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("RestControllerAspect around....");
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        //实际执行的方法
        Object proceed = proceedingJoinPoint.proceed();
        if (returnTypeName.equals("Mono")) {
            return ((Mono) proceed)
                    .doOnError((Consumer<Throwable>) throwable -> throwable.printStackTrace())
                    .onErrorResume((Function<Throwable, Mono>) throwable -> Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,throwable.getMessage(),throwable.getClass().getName()))))
                    .switchIfEmpty(Mono.just(AjaxResponse.success(null)));
        } else {
            return proceed;
        }
    }
}
