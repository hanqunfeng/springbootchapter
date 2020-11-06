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
	void encodePasswoord() {
		System.out.println(passwordEncoder.encode("demo-client"));
	}

}
