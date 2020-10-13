## 参考
* https://github.com/herionZhang/gradle-parent
* https://www.jianshu.com/p/7a24d202b395
* [spring boot:用swagger3生成接口文档，支持全局通用参数(swagger 3.0.0 / spring boot 2.3.2)](https://www.cnblogs.com/architectforest/p/13470170.html)
* [使用Gradle发布jar到Maven中央仓库](https://segmentfault.com/a/1190000018026290)

## YApi 可视化接口管理平台

https://gitee.com/suxiaoxin123/yapi

npm install -g yapi-cli --registry https://registry.npm.taobao.org
yapi server 

需要开启mongodb

管理员登录后：
> use yapi;
> db.createUser(
   {
     user: "yapiuser",
     pwd: "yapipassword",
     roles: [ { role: "dbOwner", db: "yapi" } ]
   }
  );  


http://0.0.0.0:9090/ 设置相应信息

初始化管理员账号成功,账号名："hanqf2008@163.com"，密码："ymfe.org"

部署成功，请切换到部署目录，输入： "node vendors/server/app.js" 指令启动服务器, 然后在浏览器打开 http://127.0.0.1:3000 访问

## smart-doc
https://gitee.com/smart-doc-team/smart-doc

gradle插件：https://gitee.com/smart-doc-team/smart-doc-gradle-plugin

编译报错：
Could not find snakeyaml-1.23-android.jar (org.yaml:snakeyaml:1.23)

直接从maven仓库中下载对应的包到本地：
https://mvnrepository.com/artifact/org.yaml/snakeyaml/1.23 点击view all

snakeyaml-1.23-android.jar
snakeyaml-1.23-android.jar.sha1



./gradlew smartDocRestHtml