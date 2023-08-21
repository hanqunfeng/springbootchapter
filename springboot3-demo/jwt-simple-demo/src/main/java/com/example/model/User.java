package com.example.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>User</h1>
 * Created by hanqf on 2023/8/21 12:46.
 */

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 5827774594016200297L;
    private String username;
    private String password;

}
