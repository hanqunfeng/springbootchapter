package com.example.model.one;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @TableName address
 */
@TableName(value ="address")
@Data
public class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 7076345586345444108L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @TableField(value = "province")
    private String province;

    /**
     *
     */
    @TableField(value = "city")
    private String city;

    /**
     *
     */
    @TableField(value = "userId")
    private Long userid;


}
