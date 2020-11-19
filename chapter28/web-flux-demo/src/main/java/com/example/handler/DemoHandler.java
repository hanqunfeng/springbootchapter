package com.example.handler;

import com.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:31.
 */

@Component
public class DemoHandler {

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("Hello, Spring!"));

    }

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("Index, Spring!"));

    }

    public Mono<ServerResponse> user(ServerRequest request) {
        //接收请求参数
        //request.queryParam("name");

        //接收请求路径参数
        String name = request.pathVariable("name");
        User user = new User();
        user.setName(name);
        user.setId(100L);
        return ServerResponse.ok().contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(BodyInserters.fromValue(user));

    }

    public Mono<ServerResponse> userObject(ServerRequest request) {
        //接收请求body数据
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono.flatMap(user -> ServerResponse
                .status(HttpStatus.CREATED)
                .body(Mono.just(user),User.class)
        );
    }

    public Mono<ServerResponse> userAll(ServerRequest request) {
        User user = new User();
        user.setName("张三");
        user.setId(100L);
        User user2 = new User();
        user2.setName("李四");
        user2.setId(200L);
        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user2);
        return ServerResponse.ok().contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body(BodyInserters.fromValue(list));

    }
}
