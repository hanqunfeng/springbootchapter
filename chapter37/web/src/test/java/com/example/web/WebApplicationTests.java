package com.example.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//
/**
 * 如果您使用的是JUnit 4，请不要忘记添加@RunWith(SpringRunner.class)进行测试，否则注解将被忽略。
 * 如果您使用的是JUnit 5，则不需要添加与@SpringBootTest等价的@ExtendWith(SpringExtension.class)。
 *
 * webEnvironment: 默认为Mock模式，这里配置为随机端口模式，启动容器时端口随机
*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebApplicationTests {

    @Test
    void contextLoads() {
        int a = 1;
        int b = 11;
        //junit5的断言对象
        Assertions.assertEquals(a, b,"值不相等");
    }

}
