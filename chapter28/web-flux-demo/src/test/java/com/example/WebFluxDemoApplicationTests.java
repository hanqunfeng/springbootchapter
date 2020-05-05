package com.example;

import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).consumeWith(System.out::println);
    }

    @Test
    public void testUserAll() {
        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).consumeWith(System.out::println);
    }

}
