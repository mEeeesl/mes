package com.min.mes.common.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage; // 상세 메시지 보관용

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMsg();
    }

    // 커스텀 메시지 사용: "내가 입력한 메시지"를 super와 detailMessage에 전달
    public GlobalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}
