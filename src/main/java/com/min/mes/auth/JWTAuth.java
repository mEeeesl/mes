package com.min.mes.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // 빈 등록하여 @Value 작동하도록
public class JWTAuth {
    //private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static Key secretKey;

    @Value("${jwt.secret}") // application.yaml 설정값 로드(난 하드코딩..)
    private String secretKeyStr;

    private static final long EXPIRATION = 1000 * 60 * 30; // 30분

    // 서버 기동 시 String 키를 Ket 객체로 변환
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
    }


    public static String generateToken(String userId, String userNm) {
        return Jwts.builder()
                .setSubject(userId) // PK값
                .claim("userNm", userNm) // 보조정보
                .setIssuedAt(new Date()) // 생성시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 만료시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // Key 객체와 알고리즘
                .compact();
    }

    public static String validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // username 반환
    }
}