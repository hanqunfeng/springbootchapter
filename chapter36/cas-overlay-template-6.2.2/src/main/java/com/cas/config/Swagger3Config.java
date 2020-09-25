package com.cas.config;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
@EnableOpenApi
//@EnableSwagger2
public class Swagger3Config {

    @Bean
    public Docket createRestApiForService() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("service-config")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //基于注解
                //.apis(RequestHandlerSelectors.basePackage("com.cas.controller")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/service/**")) //任意匹配规则的url
                .build();
    }

    @Bean
    public Docket createRestApiForCaptcha() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("captcha-create")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class)) //基于注解
                //.apis(RequestHandlerSelectors.basePackage("com.cas.controller")) //基于扫描路径
                //.apis(RequestHandlerSelectors.any()) //基于url
                .paths(PathSelectors.any()) //任意的url
                //.paths(PathSelectors.ant("/checkcode/**")) //任意匹配规则的url
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3接口文档")
                .description("更多请咨询服务开发者hanqf。")
                .contact(new Contact("hanqf", "https://blog.hanqunfneg.com", "hanqf2008@163.com"))
                .version("1.0")
                .build();
    }

    //官网：https://springdoc.org
    //springdoc.swagger-ui.path=/swagger-ui.html
    //springdoc.api-docs.path=/api-docs
    //@Bean
    //public GroupedOpenApi servcieApi() {
    //    return GroupedOpenApi.builder()
    //            .group("service-config")
    //            .pathsToMatch("/service/**")
    //            .build();
    //}
    //
    //@Bean
    //public GroupedOpenApi captchaApi() {
    //    return GroupedOpenApi.builder()
    //            .group("captcha-create")
    //            .pathsToMatch("/checkcode/**")
    //            .build();
    //}


}
