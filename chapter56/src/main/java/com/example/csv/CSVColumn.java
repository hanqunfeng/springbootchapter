package com.example.csv;

import java.lang.annotation.*;

/**
 * <h1>自定义实体类所需要的bean(CSV属性标题、位置等)</h1>
 * Created by hanqf on 2021/10/13 22:49.
 */


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSVColumn {
    /**
     * 标题
     */
    String title() default "";


    /**
     * 该列是否必须有值，默认false
     */
    boolean required() default false;

    /**
     * 格式化，日期的格式化
     */
    String format() default "";

    /**
     * 读csv文件字段绑定
     * 绑定格式转换类，字符串转Object
     *
     * @return
     */
    Class<? extends AbstractConvertCsvBase> convert() default AbstractConvertCsvBase.Converter.class;
}
