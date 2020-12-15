package org.example.util;

import java.lang.annotation.*;

/**
 * <h1></h1>
 * Created by hanqf on 2020/12/15 15:49.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRegister {
}
