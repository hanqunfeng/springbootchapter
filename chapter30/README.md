# SpringBoot jar包瘦身，方便部署
## pom.xml
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <layout>ZIP</layout>  # 一定要加上这个才行
        <includes>
            <include>  # 这里说明打包时不包含任何依赖
                <groupId>nothing</groupId>
                <artifactId>nothing</artifactId>
            </include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal> # 指定触发时机，运行 mvn package 即可
            </goals>
        </execution>
    </executions>
</plugin>
```

## 打包当前项目
```bash
mvn clean package #此时生成的jar中是不包含依赖的，所以生成的jar非常小
```

## 导出依赖jar到指定路径
```bash
mvn dependency:copy-dependencies -DoutputDirectory=target/lib -DincludeScope=compile
```

## 运行项目jar
```bash
java -Dloader.path=target/lib -jar target/chapter30-0.0.1-SNAPSHOT.jar

# loader.path用来指定外部依赖路径,多个使用逗号分割
```

## 说明
这种方式是将内部依赖改为外部依赖，同时不影响本地开发测试
