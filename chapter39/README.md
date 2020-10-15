## 参考
* https://github.com/herionZhang/gradle-parent
* https://www.jianshu.com/p/7a24d202b395
* [spring boot:用swagger3生成接口文档，支持全局通用参数(swagger 3.0.0 / spring boot 2.3.2)](https://www.cnblogs.com/architectforest/p/13470170.html)
* [使用Gradle发布jar到Maven中央仓库](https://segmentfault.com/a/1190000018026290)

## gradle官方用户手册
[https://docs.gradle.org/current/userguide/userguide.html](https://docs.gradle.org/current/userguide/userguide.html)

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


## asciidoctor adoc格式转html或者pdf
* [官网](https://github.com/asciidoctor/asciidoctor/blob/master/README-zh_CN.adoc)
## mac安装
$ brew install asciidoctor
$ asciidoctor -v

## 转html
$ asciidoctor index.adoc #当前目录下生成index.html
$ asciidoctor -D output index.adoc # 输出到指定路径

## 转pdf
$ asciidoctor-pdf -a pdf-style=basic-theme.yml -a pdf-fontsdir=/Library/Fonts/Microsoft index.adoc

### 说明
* basic-theme.yml，参考：[http://gist.asciidoctor.org/?github-asciidoctor%2Fasciidoctor-pdf%2F%2Fdocs%2Ftheming-guide.adoc](http://gist.asciidoctor.org/?github-asciidoctor%2Fasciidoctor-pdf%2F%2Fdocs%2Ftheming-guide.adoc)
```yaml
extends: base

font:
  catalog:
    merge: false # set value to true to merge catalog with theme you're extending
    SimHei: #这里设置字体名称，解决中文乱码，所以要设置一个中文字体
      normal: SimHei.ttf
      italic: SimHei.ttf
      bold: SimHei.ttf
      bold_italic: SimHei.ttf

page:
  layout: portrait
  margin: [0.75in, 1in, 0.75in, 1in]
  size: Letter
base:
  font-color: #333333
  font-family: SimHei
  font-size: 12
  line-height-length: 17
  line-height: $base-line-height-length / $base-font-size
vertical-spacing: $base-line-height-length
heading:
  font-color: #262626
  font-size: 17
  font-style: bold
  line-height: 1.2
  margin-bottom: $vertical-spacing
link:
  font-color: #002FA7
outline-list:
  indent: $base-font-size * 1.5
footer:
  height: $base-line-height-length * 2.5
  line-height: 1
  recto:
    right:
      content: '{page-number}'
  verso:
    left:
      content: $footer-recto-right-contentRADLE_HOME=$MY_HOME/develop_soft/gradle

```
* pdf-fontsdir 字体文件路径，多个路径逗号分割，pdf-fontsdir=path/to/fonts,path/to/more-fonts，如果路径整体使用双引号括起来，则使用;号分割，pdf-fontsdir="path/to/fonts;GEM_FONTS_DIR"

