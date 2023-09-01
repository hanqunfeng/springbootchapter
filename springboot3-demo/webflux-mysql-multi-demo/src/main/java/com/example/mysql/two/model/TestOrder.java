package com.example.mysql.two.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>SysUser</h1>
 * Created by hanqf on 2023/8/30 17:15.
 */


@Data
@Table("test_order")
public class TestOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = -2301710205992732167L;

    @Id
    private Long id;

    @Column("order_id")
    private String orderId;


}
