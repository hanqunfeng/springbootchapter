package com.example.config;/**
 * Created by hanqf on 2020/4/2 15:48.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * @author hanqf
 * @date 2020/4/2 15:48
 */
@Configuration
public class LocaleConfig {

    private static final Logger logger = LoggerFactory.getLogger(LocaleConfig.class);

    /**
     * 描述 : <本地化拦截器>. <br>
     *<p>
     <使用方法说明>
     </p>
     * @return
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale"); //?locale=zh_CN  用于切换语言的参数名称
        return localeChangeInterceptor;
    }

    /**
     * 描述 : <基于cookie的本地化资源处理器>. <br>
     *<p>
     <使用方法说明>
     也可以基于session:SessionLocaleResolver
     </p>
     * @return
     */
    @Bean(name="localeResolver")
    public CookieLocaleResolver cookieLocaleResolver(){
        logger.info("CookieLocaleResolver");
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        //Locale.SIMPLIFIED_CHINESE  == zh_CN
        //Locale.US == en_US
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); //设置缺省的语言，可以不设置
        return cookieLocaleResolver;
    }
}
