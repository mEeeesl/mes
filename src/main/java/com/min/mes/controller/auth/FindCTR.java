package com.min.mes.controller.auth;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.min.mes.ApiResponse;
import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.dto.auth.FindIdPwRequest;
import com.min.mes.service.auth.FindSVC;
import com.min.mes.util.StringUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController // front로 JsonData 떨굼
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class FindCTR {

    private final FindSVC findSVC;

    /**
     * MFA 인증코드 이메일 발송
     * Param
     * - 아이디 찾기: USER_NM, EMAIL
     * - 비밀번호 찾기: USER_ID, USER_NM, EMAIL
     */
    @PostMapping("/find-auth-chk")
    public ResponseEntity<ApiResponse<String>> findAuthChk(@RequestBody FindIdPwRequest request) {
        String authCode = "";
        try{
            authCode = findSVC.findAccountAuthChk(request);
            if("".equals(authCode)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failMsg("일치하는 사용자가 없습니다."));

            }

        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK)
                //.body(ApiResponse.success(authCode));
                .body(ApiResponse.success("이메일로 인증코드를 보냈습니다."));
    }

    /**
     * 아이디 찾기 API
     * POST /api/auth/find-id
     */
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<String>> findId(@RequestBody FindIdPwRequest request) {
        findSVC.findId(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("입력하신 이메일로 아이디 정보를 발송했습니다."));
    }

    /**
     * 비밀번호 찾기(임시 비번 발급) API
     * POST /api/auth/find-pw
     */
    @PostMapping("/find-pw")
    public ResponseEntity<ApiResponse<String>> findPw(@RequestBody FindIdPwRequest request) {
        findSVC.findPw(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("입력하신 이메일로 임시 비밀번호 정보를 발송했습니다."));
    }
}
