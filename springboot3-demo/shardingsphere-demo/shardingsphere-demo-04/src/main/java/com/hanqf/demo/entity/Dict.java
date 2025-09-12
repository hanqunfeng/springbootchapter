package com.hanqf.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 广播表，字典表
 * Created by hanqf on 2025/9/1 10:33.
 */

@Setter
@Getter
@ToString
@TableName("dict")
public class Dict {
    @TableId
    private Long dictid;
    private String dictkey;
    private String dictval;
}
