package com.example.csv.demo;

import java.lang.annotation.*;

/**
 * <h1>按名称匹配时的顺序</h1>
 * Created by hanqf on 2021/10/19 17:07.
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvOrder {
    /**
     * order越小越靠前
     */
    int order() default 0;
}
