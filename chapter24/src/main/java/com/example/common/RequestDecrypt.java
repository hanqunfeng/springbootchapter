package com.example.common;

import com.example.utils.AesUtil;

import java.lang.annotation.*;

/**
 * <p>请求解密注解</p>
 * Created by hanqf on 2020/9/2 15:29.
 */

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestDecrypt {

    /**
     * 返回对body解密，默认是true
     * @return boolean
     */
    boolean value() default true;
    /**
     * 解密类
     */
    Class decryptClass() default AesUtil.class;

    /**
     * 解密方法名称
     */
    String decryptMethod() default "decrypt";

    /**
     * 密钥
     */
    String key() default "";
}
