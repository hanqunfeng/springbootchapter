package com.cas.config;

import com.cas.controller.CaptchaController;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>验证码配置类</p>
 * Created by hanqf on 2020/9/11 14:25.
 *
 * 参考：https://blog.csdn.net/yelllowcong/article/details/79250841
 */

@Configuration("captchaConfiguration")
//此处把CasConfigurationProperties注入到当前容器中，方便其它类使用，该属性类中为application.properties中配置的以cas开头的所有属性
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CaptchaConfiguration {
    // 注册bean到spring容器
    @Bean
    @ConditionalOnMissingBean(name = "captchaController")
    public CaptchaController captchaController() {
        return new CaptchaController();
    }
}
