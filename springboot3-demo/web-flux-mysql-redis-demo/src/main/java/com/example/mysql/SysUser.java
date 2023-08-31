package com.example.mysql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * <h1>SysUser</h1>
 * Created by hanqf on 2023/8/30 17:15.
 */


@Data
@Table("sys_user")
public class SysUser implements Serializable {
    @Serial
    private static final long serialVersionUID = -2301710205992732167L;
    @Id
    private String id;

    private String username;

    private String password;

    private Boolean enable;

}
