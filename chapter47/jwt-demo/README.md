# Springboot SpringSecurity JWT

## 说明
* Json web token (JWT)，Jwt信息分三部分，第一部分我们称它为头部(header),第二部分我们称其为载荷(payload, 有效数据，如用户名称)，第三部分是签证(signature防篡改，base64加密后的header+base64加密后的payload+密钥).
* Jwt认证是STATELESS无状态的，所以很适合集群部署，因为服务器端不存储任何用户状态，客户端每次请求资源都需要携带token，服务器端负责校验token的有效性；
* Jwt认证很适合前后端分离的项目，比如手机客户端与服务器端的接口调用；
* 客户端每次请求都需要携带token，所以客户端需要保存token;
* token是有时间限制的，所以客户端需要定期请求服务器端进行token刷新;
* 防止token被劫持，正式环境必须使用https协议。

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



