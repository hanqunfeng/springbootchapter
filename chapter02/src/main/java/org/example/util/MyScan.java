package org.example.util;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <h1>扫描指定路径，并将其下的bean注册到spring中</h1>
 * Created by hanqf on 2020/12/15 11:18.
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ImportBeanDefinitionRegistrarExample.class)
public @interface MyScan {
    String value() default "";
}
