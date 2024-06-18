package com.hanqf.support.jpa;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <h1></h1>
 * Created by hanqf on 2021/9/7 18:04.
 */

@Documented
@Component
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaDto {
}
