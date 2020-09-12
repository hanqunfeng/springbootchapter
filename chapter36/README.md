# chapter36----Springboot SpringSecurity，CAS 
* 因本地只安装了jdk1.8，所以cas-server使用的是5.3.14，其是支持jdk1.8的最后一个版本

## cas单点退出
services目录下需要为每个客户端配置一个json文件，如client1-10000005.json

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|http|imaps)://localhost:8081.*", #对应客户端的地址匹配路径
  "name" : "client1",  #与文件名称对应
  "id" : 10000005,     #与文件名称对应
  "description" : "This service definition authorizes all application urls that support HTTPS and IMAPS protocols.",
  "evaluationOrder" : 10, #顺序id不能重复
  "logoutType" : "BACK_CHANNEL", #固定写法
  "logoutUrl" : "http://localhost:8081/",  #客户端地址
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
  },
  "accessStrategy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy",
    "enabled" : true,
    "ssoEnabled" : true
  }
}

```




