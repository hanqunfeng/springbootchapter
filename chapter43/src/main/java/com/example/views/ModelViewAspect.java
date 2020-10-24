package com.example.views;

import com.example.exception.ModelViewException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * <h1>aop拦截@ModelView</h1>
 * Created by hanqf on 2020/10/24 15:54.
 */

@Component
@Slf4j
@Aspect
public class ModelViewAspect {

    /**
     * 设置切入点，拦截标注了@ModelView的方法
    */
    @Pointcut("@annotation(com.example.views.ModelView)")
    public void pointcut(){ }

    /**
     * 当有标注了@ModelView的方法抛出异常时，将其转换为ModelViewException
    */
    @AfterThrowing(pointcut = "pointcut()",throwing = "e")
    public void afterThrowing(Throwable e){
        throw ModelViewException.transfer(e);
    }
}
