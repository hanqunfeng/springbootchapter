## 插件功能
* 1.为model对象添加@Data注解
* 2.删除已经存在的mapper.xml，避免重新创建时生成合并的mapper.xml

## 插件使用方法
```shell
# 进入jar包所在目录
cd $maven_repository/org/mybatis/generator/mybatis-generator-core/1.4.2
# 将生成的class文件复制到指定的路径
mkdir -p org/mybatis/generator/plugins
cp xxx/xxx/LombokPlugin.class ./org/mybatis/generator/plugins/

# 将class文件写入jar包，这里注意要带上路径，写入的位置就是这个路径
jar -uvf mybatis-generator-core-1.4.2.jar org/mybatis/generator/plugins/LombokPlugin.class
```
备注：以上jar命令中的参数解释

-u 更新存在的文件
-v 查看详细日志输出
-f 指定文件名

* 批量更新
```shell
# 将org下的所有目录及文件都写入jar
jar -uvf mybatis-generator-core-1.4.2.jar org/*
```

