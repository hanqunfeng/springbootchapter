package com.example.redis;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>UserEntity</h1>
 * Created by hanqf on 2023/8/30 16:59.
 */


@Data
public class UserEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -7229906944062898852L;
    private Long id;
    private String name;
}
