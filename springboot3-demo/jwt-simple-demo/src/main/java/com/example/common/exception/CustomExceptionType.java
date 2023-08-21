package com.example.common.exception;

import lombok.Getter;

/**
 * <h1>CustomExceptionType</h1>
 * Created by hanqf on 2023/8/21 12:10.
 */


@Getter
public enum CustomExceptionType {
    USER_INPUT_ERROR(888, "用户输入异常"),
    SYSTEM_ERROR(500, "系统服务异常"),
    OTHER_ERROR(999, "其他未知异常");

    CustomExceptionType(int code, String typeDesc) {
        this.code = code;
        this.typeDesc = typeDesc;
    }

    private String typeDesc;//异常类型中文描述

    private int code; //code

}
