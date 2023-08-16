package com.example.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单流水表
 * test_order
 */
@Data
public class TestOrder implements Serializable {
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
