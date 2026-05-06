package com.min.mes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.min.mes.repository.redis")
public class RedisConfig {

    // RedisTemplate 등의 빈(Bean) 설정

}
