package com.example.config;/**
 * Created by hanqf on 2020/4/2 15:34.
 */


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;

/**
 * @author hanqf
 * @date 2020/4/2 15:34
 */
@Configuration
@AutoConfigureAfter({LocaleConfig.class})
public class MvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    @Autowired
    LocaleChangeInterceptor localeChangeInterceptor;


    /**
     * 配置json转换为ali的FastJson
     * @param converters
     *
     * 可以对输出格式进行配置
     * FastJson SerializerFeatures
     *
     * WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
     * WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
     * DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
     * WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
     * WriteMapNullValue：是否输出值为null的字段,默认为false。
     *
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastJsonHttpMessageConverter);
        logger.info("FastJsonHttpMessageConverter");
    }

    /**
     * 添加拦截器，拦截器可以执行拦截哪些url或者不拦截哪些url
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**");
        logger.info("localeChangeInterceptor");

    }

    /**
     * 配置路劲匹配规则，以下是默认规则，可以不重写该方法
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);  //设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”，默认true即匹配；
        logger.info("configurePathMatch");
    }

    /**
     * 静态资源访问路径映射
     * 类路径下要加上classpath:前缀
     * @param registry
     * 示例：
     *  <link th:href="@{/resource/css/bootstrap.min.css}" rel="stylesheet" />
     * 	<script th:src="@{/resource/js/jquery-1.11.0.min.js}"></script>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**").addResourceLocations("/resource/").addResourceLocations("classpath:/static/");
    }

    /**
     * 路径直接映射到视图，不需要创建controller和映射方法
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("common/index");

    }

    /**
     * 跨域访问控制
     * @param registry
     *
     *
     * import org.springframework.context.annotation.Bean;
     * import org.springframework.context.annotation.Configuration;
     * import org.springframework.web.cors.CorsConfiguration;
     * import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
     * import org.springframework.web.filter.CorsFilter;
     *
     * @Configuration
     * public class CorsConfig {
     *     private CorsConfiguration buildConfig() {
     *         CorsConfiguration corsConfiguration = new CorsConfiguration();
     *         corsConfiguration.addAllowedOrigin("*"); // 1
     *         corsConfiguration.addAllowedHeader("*"); // 2
     *         corsConfiguration.addAllowedMethod("*"); // 3
     *         return corsConfiguration;
     *     }
     *
     *     @Bean
     *     public CorsFilter corsFilter() {
     *         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
     *         source.registerCorsConfiguration("/**", buildConfig()); // 4
     *         return new CorsFilter(source);
     *     }
     * }
     *
     *
     *
     * 也可以在controller的类或方法上加上@CrossOrigin注解，来设置某个controller的全部方法或某个方法可以跨域访问，参数与下面的配置类似
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //配置可以被跨域的路径，可以任意配置，可以具体到直接请求路径，这里表示全部路径
                .allowedMethods("*")   //允许所有的请求方法访问该跨域资源服务器，如：POST、GET、PUT、DELETE等。
                .allowedOrigins("*")//允许所有的请求域名访问我们的跨域资源，可以固定单条或者多条内容，如："http://www.baidu.com"，只有百度可以访问我们的跨域资源。
                .allowedHeaders("*")//允许所有的请求header访问，可以自定义设置任意请求头信息，如："X-YAUTH-TOKEN"
                .allowCredentials(true) //是否允许用户发送、处理 cookie
                .maxAge(3600); //预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
    }



}
