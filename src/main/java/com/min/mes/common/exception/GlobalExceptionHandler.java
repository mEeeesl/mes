package com.min.mes.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(GlobalException e) {
        log.error("CustomException: {}", e.getErrorCode().getMsg());
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                //.body(new ErrorResponse(errorCode));
                .body(new ErrorResponse(errorCode.getCd(), e.getMessage()));
    }
}
