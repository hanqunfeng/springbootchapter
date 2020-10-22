package com.example.exception;

/**
 * <h1>系统全局异常</h1>
 * Created by hanqf on 2020/10/22 15:13.
 */


public class CustomException extends RuntimeException {
    /**
     * 异常信息
     */
    private String message;

    /**
     * 异常错误编码
     */
    private int code;

    /**
     * 不允许创建无参对象
     */
    private CustomException() {
    }

    public CustomException(CustomExceptionType customExceptionType) {
        this.code = customExceptionType.getCode();
        this.message = customExceptionType.getDesc();
    }

    public CustomException(CustomExceptionType customExceptionType,String message) {
        this.code = customExceptionType.getCode();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
