package com.min.mes;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class ApiResponse<T> {

    private static final String COMMON_SUCCESS_CODE = "0000";
    private static final String COMMON_FAIL_CODE = "400";


    private final String cd; // 커스텀 비즈니스 상태 코드
    private final String msg; // 비즈니스 응답 메시지
    private final T data; // 리턴 데이터

    // 성공 시 간편 생성 메서드
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(COMMON_SUCCESS_CODE, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 실패 시 간편 생성 메서드 - 데이터
    public static <T> ApiResponse<T> fail(T data){
        return new ApiResponse<>(COMMON_FAIL_CODE, "응답 실패", data);
    }

    // 실패 시 간편 생성 메서드 - msg, 데이터
    public static <T> ApiResponse<T> failMsg(String msg){
        return new ApiResponse<>(COMMON_FAIL_CODE, msg, null);
    }

    // 실패 시 간편 생성 메서드 - cd, msg, 데이터
    public static <T> ApiResponse<T> failCdMsg(String cd, String msg, T data){
        return new ApiResponse<>(cd, msg, null);
    }

}
