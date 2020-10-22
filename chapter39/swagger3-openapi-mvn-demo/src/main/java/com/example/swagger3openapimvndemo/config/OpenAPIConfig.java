package com.example.swagger3openapimvndemo.config;

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
 * <h1>springdoc openapi 配置类</h1>
 * Created by hanqf on 2020/10/15 18:11.
 *
 * 参考：https://swagger.io/docs/specification/authentication/
 *     BasicAuth:
 *       type: http
 *       scheme: basic
 *
 *     BearerAuth:
 *       type: http
 *       scheme: bearer
 *
 *     ApiKeyAuth:
 *       type: apiKey
 *       in: header
 *       name: X-API-Key
 *
 *     OpenID:
 *       type: openIdConnect
 *       openIdConnectUrl: https://example.com/.well-known/openid-configuration
 *
 *     OAuth2:
 *       type: oauth2
 *       flows:
 *         authorizationCode:
 *           authorizationUrl: https://example.com/oauth/authorize
 *           tokenUrl: https://example.com/oauth/token
 *           scopes:
 *             read: Grants read access
 *             write: Grants write access
 *             admin: Grants access to admin operations
 *
 * @SecurityScheme配置：
 * oauth2:
 * @SecurityScheme(type = SecuritySchemeType.OAUTH2,
 *         flows = @OAuthFlows(
 *                 authorizationCode = @OAuthFlow(authorizationUrl = "https://example.com/oauth/authorize",
 *                 tokenUrl = "https://example.com/oauth/token",
 *                 scopes = {
 *                         @OAuthScope(name = "read",description = "Grants read access"),
 *                         @OAuthScope(name = "write",description = "Grants write access"),
 *                         @OAuthScope(name = "admin",description = "Grants access to admin operations")
 *                 })
 *         ))
 *
 * apiKey:
 * @SecurityScheme(type = SecuritySchemeType.APIKEY, name = "X-API-Key", in = SecuritySchemeIn.HEADER, paramName = "token")
 *
 * basic:
 * @SecurityScheme(name = "api", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
 *
 *
 * openId:
 * @SecurityScheme(name = "openid",type = SecuritySchemeType.OPENIDCONNECT,openIdConnectUrl = "https://example.com/.well-known/openid-configuration")
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
public class OpenAPIConfig {

    //分组
    @Bean
    public GroupedOpenApi articalsApi() {
        return GroupedOpenApi.builder()
                .group("articals")
                .pathsToMatch("/articals/**")
                .build();
    }
}

