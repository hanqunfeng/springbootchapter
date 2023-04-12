package com.example.model;

import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/11 18:09.
 */

@Data
public class ChatBodyRequest {
    private Object data;

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
