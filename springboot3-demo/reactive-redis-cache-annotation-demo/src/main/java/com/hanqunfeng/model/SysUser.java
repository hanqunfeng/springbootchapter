package com.hanqunfeng.model;

import lombok.Data;

/**
 * <h1>Demo model</h1>
 * Created by hanqf on 2024/7/18 10:06.
 */
@Data
public class SysUser implements java.io.Serializable{
    private static final long serialVersionUID = 487099956628832327L;
    private String id;
    private String username;
    private String password;
    private Boolean enable;
}
