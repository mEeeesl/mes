package com.min.mes;

import com.min.mes.interceptor.ApiLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{

    private final AppProperties appProperties;

    /* 원래 있었는데.. SecurityConfig에서 하도록 처리 [S]
    @Override
    public void addCorsMappings(CorsRegistry registry){

        System.out.println("___________________________________________--------------------------------____________________________");
        System.out.println(appProperties.getAllowDomainIp().toArray(new String[0]));
        System.out.println(appProperties.isReal());
        System.out.println(appProperties.getAllowDomainIp());
        System.out.println(appProperties.getAllowDomainIp().toArray());


        registry.addMapping("/api/**")
                .allowedOrigins(appProperties.getAllowDomainIp().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                //.allowedHeaders("Authorization", "Cache-Control", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
    원래 있었는데.. SecurityConfig에서 하도록 처리 [E] */



    /*
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(appProperties.getAllowDomainIp().toArray(new String[0]))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new ApiLoggingInterceptor())
                .addPathPatterns("/**");
    }
}