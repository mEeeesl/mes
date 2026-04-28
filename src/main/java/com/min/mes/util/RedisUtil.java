package com.min.mes.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    // 인증번호 저장 (key: 이메일, value: 인증번호, duration: 만료시간/분)
    public void setValue(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, duration, TimeUnit.MINUTES);
    }

    // 인증번호 가져오기
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 인증 완료 후 데이터 삭제
    public void delValue(String key) {
        redisTemplate.delete(key);
    }
}
