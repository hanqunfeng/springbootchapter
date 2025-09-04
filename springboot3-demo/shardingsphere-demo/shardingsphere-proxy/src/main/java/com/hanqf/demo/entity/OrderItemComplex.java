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
@TableName("t_order_item_complex")
public class OrderItemComplex implements java.io.Serializable{

    @Serial
    private static final long serialVersionUID = -7341517409885363582L;
    @TableId
    private Long itemId;
    private Long orderId;
    private Long userId;
    private String status;
}
