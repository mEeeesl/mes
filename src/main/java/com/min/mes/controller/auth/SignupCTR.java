package com.min.mes.controller.auth;

import com.min.mes.ApiResponse;
import com.min.mes.dto.auth.SignupDTO;
import com.min.mes.service.auth.SignupSVC;
import com.min.mes.util.StringUtil;
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
    //public ResponseEntity<ApiResponse<Map<String, Object>>> check(@RequestBody SignupDTO dto) throws Exception {
    public ResponseEntity<ApiResponse<Map<String, Object>>> check(@RequestBody Map paramMap) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        //Map<String, Object> paramMap = new HashMap();

        String chkId = StringUtil.checkNull(paramMap.get("userId"));

        try {
            boolean isExist = false;

            if(!"".equals(chkId)){
                isExist = signupSVC.selectUser(chkId);
            }

            if (isExist) {
                resMap.put("cd", commonFailCd);
                resMap.put("msg", "이미 사용중인 아이디입니다.");
                resMap.put("data", "");
            } else {
                resMap.put("cd", commmonSuccessCd);
                resMap.put("msg", "사용 가능한 아이디입니다.");
                //resMap.put("data", dto.getUserId());
                resMap.put("data", "");
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
    public ResponseEntity<ApiResponse> register(@RequestBody SignupDTO dto) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Object> paramMap = new HashMap();

        //boolean isExist = signupSVC.selectUser(dto);
        boolean isExist = signupSVC.selectUser(dto.getUserId());

        if(isExist){
            resMap.put("cd", "1001");
            resMap.put("msg", "이미 존재하는 사용자입니다.");
            resMap.put("data", "");
        } else {
            paramMap.put("kakao_token", dto.getKakaoCode());

            String cd = signupSVC.insertUser(dto, paramMap);

            if("2001".equals(cd)){

                resMap.put("cd", commonFailCd);
                resMap.put("msg", "카카오 인증 코드가 유효하지 않습니다.");

            } else if("2002".equals(cd)){

                resMap.put("cd", commonFailCd);
                resMap.put("msg", "카카오 사용자 정보를 불러올 수 없습니다.");

            } else if("2003".equals(cd)){

                resMap.put("cd", commonFailCd);
                resMap.put("msg", "가입된 계정이 존재합니다.");

            } else if("0000".equals(cd)){

                resMap.put("cd", commmonSuccessCd);
                resMap.put("msg", "회원가입이 완료되었습니다.");

            }

            resMap.put("data", "");

        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(resMap));

    }
}
