package com.example;

import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

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
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Spring!");
    }

    @Test
    public void testIndex() {
        webTestClient.get()
                .uri("/index")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Index, Spring!");
    }

    @Test
    public void testDemo() {
        webTestClient.get()
                .uri("/demo/100")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Demo 100");
    }

    @Test
    public void testUser() {
        webTestClient.get()
                .uri("/user/1")
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testUserAll() {
        webTestClient.get()
                .uri("/users")
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).consumeWith(System.out::println);
    }


    @Test
    public void testUserName() {
        webTestClient.get()
                .uri("/username/张三")
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testUa() {
        webTestClient.get()
                .uri("/ua")
                .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testTime() {
        webTestClient.get()
                .uri("/time")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(System.out::println);
    }

    @Test
    public void testDate() {
        webTestClient.get()
                .uri("/date")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(System.out::println);
    }

}