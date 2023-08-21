package com.example.common.support;

import com.example.common.exception.CustomException;
import com.example.common.exception.CustomExceptionType;
import com.example.common.response.AjaxResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h1>WebExceptionHandler</h1>
 * Created by hanqf on 2023/8/21 12:15.
 */


@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public AjaxResponse customerException(CustomException e) {

        return AjaxResponse.error(e);
    }


    @ExceptionHandler(Exception.class)
    public AjaxResponse exception(Exception e) {

        //没有被程序员发现，并转换为CustomException的异常，都是其他异常或者未知异常.
        return AjaxResponse.error(new CustomException(CustomExceptionType.OTHER_ERROR, e.getMessage()));
    }
}
