package com.example.common;

import com.example.utils.AesUtil;

import java.lang.annotation.*;

/**
 * <p>响应是否加密注解</p>
 * Created by hanqf on 2020/9/2 14:36.
 */

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseEncrypt {
    /**
     * 返回对body加密，默认是true
     * @return boolean
     */
    boolean value() default true;
    /**
     * 加密类
    */
    Class encryptClass() default AesUtil.class;
    
    /**
     * 加密方法名称
    */
    String encryptMethod() default "encrypt";
}
