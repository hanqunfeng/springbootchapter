# Springboot SpringSecurity OAuth2

## OAuth2支持获取access_token的方式
* authorization_code 验证码模式
* implicit 隐式模式
* password 密码模式
* client_credentials 客户端模式
* refresh_token 刷新token模式

## Oauth2提供的默认端点（endpoints）
```
   /oauth/authorize：授权端点
   /oauth/token：令牌端点
   /oauth/confirm_access：用户确认授权提交端点
   /oauth/error：授权服务错误信息端点
   /oauth/check_token：用于资源服务访问的令牌解析端点
   /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
```
  

## 验证码模式--authorization_code，最常用的模式
1. 浏览器GET http://localhost:8080/oauth/authorize?client_id=postman&response_type=code&redirect_uri=http://localhost:8080/redirect
    * 1.1 参数说明
    ```properties
    response_type=code #授权码认证，固定值
    client_id=postman #客户端id
    redirect_uri=http://localhost:8080/redirect #重定向url，这个值要与配置的值一致，配置这个值时可以配置一个不存在的路径
    scope=any 可选参数，指定请求范围，如果不希望跳转到确认页面而是直接通过，需要在代码中配置autoApprove(true)或者autoApprove("any")
    ```
    * 1.2 跳转到登录页面，使用`admin/123456`登录
    * 1.3 登录成功后跳转到认证确认页面(如果不希望跳转到确认页面而是直接通过，需要在代码中配置autoApprove(true))，点击`Authorize`按钮，页面会跳转到`http://localhost:8080/redirect?code=7ak4gI`
    * 1.4 复制code的值进入第二步

2. POST http://localhost:8080/oauth/token?grant_type=authorization_code&client_id=postman&client_secret=postman&redirect_uri=http://localhost:8080/redirect&code=7ak4gI
    * 2.1 code存在有效期，且只能使用一次，所以这个请求只能使用一次，参数说明
    ```properties
    grant_type=authorization_code #验证code并获取access_token，固定值
    client_id=postman #客户端id
    client_secret=postman #客户端密码
    redirect_uri=http://localhost:8080/redirect #重定向url
    code=7ak4gI #上一步中获取到的code值
    ```
    * 2.2 响应结果如下，默认access_token有效期12小时，refresh_token有效期30天
    ```json
    {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDYxMzI3MywiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJhNzFhMDJhYy02NzgyLTRiODEtOGJiMi1mMGI3ZWVkODk2YTgiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.SKl-0KbdVvv3acqLlXre66PX-fFIruTLwOkCO3peby0",
        "token_type": "bearer",
        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6ImE3MWEwMmFjLTY3ODItNGI4MS04YmIyLWYwYjdlZWQ4OTZhOCIsImV4cCI6MTYwNzE2MjA3MywiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI5NzRjMDRkOS03NTQ0LTRjZTEtODMzZS05NjlmODMyNmRiNmQiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.vy16MpRikSFFz_n2zJEgI5Cfy1APNDrvnuP7Sj1jqJU",
        "expires_in": 43199,
        "scope": "any",
        "jwt-ext": "JWT 扩展信息",
        "jti": "a71a02ac-6782-4b81-8bb2-f0b7eed896a8"
    }
    ```
    * 2.3 复制refresh_token的值进入第三步
    * 2.4 请求时支持base认证方式，详见3.3

3. POST http://localhost:8080/oauth/token?grant_type=refresh_token&client_id=postman&client_secret=postman&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6ImE3MWEwMmFjLTY3ODItNGI4MS04YmIyLWYwYjdlZWQ4OTZhOCIsImV4cCI6MTYwNzE2MjA3MywiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI5NzRjMDRkOS03NTQ0LTRjZTEtODMzZS05NjlmODMyNmRiNmQiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.vy16MpRikSFFz_n2zJEgI5Cfy1APNDrvnuP7Sj1jqJU
    * 3.1 参数说明，这个就是refresh_token模式，需要先获取到上一次的refresh_token
    ```properties
    grant_type=refresh_token #刷新token的参数，固定值
    client_id=postman #客户端id
    client_secret=postman #客户端密码
    refresh_token=xxx #上一步中获取的refresh_token值
    ```
    * 3.2 响应结果如下，获取到新的access_token和refresh_token，注意保存，access_token过期时，可以通过该请求重新获得新的access_token
    ```json
    {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDYxMzc4NSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI5YWQ1MTBhNy00MzQyLTQ5M2UtYjQyMS1iNzNmMDU2NmU3OWYiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.L0TPZ-NDzT4hSSivQ0WR-5rPz6xoLbC_HmdvvKOzP_Y",
        "token_type": "bearer",
        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6IjlhZDUxMGE3LTQzNDItNDkzZS1iNDIxLWI3M2YwNTY2ZTc5ZiIsImV4cCI6MTYwNzE2MjA3MywiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI5NzRjMDRkOS03NTQ0LTRjZTEtODMzZS05NjlmODMyNmRiNmQiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.ekAST3MRc0PqW3FZgnFQv1g-HykCqA4_TuzLRTplCIM",
        "expires_in": 43199,
        "scope": "any",
        "jwt-ext": "JWT 扩展信息",
        "jti": "9ad510a7-4342-493e-b421-b73f0566e79f"
    }
    ```
    * 3.3 请求时支持base认证方式
    
    ```shell script
    # 将client_id和client_secret放到url的前面
    POST http://postman:postman@localhost:8080/oauth/token?grant_type=refresh_token&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6ImM4NWM0YjJlLTBlNTktNDJhYy05M2RlLWI2N2I0OWI1ZDU5OSIsImV4cCI6MTYwNzIyMDAxMiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI2M2U2MGYxOS1iOTU1LTRlMmUtODk2Yy1mYWUxN2IyODUzMzkiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.jVZWYRjr4VwTYwbKXP-sav_08vwc8iAxzEbg0I_jVTw
    ```
    或者在header中增加`Authorization`请求头，值为`Basic xxxxxxxxx`，`xxxxxxxxx`是通过`base64.encode(client_id + 空格 + client_secret)`得到。
    使用Postman接口测试工具时，也可以使用其提供的认证功能[Authorization-->TYPE-->Basic Auth]，直接填写用户名和密码就可以方便进行测试，注意这里用户名和密码是client_id 和 client_secret。
    ```json
    {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY4NTE3NiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJhNDhiNzZhNC1kNWM0LTRkNTYtYmQ2Yy0zOWYxODQ0MGYwNTMiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.mhNUhWk1M9p267bOsv3fYXnLGeXitvnny6mUT9FyNdw",
        "token_type": "bearer",
        "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6ImE0OGI3NmE0LWQ1YzQtNGQ1Ni1iZDZjLTM5ZjE4NDQwZjA1MyIsImV4cCI6MTYwNzIyMDAxMiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI2M2U2MGYxOS1iOTU1LTRlMmUtODk2Yy1mYWUxN2IyODUzMzkiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.DT_cBdcow7pDv_S9RTHknKLHxZAIT45Byw1Rdw93K8U",
        "expires_in": 43199,
        "scope": "any",
        "jwt-ext": "JWT 扩展信息",
        "jti": "a48b76a4-d5c4-4d56-bd6c-39f18440f053"
    }
    ```
    * 3.4 access_token信息
    ```json
      //HEADER
      {
        "alg": "HS256",
        "typ": "JWT"
      }
      //PAYLOAD
      {
        "user_name": "admin",
        "jwt-ext": "JWT 扩展信息",
        "scope": [
          "any"
        ],
        "exp": 1604613785,
        "authorities": [
          "ROLE_admin"
        ],
        "jti": "9ad510a7-4342-493e-b421-b73f0566e79f",
        "client_id": "postman"
      }
   ```

## implicit模式
* 仅可获取access_token，不能获取refresh_token
* 浏览器GET http://localhost:8080/oauth/authorize?client_id=postman&response_type=token&redirect_uri=http://localhost:8080/redirect
* 然后登录，然后同意授权
* 返回结果直接拼接在redirect_uri的后面，因为是以`#`开头，所以服务器端无法收到数据，只能从浏览器中获取了
    ```
    http://localhost:8080/redirect#access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY3NTMwNSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI4ZDcyODM5NC0zODhjLTRiMjAtYjgwYy1jNTk4OWJjZmJlM2IiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.OStAhkT6fBoNLdF7vTaztwhfNV5J8K1MpE1A7pxbyCs&token_type=bearer&expires_in=43199&scope=any&jwt-ext=JWT%20%E6%89%A9%E5%B1%95%E4%BF%A1%E6%81%AF&jti=8d728394-388c-4b20-b80c-c5989bcfbe3b
    ```
* access_token信息
  ```json
  //HEADER
  {
    "alg": "HS256",
    "typ": "JWT"
  }
  //PAYLOAD
  {
    "user_name": "admin",
    "jwt-ext": "JWT 扩展信息",
    "scope": [
      "any"
    ],
    "exp": 1604675305,
    "authorities": [
      "ROLE_admin"
    ],
    "jti": "8d728394-388c-4b20-b80c-c5989bcfbe3b",
    "client_id": "postman"
  }
  ```

## password 模式
* 密码模式（Resource Owner Password Credentials Grant）中，用户向客户端提供自己的用户名和密码。客户端使用这些信息，向"服务商提供商"索要授权。
* 在这种模式中，用户必须把自己的密码给客户端，但是客户端不得储存密码。这通常用在用户对客户端高度信任的情况下，比如客户端是操作系统的一部分，或者由一个著名公司出品。而认证服务器只有在其他授权模式无法执行的情况下，才能考虑使用这种模式。
* POST http://localhost:8080/oauth/token?username=admin&password=123456&grant_type=password&client_id=postman&client_secret=postman
* 请求时支持base认证方式，认证用户名和密码是client_id 和 client_secret
* grant_type=password，需要携带用户的用户名和密码
* 返回结果
  ```
  {
      "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY3NzU1MiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJlNjZiMWFjOS04M2RmLTRiNTMtYjNmYS1mMTQyYzA2MTYzMjkiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.jfBeoA-AeNaMUSWHKBktN8IOFbGJHtQSIQUgxP8zzg0",
      "token_type": "bearer",
      "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImF0aSI6ImU2NmIxYWM5LTgzZGYtNGI1My1iM2ZhLWYxNDJjMDYxNjMyOSIsImV4cCI6MTYwNzIyNjM1MiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiI5MDUwNTQ0Yi00OGVmLTQ2OWMtYjM3OS1lMWQyZDViOTcyYjkiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.fGDixRXD68cYdRTjprH3TM7gAGmFqDAaKxs3RTxphMs",
      "expires_in": 43199,
      "scope": "any",
      "jwt-ext": "JWT 扩展信息",
      "jti": "e66b1ac9-83df-4b53-b3fa-f142c0616329"
  }
  ```
* access_token信息
  ```json
  //HEADER
  {
    "alg": "HS256",
    "typ": "JWT"
  }
  //PAYLOAD
  {
    "user_name": "admin",
    "jwt-ext": "JWT 扩展信息",
    "scope": [
      "any"
    ],
    "exp": 1604677552,
    "authorities": [
      "ROLE_admin"
    ],
    "jti": "e66b1ac9-83df-4b53-b3fa-f142c0616329",
    "client_id": "postman"
  }
  ```

## client_credentials 客户端模式
* 客户端模式（Client Credentials Grant）指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。严格地说，客户端模式并不属于OAuth框架所要解决的问题。在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在授权问题。
* POST http://localhost:8080/oauth/token?grant_type=client_credentials&client_id=postman&client_secret=postman
* 不需要用户的用户名和密码，只是认证客户端是否有效，返回的access_token的payload中也不包含任何用户信息(user_name,authorities)
* 没有refresh_token
* 返回结果
  ```
  {
      "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqd3QtZXh0IjoiSldUIOaJqeWxleS_oeaBryIsInNjb3BlIjpbImFueSJdLCJleHAiOjE2MDQ2Nzc4NjksImp0aSI6ImU3ZjJhYTEyLTFhMWUtNGFmMC04MjJkLTkxNzg5NmYyMGMwMyIsImNsaWVudF9pZCI6InBvc3RtYW4ifQ.OHy0IUGf9KSmCDKlqq1IZ8bICAhBHFtpazwK1gcbCOI",
      "token_type": "bearer",
      "expires_in": 43199,
      "scope": "any",
      "jwt-ext": "JWT 扩展信息",
      "jti": "e7f2aa12-1a1e-4af0-822d-917896f20c03"
  }
  ```
* access_token信息
  ```json
  //HEADER
  {
    "alg": "HS256",
    "typ": "JWT"
  }
  //PAYLOAD
  {
    "jwt-ext": "JWT 扩展信息",
    "scope": [
      "any"
    ],
    "exp": 1604677869,
    "jti": "e7f2aa12-1a1e-4af0-822d-917896f20c03",
    "client_id": "postman"
  }
  ```


## refresh_token模式，见验证码模式第3部分
通过已有的refresh_token获取新的access_token和refresh_token