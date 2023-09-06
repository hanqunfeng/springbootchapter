package com.example;

import com.example.mysql.SysUser;
import com.example.mysql.SysUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
class WebFluxMysqlRedisDemoApplicationTests {

	@Autowired
	private TransactionalOperator transactionalOperator;

	@Autowired
	private SysUserRepository sysUserRepository;

	@Test
	void testAddSysUser() {
		SysUser sysUser = new SysUser();
		sysUser.setId(UUID.randomUUID().toString());
		sysUser.setUsername("testAddSysUser");
		sysUser.setPassword("testAddSysUser");
		sysUser.setEnable(true);
		sysUserRepository.addSysUser(sysUser)
				.as(transactionalOperator::transactional)
				.log()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();

	}


}
