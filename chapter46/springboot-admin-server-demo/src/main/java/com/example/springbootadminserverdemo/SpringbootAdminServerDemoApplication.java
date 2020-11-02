package com.example.springbootadminserverdemo;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class SpringbootAdminServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAdminServerDemoApplication.class, args);
	}

}
