package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Jwt Token
 */
@Component
public class JwtToken {
    

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 生成token令牌
     *
     * @param userName 用户名称
     * @return 令token牌
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", userName);
        claims.put("created", new Date());

        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从令牌中获取到期时间
     *
     * @param token 令牌
     * @return 用户名
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token    令牌
     * @param userName 用户名称
     * @return 是否有效
     */
    public Boolean validateToken(String token, String userName) {

        String username = getUsernameFromToken(token);
        return (username.equals(userName) && !isTokenExpired(token));
    }

    /**
     * 从claims生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration());
        String type = jwtProperties.getType();
        return switch (type) {
            case "secret" -> Jwts.builder().setClaims(claims)
                    .setExpiration(expirationDate)
                    //对称加密
                    .signWith(jwtProperties.getSecretKey())
                    .compact();
            case "rsa" -> Jwts.builder().setClaims(claims)
                    .setExpiration(expirationDate)
                    //使用RSA私钥加密
                    .signWith(jwtProperties.rsaPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
            case "jks" -> Jwts.builder().setClaims(claims)
                    .setExpiration(expirationDate)
                    //使用JKS私钥加密
                    .signWith(jwtProperties.jksPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();
            default -> null;
        };
    }


    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            String type = jwtProperties.getType();
            claims = switch (type) {
                case "secret" -> Jwts.parserBuilder()
                        .setSigningKey(jwtProperties.getSecretKey())
                        .build().parseClaimsJws(token).getBody();
                case "rsa" -> Jwts.parserBuilder()
                        //使用RSA公钥解密
                        .setSigningKey(jwtProperties.rsaPublicKey())
                        .build().parseClaimsJws(token).getBody();
                case "jks" -> Jwts.parserBuilder()
                        //使用JKS公钥解密
                        .setSigningKey(jwtProperties.jksPublicKey())
                        .build().parseClaimsJws(token).getBody();
                default -> null;
            };
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

}
