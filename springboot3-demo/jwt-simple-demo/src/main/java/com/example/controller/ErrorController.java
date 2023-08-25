package com.example.controller;

import com.example.exception.CustomException;
import com.example.response.AjaxResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>ErrorController</h1>
 * Created by hanqf on 2023/8/21 15:24.
 */

@RestController
public class ErrorController {

    @RequestMapping("/404")
    public AjaxResponse notFound() {
        return AjaxResponse.error(new CustomException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()));
    }
}
