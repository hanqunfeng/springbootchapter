package com.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * CREATE TABLE `lock_info` (
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
 *   `expiration_time` datetime NOT NULL COMMENT '过期时间',
 *   `tag` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '锁的标示，可以理解为key',
 *   PRIMARY KEY (`id`),
 *   UNIQUE KEY `uk_tag` (`tag`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
 */
@Entity
@Table(name = "lock_info")
public class LockInfo {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 过期时间
     */
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;


    /**
     * 锁的标示，可以理解为key
     */
    @Column(name = "tag")
    private String tag;

    public LockInfo(String tag,LocalDateTime expirationTime) {
        this.tag=tag;
        this.expirationTime=expirationTime;
    }

    public LockInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

