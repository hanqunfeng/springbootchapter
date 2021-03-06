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
* [adoc语法](https://blog.csdn.net/qq_36135928/article/details/97931495?utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~all~top_click~default-1-97931495.nonecase&utm_term=asciidoc&spm=1000.2123.3001.4430)

## mac安装
$ brew install asciidoctor
$ asciidoctor -v

## 转html
$ asciidoctor index.adoc #当前目录下生成index.html
$ asciidoctor -D output index.adoc # 输出到指定路径
$ asciidoctor -a toc=left -a toclevels=3 -a doctype=book -a numbered=true index.adoc # 带左侧目录导航

## 转pdf
* 参考1：[http://gist.asciidoctor.org/?github-asciidoctor%2Fasciidoctor-pdf%2F%2Fdocs%2Ftheming-guide.adoc](http://gist.asciidoctor.org/?github-asciidoctor%2Fasciidoctor-pdf%2F%2Fdocs%2Ftheming-guide.adoc)
* 参考2：[https://intellij-asciidoc-plugin.ahus1.de/docs/users-guide/features/advanced/pdf-non-latin-languages.html](https://intellij-asciidoc-plugin.ahus1.de/docs/users-guide/features/advanced/pdf-non-latin-languages.html) 
* 字体下载：[https://github.com/chloerei/asciidoctor-pdf-cjk-kai_gen_gothic/releases](https://github.com/chloerei/asciidoctor-pdf-cjk-kai_gen_gothic/releases)
* pdf-fontsdir 字体文件路径，多个路径逗号分割，pdf-fontsdir=path/to/fonts,path/to/more-fonts，如果路径整体使用双引号括起来，则使用;号分割，pdf-fontsdir="path/to/fonts;GEM_FONTS_DIR

### 命令
```
asciidoctor-pdf -a pdf-style=/Users/hanqf/asciidoctor-pdf/themes/zh_CN-theme.yml -a pdf-fontsdir=/Users/hanqf/asciidoctor-pdf/fonts -a toc=left -a toclevels=3 -a doctype=book -a numbered=true index.adoc
```

* zh_CN-theme.yml
```
# default theme at https://github.com/asciidoctor/asciidoctor-pdf/blob/master/data/themes/default-theme.yml
extends: default
font:
  fallbacks:
    - kaigen-gothic-cn
  catalog:
    # These are the KaiGen Gothic CN fonts, download them from
    # https://github.com/minjiex/kaigen-gothic/tree/master/dist/CN
    kaigen-gothic-cn:
      normal: KaiGenGothicCN-Regular.ttf
      bold: KaiGenGothicCN-Bold.ttf
      italic: KaiGenGothicCN-Regular.ttf
      bold_italic: KaiGenGothicCN-Bold.ttf
    Noto Serif:
      normal: GEM_FONTS_DIR/notoserif-regular-subset.ttf
      bold: GEM_FONTS_DIR/notoserif-bold-subset.ttf
      italic: GEM_FONTS_DIR/notoserif-italic-subset.ttf
      bold_italic: GEM_FONTS_DIR/notoserif-bold_italic-subset.ttf
    # M+ 1mn supports ASCII and the circled numbers used for conums
    M+ 1mn:
      normal: GEM_FONTS_DIR/mplus1mn-regular-subset.ttf
      bold: GEM_FONTS_DIR/mplus1mn-bold-subset.ttf
      italic: GEM_FONTS_DIR/mplus1mn-italic-subset.ttf
      bold_italic: GEM_FONTS_DIR/mplus1mn-bold_italic-subset.ttf
base:
  font_family: kaigen-gothic-cn
```


### idea AsciiDoc插件
Preferences | Languages & Frameworks | AsciiDoc
在最下面的Attributes栏中配置相关属性


## asciidoc 转 markdown
### 安装pandoc
`brew install pandoc`

### 执行转换
* [先将asciidoc转成docbook格式](https://www.pandoc.org/getting-started.html)：
`asciidoctor -b docbook index.adoc`
* [再将docbook转换为markdown格式](https://asciidoctor.org/docs/convert-documents/#converting-a-document-to-docbook)：
`pandoc -f docbook -t markdown_strict index.xml -o index.md`   

#### 扩展，markdown转html
`pandoc -f markdown -t html index.md -o index.html`
* 这种方式转换后的html是不包含html元素的，需要自己补充上，将生成的内容粘贴到下面的###处即可
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>
  ###
</body>
</html>
```
#### 说明
使用pandoc转markdown时，如果-t markdown 则转换后的table格式是不标准的，这里使用markdown_strict，则table会被转换为html的table格式



## asciidoc 引入其它文件：
`include::../generated-snippets/demoMap/curl-request.adoc[]`




