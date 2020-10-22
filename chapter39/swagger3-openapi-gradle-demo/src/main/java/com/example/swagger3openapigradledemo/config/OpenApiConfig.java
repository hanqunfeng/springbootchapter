package com.example.swagger3openapigradledemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>swagger3配置类</h1>
 * Created by hanqf on 2020/10/22 11:23.
 * 显示全部接口api: http://localhost:8080/swagger-ui/index.html?url=/v3/api-docs
 * 显示分组接口api： http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
 */

//配置项
@OpenAPIDefinition(
        info = @Info(
                title = "Springdoc demo (openApi)",
                description = "" +
                        "Springdoc demo",
                contact = @Contact(
                        name = "hanqf",
                        url = "https://blog.hanqunfeng.com",
                        email = "hanqf2008@163.com"
                ),
                license = @License(
                        name = "MIT Licence",
                        url = "https://github.com/hanqunfeng/springbootchapter/blob/master/LICENSE")),
        servers = @Server(url = "http://localhost:8080")
)
//username,password basic认证
@SecurityScheme(name = "api", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
//token认证
//@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "apiKey", in = SecuritySchemeIn.HEADER, paramName = "token")
@Configuration
public class OpenApiConfig {

    //分组
    @Bean
    public GroupedOpenApi articalsApi() {
        return GroupedOpenApi.builder()
                .group("articals")
                .pathsToMatch("/articals/**")
                .build();
    }

    @Bean
    public GroupedOpenApi demoApi() {
        return GroupedOpenApi.builder()
                .group("demo")
                .pathsToMatch("/demo/articals/**")
                .build();
    }

}
