package com.example.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Mybatis Generator on 2024-09-26 17:36:13
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
    /**
     */
    private Long id;

    /**
     */
    private String username;

    /**
     */
    private String addr;

    /**
     */
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}
