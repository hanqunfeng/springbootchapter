package com.example.config;

/**
 * <h1>WebFluxConfig</h1>
 * Created by hanqf on 2023/8/28 16:32.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * <h1>WebFlux配置类</h1>
 *
 * 我们若需要配置Spring WebFlux只需让配置配实现接口WebFluxConfigurer，
 * 这样我们既能保留Spring Boot给WebFlux配置又能添加我们的定制配置。
 * 若我们想完全控制WebFlux，则在配置类添加注解@EnableWebFlux
 * 配置方式和Spring MVC类似
 */

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    //跨域设置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
                .allowedMethods("GET","POST", "PUT", "DELETE") //开放哪些Http方法，允许跨域访问
                .allowedHeaders("*") //允许HTTP请求中的携带哪些Header信息
                //When allowCredentials is true, allowedOrigins cannot contain the special value "*"since that cannot be set on the "Access-Control-Allow-Origin" response header.
                // To allow credentials to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
                //.allowedOrigins("*") //开放哪些ip、端口、域名的访问权限
                .allowedOriginPatterns("*")
                .allowCredentials(true); //是否允许发送Cookie信息

    }
}
