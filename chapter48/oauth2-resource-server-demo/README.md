# Springboot SpringSecurity OAuth2 资源服务器
* 如果已经获取到access_token，则访问资源服务器时，资源服务器负责解析token，不需要访问认证服务器。

## 通过access_token访问受保护的资源 

### 有两种方式: 

1. url中追加access_token参数

    ```
    http://localhost:8081/user?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY1ODc4NiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJjYjEzZjhmZC03NWRiLTRmODItOTkxOC00YzFjZGI3MDEwMGMiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.W78nue0rPxB-Te7ZsxfzmTUYTasHHfQT0lMgAMG_i5g
    ```

2. 在请求的header中设置参数：参数名称：Authorization，值是`[grant_type] [access_token]`，grant_type值与access_token值之间用空格分开。例如：

    ```
    bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImp3dC1leHQiOiJKV1Qg5omp5bGV5L-h5oGvIiwic2NvcGUiOlsiYW55Il0sImV4cCI6MTYwNDY1ODc4NiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiJdLCJqdGkiOiJjYjEzZjhmZC03NWRiLTRmODItOTkxOC00YzFjZGI3MDEwMGMiLCJjbGllbnRfaWQiOiJwb3N0bWFuIn0.W78nue0rPxB-Te7ZsxfzmTUYTasHHfQT0lMgAMG_i5g
    ```
    使用Postman接口测试工具时，也可以使用其提供的认证功能[Authorization-->TYPE--> Bearer Token]，然后将access_token填入
