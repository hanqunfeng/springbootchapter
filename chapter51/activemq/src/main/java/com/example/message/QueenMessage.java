package com.example.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <h1>消息对象</h1>
 * Created by hanqf on 2021/5/14 14:55.
 *
 * JMS规范中的消息类型包括TextMessage、MapMessage、ObjectMessage、BytesMessage、和StreamMessage等五种。
 * ActiveMQ也有对应的实现，基于springboot，可以将任意类型作为消息体，springboot在调用时会进行相应的类型转换。
 * 本例使用的实际上就是ObjectMessage，这里注意要实现Serializable接口
 */


@Data
@AllArgsConstructor
//消息对象必须实现Serializable接口，否则不能被发布和接收
public class QueenMessage implements Serializable {

    private static final long serialVersionUID = 958448580883101202L;
    private String title;

    private String content;

}
