## Mybatis插件功能
* 1.为model对象添加@Data注解
* 2.删除已经存在的mapper.xml，避免重新创建时生成合并的mapper.xml
* 3.为MapperClass或者Mapper.xml的insert方法添加 `useGeneratedKeys = true`

## 插件使用方法
```shell
# 进入jar包所在目录
cd $MAVEN_REPOSITORY/org/mybatis/generator/mybatis-generator-core/1.4.2
# 将生成的class文件复制到指定的路径
mkdir -p org/mybatis/generator/plugins
cp xxx/xxx/CustomerPlugin.class ./org/mybatis/generator/plugins/

# 将class文件写入jar包，这里注意要带上路径，写入的位置就是这个路径
jar -uvf mybatis-generator-core-1.4.2.jar org/mybatis/generator/plugins/CustomerPlugin.class
```
备注：以上jar命令中的参数解释

-u 更新存在的文件
-v 查看详细日志输出
-f 指定文件名

## 扩展
* 批量更新
```shell
# 将org下的所有目录及文件都写入jar
jar -uvf mybatis-generator-core-1.4.2.jar org/*
```

* 从jar中删除指定的文件
```shell
# 安装7z
brew install p7zip 

# 删除指定的文件
7z d mybatis-generator-core-1.4.2.jar org/mybatis/generator/plugins/LombokPlugin.class
```

