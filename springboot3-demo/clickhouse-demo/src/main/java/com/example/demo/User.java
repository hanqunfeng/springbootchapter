package com.example.demo;

/**
 * <h1></h1>
 * Created by hanqf on 2024/9/23 14:29.
 */

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 用户
 */
@Data
@Entity
@Accessors(chain = true)
@Table(name = "user_info")
public class User {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @Column(name = "id")
    // 不能设置主键生成策略
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "addr")
    private String addr;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Transient
    private String createTimeStr;

    public String getCreateTimeStr() {
        return Objects.requireNonNullElseGet(createTime, LocalDateTime::now).format(FORMATTER);
    }
}
