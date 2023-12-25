package com.example.config;

import com.example.support.CP_InitializingInterceptor;
import com.example.support.CP_ResourceBundleMessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;

/**
 * @author hanqf
 * @date 2020/4/2 15:34
 */
@Configuration
@Slf4j
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 描述 : <注册消息资源处理器>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @return
     */
    @Bean("messageSource")
    public MessageSource messaeSource() {
        log.info("MessageSource");
        CP_ResourceBundleMessageSource messageSource = new CP_ResourceBundleMessageSource();
        messageSource.setPackagename("config.i18n");
        messageSource.setDefaultEncoding("utf-8");
        return messageSource;
    }


    /**
     * 配置json转换为Jackson
     *
     * @param converters 可以对输出格式进行配置
     *                   Jackson SerializerFeatures
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
        log.info("jackson2HttpMessageConverter");
    }


    /**
     * 描述 : <本地化拦截器>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @return
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale"); //?locale=zh_CN  用于切换语言的参数名称
        return localeChangeInterceptor;
    }

    /**
     * 描述 : <基于cookie的本地化资源处理器>. <br>
     * <p>
     * <使用方法说明>
     * 也可以基于session:SessionLocaleResolver
     * </p>
     *
     * @return
     */
    @Bean(name = "localeResolver")
    public CookieLocaleResolver cookieLocaleResolver() {
        log.info("CookieLocaleResolver");
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        //Locale.SIMPLIFIED_CHINESE  == zh_CN
        //Locale.US == en_US
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); //设置缺省的语言，可以不设置
        return cookieLocaleResolver;
    }


    /**
     * 描述 : <注册自定义拦截器>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @return
     */
    @Bean
    public CP_InitializingInterceptor initializingInterceptor() {
        CP_InitializingInterceptor cp_initializingInterceptor = new CP_InitializingInterceptor();
        return cp_initializingInterceptor;
    }


    /**
     * 添加拦截器，拦截器可以执行拦截哪些url或者不拦截哪些url
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
        log.info("localeChangeInterceptor");

        registry.addInterceptor(initializingInterceptor()).addPathPatterns("/**/*.do*");
        log.info("CP_InitializingInterceptor");
    }

    /**
     * 配置路劲匹配规则，以下是默认规则，可以不重写该方法
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);  //设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”，默认true即匹配；
        log.info("configurePathMatch");
    }

    /**
     * 静态资源访问路径映射
     * 类路径下要加上classpath:前缀
     *
     * @param registry 示例：
     *                 <link th:href="@{/resource/css/bootstrap.min.css}" rel="stylesheet" />
     *                 <script th:src="@{/resource/js/jquery-1.11.0.min.js}"></script>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**")
                .addResourceLocations("/resource/")
                .addResourceLocations("classpath:/static/");

    }

    /**
     * 路径直接映射到视图，不需要创建controller和映射方法
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("common/index");

    }

    /**
     * 跨域访问控制
     * <p>
     * 也可以在controller的类或方法上加上@CrossOrigin注解，来设置某个controller的全部方法或某个方法可以跨域访问，参数与下面的配置类似
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //配置可以被跨域的路径，可以任意配置，可以具体到直接请求路径，这里表示全部路径
                .allowedMethods("*")   //允许所有的请求方法访问该跨域资源服务器，如：POST、GET、PUT、DELETE等。
                .allowedOriginPatterns("*")//允许所有的请求域名访问我们的跨域资源，可以固定单条或者多条内容，如："http://www.baidu.com"，只有百度可以访问我们的跨域资源。
                .allowedHeaders("*")//允许所有的请求header访问，可以自定义设置任意请求头信息，如："X-YAUTH-TOKEN"
                .allowCredentials(true) //是否允许用户发送、处理 cookie
                .maxAge(3600); //预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
    }


}
