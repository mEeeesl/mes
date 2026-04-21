package com.min.mes.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Auth
    USER_NOT_FOUND(404, "AUTH_001", "일치하는 회원 정보가 없습니다."),
    INVALID_PASSWORD(400, "AUTH_002", "비밀번호가 일치하지 않습니다."),

    // Mail
    MAIL_SEND_ERROR(500, "MAIL_001", "메일 발송 중 오류가 발생했습니다."),

    // Common
    INTERNAL_SERVER_ERROR(500, "COMMON_001", "서버 내부 오류가 발생했습니다.");

    private final int status; // HTTP 상태 코드
    private final String cd;
    private final String msg;
}
