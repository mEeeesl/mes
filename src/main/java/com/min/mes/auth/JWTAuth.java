package com.min.mes.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
// import java.security.Key; jjwt 0.11.5 버전
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // 빈 등록하여 @Value 작동하도록
public class JWTAuth {
    //private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //private static Key secretKey; jjwt 0.11.5 버전
    private static SecretKey secretKey;

    @Value("${jwt.secret}") // application.yaml 설정값 로드(난 하드코딩..)
    private String secretKeyStr;

    private static final long EXPIRATION = 1000 * 60 * 30; // 30분

    // 서버 기동 시 String 키를 Ket 객체로 변환
    @PostConstruct
    public void init()
    {
        //this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        // 2. static 변수는 클래스명으로 접근하거나 직접 대입 (this 제외)
        secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
    }


    public static String generateToken(String userId, String userNm) {
        /* jjwt 0.12.5 버전 */
        return Jwts.builder()
                .subject(userId)           // setSubject -> subject
                .claim("userNm", userNm)
                .issuedAt(new Date())      // setIssuedAt -> issuedAt
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION)) // setExpiration -> expiration
                .signWith(secretKey)       // 알고리즘 생략 가능 (SecretKey 타입일 때)
                .compact();
        /* jjwt 0.11.5 버전
        return Jwts.builder()
                .setSubject(userId) // PK값
                .claim("userNm", userNm) // 보조정보
                .setIssuedAt(new Date()) // 생성시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 만료시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // Key 객체와 알고리즘
                .compact();
         */
    }

    public static String validateToken(String token) throws JwtException {
        /* jjwt 0.12.5 버전 */
        return Jwts.parser() // 빌더 생성 메서드
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token) // '서명된 클레임'을 파싱
                .getPayload() // JWT 표준 용어인 Payload(페이로드)
                .getSubject(); // username 반환
        /* jjwt 0.11.5 버전
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // username 반환
        */
    }
}