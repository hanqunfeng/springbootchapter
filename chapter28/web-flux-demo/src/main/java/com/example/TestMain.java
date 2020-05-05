package com.example;

import com.example.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:38.
 */


public class TestMain {

    private static WebClient client = WebClient.create("http://localhost:8080");

    private static Mono<ClientResponse> hello = client.get()
            .uri("/hello")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getHello() {
        return ">> result = " + hello.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static Mono<ClientResponse> index = client.get()
            .uri("/index")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getIndex() {
        return ">> result = " + index.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static Mono<ClientResponse> demo = client.get()
            .uri("/demo/100")
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getDemo() {
        return ">> result = " + demo.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static Mono<ClientResponse> user = client.get()
            .uri("/user/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange();

    private static String getUser() {
        User block = user.flatMap(res -> res.bodyToMono(User.class)).block();
        return ">> result = " + block;
    }

    private static Mono<ClientResponse> userAll = client.get()
            .uri("/users")
            .accept(MediaType.APPLICATION_JSON).exchange();

    private static String getUserAll() {
        List<User> block = userAll.flatMap(res -> res.bodyToFlux(User.class).collectList()).block();
        return ">> result = " + block;
    }

    public static void main(String[] args) {
        System.out.println(TestMain.getHello());
        System.out.println(TestMain.getIndex());
        System.out.println(TestMain.getDemo());
        System.out.println(TestMain.getUser());
        System.out.println(TestMain.getUserAll());
    }
}
