package com.min.mes.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component // 빈 등록하여 @Value 작동하도록
public class JwtUtil_back {
    //private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static Key secretKey;

    @Value("${jwt.secret}") // application.yaml 설정값 로드(난 하드코딩..)
    private String secretKeyStr;

    private static final long EXPIRATION = 1000 * 60 * 30; // 30분
    //private static final long EXPIRATION = 1000 * 60 * 60; // 1시간

    // 서버 기동 시 String 키를 Ket 객체로 변환
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
    }

    /*
    @Value("${jwt.secret}")
    public void setSecretKey(String key) {
        secretKey = key;
    }
    */




    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 누구 토큰인지
                .setIssuedAt(new Date()) // 생성시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                //.signWith(secretKey)
                .signWith(secretKey, SignatureAlgorithm.HS256) // Key 객체와 알고리즘
                .compact();
    }

    public static String validateToken(String token) throws JwtException {
        return "";
        /*
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // username 반환

         */
    }
}