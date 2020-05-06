package com.example.router;

import com.example.handler.DemoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.nio.charset.StandardCharsets;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:33.
 */

@Configuration
public class DemoRouter {
    @Bean
    public RouterFunction<ServerResponse> route(DemoHandler demoHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), demoHandler::hello)
                .andRoute(RequestPredicates.GET("/index").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), demoHandler::index)
                .andRoute(RequestPredicates.GET("/username/{name}").and(RequestPredicates.accept(new MediaType("application", "json", StandardCharsets.UTF_8))), demoHandler::user)
                .andRoute(RequestPredicates.GET("/ua").and(RequestPredicates.accept(new MediaType("application", "json", StandardCharsets.UTF_8))), demoHandler::userAll)
                ;
    }

}
