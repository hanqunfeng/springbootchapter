package com.example;

import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //使用随机端口启动server
class WebFluxDemoApplicationTests {


    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void testHello() {
        webTestClient.get()
                .uri("/hello")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Spring!");
    }

    @Test
    public void testIndex() {
        webTestClient.get()
                .uri("/index")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Index, Spring!");
    }

    @Test
    public void testDemo() {
        webTestClient.get()
                .uri("/demo/100")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Demo 100");
    }

    @Test
    public void testUser() {
        webTestClient.get()
                .uri("/user/1")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testUserAll() {
        webTestClient.get()
                .uri("/users")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).consumeWith(System.out::println);
    }


    @Test
    public void testUserName() {
        webTestClient.get()
                .uri("/username/张三")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testUa() {
        webTestClient.get()
                .uri("/ua")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testTime() {
        webTestClient.get()
                .uri("/time")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(System.out::println);
    }

    @Test
    public void testDate() {
        webTestClient.get()
                .uri("/date")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(System.out::println);
    }

}
