package com.example.swagger3demo.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>实际上不做任何配置，swagger3就可以使用，默认扫描所有的url</p>
 *  访问路径：/swagger-ui/ 或 /swagger-ui/index.html
 */

@Configuration
//@EnableOpenApi  //可以不配置
public class Swagger3Config {

    //缺省配置，default
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo"))
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public Docket createRestApiForDemo() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("demo-config") //分组
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //基于注解
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo.controller")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //匹配规则的url
                .build();
    }


    //只需要在认证的地方填一次
    @Bean
    public Docket createRestApiForSecurity() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("security-config")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class)) //基于注解
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo.securityController")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //任意匹配规则的url
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }


    //每次请求都要配置参数
    @Bean
    public Docket createRestApiForSecurityByGlobalRequestParameters() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("security-config-parameters")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class)) //基于注解
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo.securityController")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //任意匹配规则的url
                .build()
                .globalRequestParameters(globalRequestParameters());
    }

    /**
     * <p>每个请求都需要单独指定的参数</p>
     * 可以设置多个
     *
     * @return java.util.List<springfox.documentation.service.RequestParameter>
     * @author hanqf
     * 2020/10/10 09:43
     * <p>
     * QUERY("query"),
     * HEADER("header"),
     * PATH("path"),
     * COOKIE("cookie"),
     * FORM("form"),
     * FORMDATA("formData"),
     * BODY("body");
     */
    private List<RequestParameter> globalRequestParameters() {
        RequestParameter parameterBuilder1 = new RequestParameterBuilder()
                .in(ParameterType.HEADER)
                .name("token")
                .description("认证token")
                .required(false)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build();

        RequestParameter parameterBuilder2 = new RequestParameterBuilder()
                .in(ParameterType.HEADER)
                .name("Authorization")
                .description("认证Authorization")
                .required(false)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build();
        //return Collections.singletonList(parameterBuilder);
        return Arrays.asList(parameterBuilder1, parameterBuilder2);

    }

    /**
     * <p>统一认证参数</p>
     *
     * @return java.util.List<springfox.documentation.service.SecurityScheme>
     * @author hanqf
     * 2020/10/10 09:45
     * <p>
     * QUERY("query"),
     * HEADER("header"),
     * PATH("path"),
     * COOKIE("cookie"),
     * FORM("form"),
     * FORMDATA("formData"),
     * BODY("body");
     */
    //参考：https://blog.csdn.net/qq_38316721/article/details/103902796
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey1 = new ApiKey("Authorization", "Authorization", "header");
        ApiKey apiKey2 = new ApiKey("token", "token", "header");
        return Arrays.asList(apiKey1, apiKey2);
    }


    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );
    }

    /**
     * <p>这里的参数名称要和上面的apikey一一对应</p>
     *
     * @return java.util.List<springfox.documentation.service.SecurityReference>
     * @author hanqf
     * 2020/10/10 10:13
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(
                new SecurityReference("Authorization", authorizationScopes),
                new SecurityReference("token", authorizationScopes));
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3接口文档")
                .description("更多请咨询服务开发者hanqf。")
                .contact(new Contact("hanqf", "https://blog.hanqunfeng.com", "hanqf2008@163.com"))
                .version("1.0")
                .build();
    }


}
