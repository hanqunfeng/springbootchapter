package com.example.swagger3openapigradledemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Swagger3OpenapiGradleDemoApplication {

	public static void main(String[] args) {
		//设置hibernate的dialect，这里设置为mysql的innodb，因为MySQL5InnoDBDialect已过时，作者建议使用环境变量设置
		System.setProperty("hibernate.dialect.storage_engine","innodb");
		SpringApplication.run(Swagger3OpenapiGradleDemoApplication.class, args);
	}

}
