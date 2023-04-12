package com.example.model;

import lombok.Data;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/11 18:51.
 */

@Data
public class ChatStreamResponse {
    private String object;
    private Object choices;
    private String id;
    private String model;
    private Integer created;
}
