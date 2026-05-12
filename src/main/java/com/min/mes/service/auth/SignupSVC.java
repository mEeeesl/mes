package com.min.mes.service.auth;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.controller.kakao.KakaoSVC;
import com.min.mes.dto.auth.SignupDTO;
import com.min.mes.entity.UserDtlEntity;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.jpa.UserDtlRepository;
import com.min.mes.repository.jpa.UserRepository;
import com.min.mes.util.EncryptionUtils;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignupSVC extends BaseWalker {

    private final UserRepository userRepository;
    private final UserDtlRepository userDtlRepository;
    private final KakaoSVC kakaoSVC; // 주입

    //public boolean selectUser(SignupDTO dto) throws Exception {
    public boolean selectUser(String userId) throws Exception {
        boolean isExist = false;


        //if(userRepository.findById(dto.getUserId()).isPresent()){
        if(userRepository.findById(userId).isPresent()){
            isExist = true;
        }

        return isExist;

    }

    @Transactional
    public String insertUser(SignupDTO dto, Map paramMap) throws Exception {
        try {

            // --- 카카오 유효성 검증 ---
            String kakaoAccessToken = StringUtil.checkNull(kakaoSVC.getKakaoAccessToken(dto.getKakaoCode()));
            logInfo((kakaoAccessToken));
            if ("".equals(kakaoAccessToken)) {
                return "2001";
            }

            Map<String, Object> kakaoInfo = kakaoSVC.getKakaoUserInfo(kakaoAccessToken);
            logInfo(kakaoInfo);
            if (kakaoInfo == null) {
                return "2002";
            }

            String kakaoId = StringUtil.checkNull(kakaoInfo.get("id")); // 카카오 사용자 고유 ID

            if(userRepository.findByKakaoTokenId(kakaoId).isPresent()){
                return "2003";
            }

            UserEntity newUser = UserEntity.createGeneralUser(
                    dto.getUserId(),
                    dto.getUserNm(),
                    EncryptionUtils.encSHA256(dto.getUserPw()),
                    dto.getTelNo(),
                    dto.getBirthDate(),
                    dto.getEmail(),
                    kakaoId
            );

            UserDtlEntity newUserDtl = UserDtlEntity.createGeneralUser(
                    dto.getUserId()
            );

            // DB 저장(JPA)
            userRepository.save(newUser);
            userDtlRepository.save(newUserDtl);

        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return "0000";
    }

}
