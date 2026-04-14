package com.min.mes.controller.auth;

import com.min.mes.ApiResponse;
import com.min.mes.dto.auth.SignupDTO;
import com.min.mes.service.auth.SignupSVC;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
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
@RequestMapping("/api/auth/signup")
@RequiredArgsConstructor
public class SignupCTR extends BaseWalker {

    private final SignupSVC signupSVC;

    @PostMapping("/chk")
    public ResponseEntity<ApiResponse<Map<String, Object>>> check(@RequestBody SignupDTO dto) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap();

        try {

            boolean isExist = signupSVC.selectUser(dto);

            if (isExist) {
                resMap.put("cd", "1001");
                resMap.put("msg", "이미 존재하는 사용자입니다.");
                resMap.put("data", "");
            } else {
                resMap.put("cd", "0000");
                resMap.put("msg", "사용 가능한 아이디입니다.");
                resMap.put("data", dto.getUserId());
            }

        } catch(Exception e){
            logErr(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    //.body(new ApiResponse<>("404", e.getMessage(), null));
                    .body(ApiResponse.failMsg("DB 쿼리 실행 오류"));

        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(resMap));


    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignupDTO dto) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap();

        boolean isExist = signupSVC.selectUser(dto);

        if(isExist){
            resMap.put("cd", "1001");
            resMap.put("msg", "이미 존재하는 사용자입니다.");
            resMap.put("data", "");
        } else {
            paramMap.put("kakao_token", "");

            resMap = signupSVC.insertUser(dto, paramMap);
        }



        return ResponseEntity.ok(resMap);
    }
}
