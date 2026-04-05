package com.min.mes.security;

import com.min.mes.AppProperties;
import com.min.mes.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API 테스트를 위해 잠시 끔 , REST API라면 CSRF를 disable 하는 경우가 많음
                //.cors(cors -> {})
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 여기서 직접 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청허용
                        .requestMatchers("/", "/api/login", "/api/reissue").permitAll() // 루트 페이지, 로그인, 토큰재발급 API 허용 (누구나 접근가능)
                        .anyRequest().authenticated() // 나머지는 로그인 필수
                )
                .formLogin(form -> form.disable())  // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // 기본 인증(얼럿창) 비활성화

                // 토큰 인증 실패 시 401을 던지도록 설정
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private final AppProperties appProperties;
    public SecurityConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // AppProperties에서 가져온 도메인 리스트 설정
        configuration.setAllowedOrigins(appProperties.getAllowDomainIp());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 일단 전체 허용 후 필요한 것만 추리기 - "Authorization", "Cache-Control", "Content-Type", "Origin", "Accept"
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

        /*

         registry.addMapping("/api/**")
                .allowedOrigins(appProperties.getAllowDomainIp().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                //.allowedHeaders("Authorization", "Cache-Control", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);

         */
    }
}
