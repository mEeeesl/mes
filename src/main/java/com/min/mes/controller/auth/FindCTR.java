package com.min.mes.controller.auth;

import com.min.mes.ApiResponse;
import com.min.mes.dto.auth.FindIdPwRequest;
import com.min.mes.service.auth.FindSVC;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController // front로 JsonData 떨굼
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class FindCTR {

    private final FindSVC findSVC;

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
