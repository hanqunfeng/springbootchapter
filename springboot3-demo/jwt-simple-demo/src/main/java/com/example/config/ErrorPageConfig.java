package com.example.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * <h1>ErrorPageConfig</h1>
 * Created by hanqf on 2023/8/21 15:19.
 */

@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage[] errorPages = new ErrorPage[1];
        errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
        registry.addErrorPages(errorPages);
    }
}
