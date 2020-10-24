package com.example.views;

import java.lang.annotation.*;

/**
 * <h1>视图注解</h1>
 * Created by hanqf on 2020/10/24 15:49.
 *
 * 注解只能使用在controller的方法上
 * 使用该注解后，无需自行处理异常，将统一由全局异常拦截器处理
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelView {
}
