package com.min.mes.controller.schedule;

import com.min.mes.ApiResponse;
import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.jpa.UserDtlRepository;
import com.min.mes.repository.jpa.UserRepository;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ApplyCTR extends BaseWalker {

    private final UserDtlRepository userDtlRepository;

    @PostMapping("check-status")
    public ResponseEntity<ApiResponse> existsByPersonalId (@RequestBody Map<String, Object> paramMap) {
        Boolean isExist = false;

        try {
            String userId = StringUtil.checkNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            isExist = userDtlRepository.existsByJu1(userId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        Map.of(
                                "existYn", isExist
                        )
                ));
    }

}
