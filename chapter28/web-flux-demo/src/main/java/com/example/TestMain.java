package com.example;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public static void main(String[] args) {
        System.out.println(TestMain.getHello());
        System.out.println(TestMain.getIndex());
    }
}
