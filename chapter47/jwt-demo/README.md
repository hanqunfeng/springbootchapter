# Springboot SpringSecurity JWT

## 依赖
```yaml
dependencies {
    //jwt工具类
    runtime 'io.jsonwebtoken:jjwt-impl:0.11.2'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.11.2'
}
```
## JwtTokenUtil
jwt工具类，负责token相关操作

## JwtAuthController
负责获取token和刷新token

## JwtAuthenticationTokenFilter
负责验证token是否有效

## CustomErrorController
请求头不含有jwt token数据，或者含有token但是token不合法，即无效或者过期时负责抛出自定义异常

## CustomAccessDeniedHandler
登录验证成功，即token合法，但是没有权限访问资源时负责封装响应数据返回给用户

## WebExceptionHandler
全局异常处理器，负责将异常信息以统一的格式返回给用户

## WebSecurityConfigByDefault
SpringSecurity配置类



