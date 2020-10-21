package com.example.log4j2demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Log4j2DemoApplication {

	public static void main(String[] args) {
		//开启全局异步日志，如果是同步模式或者同步异步混合模式，则需要哦注释掉该设置
		//System.setProperty("Log4jContextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		SpringApplication.run(Log4j2DemoApplication.class, args);
	}

}
