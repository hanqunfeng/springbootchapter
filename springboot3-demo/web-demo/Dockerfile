FROM openjdk:17-ea-slim
MAINTAINER hanqf

WORKDIR /usr/local/web-demo

#注意add的文件必须在Dockerfile所在文件夹及其子目录下才可以被访问到，否则会报Dockerfile ADD failed to compute cache key
ADD ./target/web-demo-0.0.1-SNAPSHOT.jar app.jar

#指令是声明运行时容器提供服务端口,这只是一个声明,在运行时并不会因为这个声明应用就会开启这个端口的服务
EXPOSE 8001

ENTRYPOINT ["sh","-c","java -jar app.jar"]
