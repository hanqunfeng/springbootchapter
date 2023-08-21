package com.example.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * <h1>CustomException</h1>
 * Created by hanqf on 2023/8/21 12:11.
 */

@Getter
public class CustomException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7517737797718858641L;
    //异常错误编码
    private int code;
    //异常信息
    private String message;

    //返回数据
    private Object data;

    private CustomException() {
    }

    public CustomException(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }

    public CustomException(CustomExceptionType exceptionTypeEnum, String message, Object data) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
        this.data = data;
    }

    public CustomException(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
    }

    public CustomException(HttpStatus httpStatus, String message, Object data) {
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
