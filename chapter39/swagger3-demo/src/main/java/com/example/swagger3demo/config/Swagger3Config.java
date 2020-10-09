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

import java.util.Collections;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/9/21 14:25.
 * 参考：https://www.cnblogs.com/ruiyeclub/p/13334826.html
 * Swagger3  -> springdoc
 * @ApiParam -> @Parameter
 * @ApiOperation -> @Operation
 * @Api -> @Tag
 * @ApiImplicitParams -> @Parameters
 * @ApiImplicitParam -> @Parameter
 * @ApiIgnore -> @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
 * @ApiModel -> @Schema
 * @ApiModelProperty -> @Schema
 */

@Configuration
//@EnableOpenApi  //可以不配置
public class Swagger3Config {

    // 访问路径：/swagger-ui/ 或 /swagger-ui/index.html
    @Bean
    public Docket createRestApiForDemo() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("demo-config")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //基于注解
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo.controller")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //任意匹配规则的url
                .build();
    }


    //只需要在认证的地方填一次
    @Bean
    public Docket createRestApiForSecurity() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("security-config")
                .select()
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //基于注解
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
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //基于注解
                .apis(RequestHandlerSelectors.basePackage("com.example.swagger3demo.securityController")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //任意匹配规则的url
                .build()
                .globalRequestParameters(globalRequestParameters());
    }

    private List<RequestParameter> globalRequestParameters() {
        RequestParameterBuilder parameterBuilder = new RequestParameterBuilder()
                .in(ParameterType.HEADER)
                .name("token")
                .description("认证token")
                .required(false)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)));
        return Collections.singletonList(parameterBuilder.build());
    }

    //参考：https://blog.csdn.net/qq_38316721/article/details/103902796
    private List<SecurityScheme> securitySchemes() {
        //ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        ApiKey apiKey = new ApiKey("token", "token", "header");
        return Collections.singletonList(apiKey);
    }


    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                //new SecurityReference("Authorization", authorizationScopes));
                new SecurityReference("token", authorizationScopes));
    }




    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3接口文档")
                .description("更多请咨询服务开发者hanqf。")
                .contact(new Contact("hanqf", "https://blog.hanqunfneg.com", "hanqf2008@163.com"))
                .version("1.0")
                .build();
    }



}
