package com.cas.security;

import org.apereo.cas.authentication.AuthenticationException;

/**
 * <p>验证码匹配失败异常</p>
 * Created by hanqf on 2020/9/23 15:19.
 */


public class CheckCodeErrorException extends AuthenticationException {

    public CheckCodeErrorException(){
        super();
    }

    public CheckCodeErrorException(String msg) {
        super(msg);
    }
}
