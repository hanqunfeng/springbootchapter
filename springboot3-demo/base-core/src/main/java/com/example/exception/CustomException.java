package com.example.exception;

/**
 * <h1>CustomException</h1>
 * Created by hanqf on 2023/8/24 15:31.
 */



import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    //异常错误编码
    private int code ;
    //异常信息
    private String message;

    //返回数据
    private Object data;

    private CustomException(){}

    public CustomException(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }

    public CustomException(CustomExceptionType exceptionTypeEnum, String message,Object data) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
        this.data = data;
    }

    public CustomException(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
    }

    public CustomException(HttpStatus httpStatus, String message,Object data) {
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
