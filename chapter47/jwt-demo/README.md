# Springboot SpringSecurity JWT

## 说明
* Json web token (JWT)，Jwt信息分三部分，第一部分我们称它为头部(header),第二部分我们称其为载荷(payload, 有效数据，如用户名称)，第三部分是签证(signature防篡改，base64加密后的header+base64加密后的payload+密钥);
* Jwt认证是STATELESS无状态的，所以很适合集群部署，因为服务器端不存储任何用户状态，客户端每次请求资源都需要携带token，服务器端负责校验token的有效性;
* Jwt认证很适合前后端分离的项目，比如手机客户端与服务器端的接口调用;
* 客户端每次请求都需要携带token，所以客户端需要保存token;
* token是有时间限制的，所以客户端需要定期请求服务器端进行token刷新;
* 防止token被劫持，正式环境必须使用https协议;
* 获取token和刷新token的controller可以单独部署到一个服务中作为认证服务器，流程为客户端先去认证服务器获取token，然后携带token去访问受保护的服务资源

## 知识点
* jwt认证时如果请求中没有携带token或者token无效时会提示访问被拒绝，返回客户端一个403状态的json信息，但是这个信息格式与系统的全局响应方式不一致，所以需要对其响应进行封装，
实际上，这个json响应是由BasicErrorController的error方法进行处理的，也就是说，发生认证失败时，会将异常信息重定向到`/error`，交给BasicErrorController的errer方法进行处理，
我们只需要自定义一个controller继承自BasicErrorController，并重写error方法即可。
```java
@Override
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
    Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL).including(ErrorAttributeOptions.Include.MESSAGE));
    HttpStatus status = getStatus(request);
    throw new CustomException(status, body.getOrDefault("message","抱歉，您的token无效或过期").toString(), body);
}
```

* jwt的token认证通过后，如果是因为没有权限而不能访问资源，可以自定义一个AccessDeniedHandler，重写其handle方法，如本例中的CustomAccessDeniedHandler，然后将其配置到`http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);`
```java
@Override
public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp",new Date());
    body.put("status",403);
    body.put("error","Forbidden");
    body.put("message",e.getMessage());
    body.put("path",request.getRequestURI());

    AjaxResponse ajaxResponse = AjaxResponse.error(new CustomException(HttpStatus.FORBIDDEN, "抱歉，您没有访问该接口的权限",body));
    response.setStatus(403);
    response.setContentType("application/json;charset=utf-8");
    response.setCharacterEncoding("UTF-8");
    try (PrintWriter writer = response.getWriter()) {
        writer.write(objectMapper.writeValueAsString(ajaxResponse));
    }
}
```
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



