package com.example.model;

import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/11 18:18.
 */

@Data
public class ChatResponse {
    private String message;
    private Integer errorCode;
    private Object data;
    private Integer code;
}
