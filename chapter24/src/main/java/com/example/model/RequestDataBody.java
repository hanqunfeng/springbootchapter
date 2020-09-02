package com.example.model;

import java.io.Serializable;

/**
 * <p>请求体</p>
 * Created by hanqf on 2020/9/2 16:07.
 */


public class RequestDataBody implements Serializable {

    private static final long serialVersionUID = -5742618214359372795L;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RequestDataBody{" +
                "data='" + data + '\'' +
                '}';
    }
}
