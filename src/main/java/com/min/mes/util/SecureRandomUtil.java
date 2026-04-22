package com.min.mes.util;

import java.security.SecureRandom;

public class SecureRandomUtil {

    private static final SecureRandom sr = new SecureRandom();

    /**
     * 숫자로만 구성된 6자리 코드
     * @return String
     */
    public String generateCode() {
        int code = sr.nextInt(900000) + 100000; // 100,000 ~ 999,999 범위
        return String.valueOf(code);
    }
}
