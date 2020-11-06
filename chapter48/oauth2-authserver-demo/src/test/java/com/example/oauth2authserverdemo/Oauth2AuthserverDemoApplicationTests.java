package com.example.oauth2authserverdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class Oauth2AuthserverDemoApplicationTests {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Test
	void encodePassword() {
		System.out.println("postman:" + passwordEncoder.encode("postman"));
		System.out.println("demo-client:" + passwordEncoder.encode("demo-client"));
	}

}
