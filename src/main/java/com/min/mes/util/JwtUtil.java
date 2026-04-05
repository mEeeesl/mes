package com.min.mes.util;

import com.min.mes.AppProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // 빈 등록하여 @Value 작동하도록
public class JwtUtil {
    //private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static AppProperties appProperties;
    @Value("${app.is-real}")
    private boolean isReal;


    private static Key secretKey;

    @Value("${jwt.secret}") // application.yaml 설정값 로드(난 하드코딩..)
    private String secretKeyStr;

    private static final long ACCESS_TOKEN_EXP = 1000 * 60 * 30; // 30분 (초 단위)
    private static final long REFRESH_TOKEN_EXP = 604800; // 7일 (초 단위)

    // ( 서버 기동 시 String 키를 Ket 객체로 변환 )
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes((StandardCharsets.UTF_8)));
    }

    // ( 토큰 생성 (Access / Refresh 공용 )
    public static String generateToken(String userId, String userNm) {
        return Jwts.builder()
                .setSubject(userId) // PK값
                .claim("userNm", userNm) // 보조정보
                .setIssuedAt(new Date()) // 생성시간
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP)) // 만료시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // Key 객체와 알고리즘
                .compact();
    }

    // ( 응답용 쿠키 생성 (Access / Refresh 공용) )
    public ResponseCookie createCookie(String name, String value) {
        return ResponseCookie.from(name, value) // 쿠키 키, 밸류
                .httpOnly(true) // JS 접근 차단(XSS 방지)
                //.secure(appProperties.isReal()) // HTTPS만 전송(로컬테스트시 false)
                .secure(isReal) // HTTPS만 전송(로컬테스트시 false)
                .path("/") // 모든 경로에서 쿠키 유효
                .maxAge(REFRESH_TOKEN_EXP)
                .sameSite("Lax") // CSRF 방지
                .build();
    }

    // ( 토큰에서 UserId 추출 )
    public static String validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // username 반환
    }

    // 쿠키삭제
    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(true)
                .secure(isReal)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    // 만료된 토큰에서 UserId 뽑는 유틸
    public String getUserIdFromExpiredToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 만료되었어도 에러 객체 안에 담긴 body에서 정보를 꺼낼 수 있음
            return e.getClaims().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}