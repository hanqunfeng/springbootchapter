package com.cas.security;

import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Size;

/**
 * <p>自定义登录表单信息</p>
 * Created by hanqf on 2020/9/22 17:07.
 */


public class CustomCredential extends UsernamePasswordCredential {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCredential.class);

    private static final long serialVersionUID = -4166149641561667276L;

    //验证码
    @Size(min = 4,max = 4,message = "require captcha")
    private String captcha;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
