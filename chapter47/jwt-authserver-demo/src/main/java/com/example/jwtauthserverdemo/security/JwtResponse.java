package com.example.jwtauthserverdemo.security;

import lombok.Data;

import java.util.Date;

/**
 * <h1>JwtResponse</h1>
 * Created by hanqf on 2020/11/4 14:20.
 */

@Data
public class JwtResponse {
    private String token;
    private Date expiration;
}
