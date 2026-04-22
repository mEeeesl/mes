package com.min.mes.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeUtil {
    // 숫자 6자리 생성 (예: 429184)
    public String generateNumericCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    // 영문 대문자 + 숫자 혼합 6자리 (예: 7K2P9W)
    public String generateAlphanumericCode() {
        return RandomStringUtils.random(6, true, true).toUpperCase();
    }
}
