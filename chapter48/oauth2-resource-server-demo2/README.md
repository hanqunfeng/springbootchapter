# Springboot SpringSecurity OAuth2 资源服务器
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          # 公钥文件路径
          # public-key-location: classpath:oauth2_key.pub

          # 认证服务器提供的密钥验证路径
          jwk-set-uri: http://localhost:8080/.well-known/jwks.json

```
* public-key-location：如果已经获取到access_token，则访问资源服务器时，资源服务器负责解析token，不需要访问认证服务器。
* jwk-set-uri: 每次验证token时都需要访问认证服务器的这个地址

## 通过access_token访问受保护的资源 

### 只支持Bearer Token: 

    ```
    http://localhost:8082/user
    
    # 在请求的header中设置参数：参数名称：Authorization，值是`[grant_type] [access_token]`，grant_type值与access_token值之间用空格分开。例如：

    bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY1ODc4NiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJjYjEzZjhmZC03NWRiLTRmODItOTkxOC00YzFjZGI3MDEwMGMiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.W78nue0rPxB-Te7ZsxfzmTUYTasHHfQT0lMgAMG_i5g
    ```
    使用Postman接口测试工具时，也可以使用其提供的认证功能[Authorization-->TYPE--> Bearer Token]，然后将access_token填入
