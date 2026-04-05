package com.min.mes.filter;

import com.min.mes.auth.JWTAuth;
import com.min.mes.util.JwtUtil;
import com.min.mes.util.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String ACCESS_TOKEN_NAME = "accessToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /*
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String username = JwtUtil.validateToken(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response); // 컨트롤러 실행
        */




        /* 1. 쿠키에서 토큰 찾기 (HttpOnly 방식 대응) */
        /* Stream API 방식 ... */
        String token = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> ACCESS_TOKEN_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        /* for - if 방식 .. ACCESS_TOKEN_NAME*/
        /*
        String token = null;
         if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) { // 로그인 시 정한 쿠키 이름
                    token = cookie.getValue();
                    break;
                }
            }
        }
         */


        // 1. 쿠키에서 토큰 찾기 (HttpOnly 방식 대응) -- for - if 방식...



        // 💡 2. 쿠키에 없으면 헤더에서 찾기 (보안이슈)
        /*
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        */



        // 💡 3. 토큰이 있다면 검증 및 컨텍스트 등록
        if (token != null) {
            try {
                //String username = StringUtil.checkNull(JWTAuth.validateToken(token));
                String username = StringUtil.checkNull(JwtUtil.validateToken(token));
                if(!"".equals(username)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                logger.error(e.getMessage() + " Check server secret key settings ( -.yaml ) [ Incoming token value ] : " + token);

                // 토큰 만료 등 에러 시 처리 (무시하고 filterChain으로 넘겨서 Anonymous로 둘 수도 있음)
                //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                logger.warn("토큰 만료됨");
                SecurityContextHolder.clearContext();

                //return;
            }
        }

        filterChain.doFilter(request, response);
    }
}