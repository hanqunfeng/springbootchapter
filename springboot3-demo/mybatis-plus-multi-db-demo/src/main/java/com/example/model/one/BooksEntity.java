package com.example.model.one;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author hanqf
 * @since 2023-08-17 12:39:30
 */
@Data
@TableName("books")
public class BooksEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7132935702549205420L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("bookName")
    private String bookName;

    @TableField("price")
    private Double price;

    @TableField("totalPage")
    private Integer totalPage;

    @TableField("userId")
    private Long userId;
}
