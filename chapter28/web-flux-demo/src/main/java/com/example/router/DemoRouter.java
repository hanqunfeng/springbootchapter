package com.example.router;

import com.example.handler.GreetingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:33.
 */

@Configuration
public class DemoRouter {
    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), greetingHandler::hello)
                .andRoute(RequestPredicates.GET("/index").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), greetingHandler::index);
    }

}
