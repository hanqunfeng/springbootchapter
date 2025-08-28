package com.hanqf.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;

/**
 * 课程表
 * Created by hanqf on 2025/8/27 17:14.
 */
@TableName("course")
public class Course implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -7970564112098055294L;
    @TableId
    private Long cid;
    private String cname;
    private Long userId;
    private String cstatus;

    public Long getCid() {
        return this.cid;
    }

    public String getCname() {
        return this.cname;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getCstatus() {
        return this.cstatus;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCstatus(String cstatus) {
        this.cstatus = cstatus;
    }
}
