package com.example.oauth2clientdemo2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResponse handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        return AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,fieldError.getDefaultMessage()));
    }


    @ExceptionHandler(BindException.class)
    public AjaxResponse handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        return AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,fieldError.getDefaultMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public AjaxResponse customerException(CustomException e, HttpServletRequest request, HttpServletResponse response) {
        if(e.getCode() == CustomExceptionType.SYSTEM_ERROR.getCode()){
                 //400异常不需要持久化，将异常信息以友好的方式告知用户就可以
                //TODO 将500异常信息持久化处理，方便运维人员处理
        }

        if(e.getCode() == HttpStatus.NOT_ACCEPTABLE.value()){
            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            try {
                redirectStrategy.sendRedirect(request,response,"/logout");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return AjaxResponse.error(e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResponse accessDeniedException(AccessDeniedException e) {
        //TODO 将异常信息持久化处理，方便运维人员处理

        //没有被程序员发现，并转换为CustomException的异常，都是其他异常或者未知异常.
        return AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,e.getMessage(),e.getClass().getName()));
    }

    @ExceptionHandler(Exception.class)
    public AjaxResponse exception(Exception e) {
        //TODO 将异常信息持久化处理，方便运维人员处理
        e.printStackTrace();

        //没有被程序员发现，并转换为CustomException的异常，都是其他异常或者未知异常.
        return AjaxResponse.error(new CustomException(CustomExceptionType.OTHER_ERROR,"未知异常"));
    }


}
