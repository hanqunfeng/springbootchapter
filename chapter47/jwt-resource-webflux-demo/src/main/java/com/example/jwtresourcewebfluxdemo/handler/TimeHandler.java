package com.example.jwtresourcewebfluxdemo.handler;

/**
 * <p></p>
 * Created by hanqf on 2020/5/7 23:16.
 */


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class TimeHandler {
    public Mono<ServerResponse> getTime(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Now is " + LocalTime.now()), String.class);
    }

    public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Today is " + LocalDate.now()), String.class);
    }

    //curl http://localhost:8080/times
    //data:21:32:22
    //data:21:32:23
    //data:21:32:24
    //data:21:32:25
    //data:21:32:26
    //<Ctrl+C>
    public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return ok()
                .contentType(MediaType.TEXT_EVENT_STREAM) // MediaType.TEXT_EVENT_STREAM表示Content-Type为text/event-stream，即SSE-->服务端推送（Server Send Event）
                .body(
                        Flux.interval(Duration.ofSeconds(1))//利用interval生成每秒一个数据的流
                                .map(l -> LocalTime.now().format(dateTimeFormatter)),
                        String.class
                );
    }
}
