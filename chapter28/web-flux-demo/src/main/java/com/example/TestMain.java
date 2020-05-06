package com.example;

import com.example.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:38.
 */


public class TestMain {

    private static final WebClient CLIENT = WebClient.create("http://localhost:8080");

    private static final Mono<ClientResponse> HELLO = CLIENT.get()
            .uri("/hello")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getHello() {
        return ">> result = " + HELLO.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static final Mono<ClientResponse> INDEX = CLIENT.get()
            .uri("/index")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getIndex() {
        return ">> result = " + INDEX.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static final Mono<ClientResponse> DEMO = CLIENT.get()
            .uri("/demo/100")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getDemo() {
        return ">> result = " + DEMO.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static final Mono<ClientResponse> USER = CLIENT.get()
            .uri("/user/1")
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
            .exchange();

    private static String getUser() {
        User block = USER.flatMap(res -> res.bodyToMono(User.class)).block();
        return ">> result = " + block;
    }

    private static final Mono<ClientResponse> USER_ALL = CLIENT.get()
            .uri("/users")
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8)).exchange();

    private static String getUserAll() {
        List<User> block = USER_ALL.flatMap(res -> res.bodyToFlux(User.class).collectList()).block();
        return ">> result = " + block;
    }

    private static final Mono<ClientResponse> USERNAME = CLIENT.get()
            .uri("/username/张三")
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
            .exchange();

    private static String getUserName() {
        User block = USERNAME.flatMap(res -> res.bodyToMono(User.class)).block();
        return ">> result = " + block;
    }

    private static final Mono<ClientResponse> UA = CLIENT.get()
            .uri("/ua")
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8)).exchange();

    private static String getUa() {
        List<User> block = UA.flatMap(res -> res.bodyToFlux(User.class).collectList()).block();
        return ">> result = " + block;
    }
    public static void main(String[] args) {
        System.out.println(TestMain.getHello());
        System.out.println(TestMain.getIndex());
        System.out.println(TestMain.getDemo());
        System.out.println(TestMain.getUser());
        System.out.println(TestMain.getUserAll());
        System.out.println(TestMain.getUserName());
        System.out.println(TestMain.getUa());
    }
}
