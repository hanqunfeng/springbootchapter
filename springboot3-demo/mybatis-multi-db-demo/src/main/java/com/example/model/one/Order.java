package com.example.model.one;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Mybatis Generator on 2023-08-16 18:10:36
 */
@Data
public class Order implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单id
     */
    private String orderId;

    private static final long serialVersionUID = 1L;
}
