package com.example.common.response;

import lombok.Data;

import java.util.Date;

/**
 * <h1>JwtResponse</h1>
 * Created by hanqf on 2023/8/21 12:43.
 */


@Data
public class JwtResponse {
    private String token;
    private Date expiration;
}
