package com.example.chapter43;

import com.example.utils.JasyptUtil;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class Chapter43ApplicationTests {

	/**
	 * 引入jasypt-spring-boot-starter就会自动注入
	*/
	@Resource
	private StringEncryptor stringEncryptor;

	@Test
	void StringEncryptor() {
		String encrypt = stringEncryptor.encrypt("newpwd");
		System.out.println(encrypt);

		String decrypt = stringEncryptor.decrypt(encrypt);
		System.out.println(decrypt);

		//使用工具类解密
		String s = JasyptUtil.decrypt("123456", encrypt);
		System.out.println(s);
	}

}
