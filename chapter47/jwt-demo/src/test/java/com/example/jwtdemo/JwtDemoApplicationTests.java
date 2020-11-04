package com.example.jwtdemo;

import com.example.jwtdemo.security.JwtTokenUtil;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class JwtDemoApplicationTests {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void generateToken() {
        String userName = "kobe";
        String token = jwtTokenUtil.generateToken(userName);
        System.out.println(token);

        String[] splits = token.split("\\.");
        System.out.println("加密算法：" + new String(Decoders.BASE64.decode(splits[0]), StandardCharsets.UTF_8));
        System.out.println("加密主体：" + new String(Decoders.BASE64.decode(splits[1]), StandardCharsets.UTF_8));

        System.out.println(jwtTokenUtil.validateToken(token, userName));

        System.out.println(jwtTokenUtil.refreshToken(token));
    }


}
