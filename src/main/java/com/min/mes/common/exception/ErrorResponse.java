package com.min.mes.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String cd;  // "AUTH_001"
    private final String msg; // "일치하는 회원 정보가 없습니다."

    public ErrorResponse(String cd, String msg) {
        this.cd = cd;
        this.msg = msg;
    }
}
