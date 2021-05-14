package com.example.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <h1>消息对象</h1>
 * Created by hanqf on 2021/5/14 14:55.
 */


@Data
@AllArgsConstructor
//消息对象必须实现Serializable接口，否则不能被发布和接收
public class QueenMessage implements Serializable {

    private static final long serialVersionUID = 958448580883101202L;
    private String title;

    private String content;

}
