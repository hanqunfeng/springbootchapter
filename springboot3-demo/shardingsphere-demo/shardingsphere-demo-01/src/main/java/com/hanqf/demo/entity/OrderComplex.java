package com.hanqf.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 *
 * Created by hanqf on 2025/8/28 17:42.
 */

@Setter
@Getter
@ToString
@TableName("t_order_complex")
public class OrderComplex implements java.io.Serializable{
    @Serial
    private static final long serialVersionUID = 957527102918236239L;
    @TableId
    private Long orderId;
    private Long userId;
    private String status;
}
