package com.min.mes.service.auth;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.dto.auth.FindIdPwRequest;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.UserRepository;
import com.min.mes.service.sendSNS.brevo.BrevoSVC;
import com.min.mes.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindSVC {

    private final UserRepository userRepository;
    private final BrevoSVC brevoSVC;

    // 아이디 찾기
    public void findId(FindIdPwRequest dto) {
        UserEntity user = userRepository.findByUserNmAndEmail(dto.getUserNm(), dto.getEmail())
                .orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));

        Map dataMap = new HashMap<String, String>();
        dataMap.put("type", "id");
        brevoSVC.snedEmail(dataMap, user);

    }

    // 비밀번호 찾기
    public void findPw(FindIdPwRequest dto) {

        try {
            UserEntity user = userRepository.findByUserIdAndUserNmAndEmail(dto.getUserId(), dto.getUserNm(), dto.getEmail())
                    .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
            //.orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "일치하는 회원정보가 없습니다."));

            Map dataMap = new HashMap<String, String>();
            // 임시 비밀번호
            String tmpPw = UUID.randomUUID().toString().substring(0, 10);
            String encSHA256tmpPw = EncryptionUtils.encSHA256(tmpPw);

            // DB 업데이트
            userRepository.updateChkPass(encSHA256tmpPw, user.getUserId());


            dataMap.put("type", "pw");
            dataMap.put("tmpPw", tmpPw);
            brevoSVC.snedEmail(dataMap, user);

        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
