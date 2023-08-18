package com.example.model;

import java.io.Serializable;
import lombok.Data;

/**
 * Created by Mybatis Generator on 2023-08-18 11:15:41
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