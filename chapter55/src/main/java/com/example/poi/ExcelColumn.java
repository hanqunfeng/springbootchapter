package com.example.poi;

import java.lang.annotation.*;

/**
 * <h1>自定义实体类所需要的bean(Excel属性标题、位置等)</h1>
 * Created by hanqf on 2021/8/10 22:49.
 */


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    /**
     * Excel标题
     */
    String title() default "";

    /**
     * Excel从左往右排列位置
     */
    int col() default -1;

    /**
     * 该列是否必须有值，默认false
     */
    boolean required() default false;
}
