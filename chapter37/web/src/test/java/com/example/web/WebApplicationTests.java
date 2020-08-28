package com.example.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

//
/**
 * 如果您使用的是JUnit 4，请不要忘记添加@RunWith(SpringRunner.class)进行测试，否则注解将被忽略。
 * 如果您使用的是JUnit 5，则不需要添加与@SpringBootTest等价的@ExtendWith(SpringExtension.class)。
 *
 * webEnvironment: 默认为Mock模式，这里配置为随机端口模式，启动容器时端口随机
 *
 *
 * @DisplayName("我的测试类") 用于指定测试类或方法的名称，默认为类和方法名称
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("我的测试类")
class WebApplicationTests {

    /**
     * 相当于 @Value("${local.server.port}")；
     * 与webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT 配合使用
     * 此时就是随机端口的值
    */
    @LocalServerPort
    private int port;


    /**
     * TestRestTemplate使用方法与RestTemplate一致，只是这里不需要启动服务就可以测试
     * 与webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT 配合使用
    */
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void beforeEach(){
        System.out.println("port=="+port);
    }

    @Test
    @DisplayName("我的测试方法")
    void test1() {

        String forObject = restTemplate.getForObject("/index/zhangsan", String.class);
        System.out.println(forObject);
        Assertions.assertEquals("zhangsan",forObject);
    }

}
