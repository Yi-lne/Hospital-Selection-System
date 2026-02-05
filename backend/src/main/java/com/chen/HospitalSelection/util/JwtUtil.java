package com.chen.HospitalSelection.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT token
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret: hospitalSelectionSecretKey2024ForIntelligentHospitalSystem}")
    private String secret;

    @Value("${jwt.expiration: 86400000}")
    private Long expiration;

    /**
     * 生成JWT token
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @return JWT token
     */
    public String generateToken(Long userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        return createToken(claims, phone);
    }

    /**
     * 创建token
     *
     * @param claims 声明信息
     * @param subject 主题（手机号）
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 解析token
     *
     * @param token JWT token
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从token获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token获取手机号
     *
     * @param token JWT token
     * @return 手机号
     */
    public String getPhoneFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证token是否有效
     *
     * @param token JWT token
     * @return true-有效，false-无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 刷新token
     *
     * @param token 旧token
     * @return 新token
     */
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String phone = claims.getSubject();
        return generateToken(userId, phone);
    }
}
