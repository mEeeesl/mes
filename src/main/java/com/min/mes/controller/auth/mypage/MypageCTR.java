package com.min.mes.controller.auth.mypage;

import com.min.mes.ApiResponse;
import com.min.mes.entity.UserEntity;
import com.min.mes.service.user.UserService;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController // front로 JsonData 떨굼
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MypageCTR extends BaseWalker {
    private final UserService userService;

    @PostMapping("/mypage")
    public ResponseEntity<ApiResponse> mypage() throws Exception {
        Map userMap = new HashMap();

        UserEntity user = null;

        try {
            String userId = StringUtil.checkNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            if(!"".equals(userId) && !"anonymousUser".equals(userId)){
                user = userService.getUser(userId);
                userMap.put("userId", user.getUserId());
                userMap.put("userNm", user.getUserNm());
                userMap.put("email", user.getEmail());
                userMap.put("phoneNum",user.getPhoneNum());

                // ( 바디에는 토큰 제외 정보만 )
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.success(
                                Map.of(
                                        "user", userMap
                                )
                        ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            logErr("유효하지 않은 토큰입니다. 재로그인 해주세요.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failMsg("토큰이 유효하지 않습니다"));

    }

}
