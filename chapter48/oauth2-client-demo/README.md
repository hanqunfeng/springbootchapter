# Springboot SpringSecurity OAuth2-Client

## 使用方式
* 浏览器 http://localhost:8088/postman
* 跳转到任务服务器的登录页面，输入用户名和密码，成功后跳转回http://localhost:8088/postman
* 请求资源服务器的数据：http://localhost:8088/postman/res/user 正常显示就为成功

## 说明
* 只实现了单点登录，没有实现单点登出
* 可以访问资源服务器的数据，在restTemplate中增加header的认证数据
