package com.min.mes.service.user;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // JPA 주입

    public UserEntity getUser(String userId){
        return userRepository.findById(userId).orElse(null); // SELECT * FROM USER_BASICINF WHERE USER_ID = ? 쿼리 자동 생성

        //return userRepository.findById(userId).orElswThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        /*
        // ( Map 치환버전... )
        Map<String, Object> userMap = toMap(userRepository.findById(userId));
         */
    }

    public UserEntity getUserBySocialId(String provider, String code) {
        UserEntity userEntity = null;
        if("kakao".equals(provider)){
             userEntity = userRepository.findByKakaoTokenId(code).orElse(null);
        } else if("GOOGLE".equals(provider)){

        } else if("NAVER".equals(provider)){

        }

        //return userRepository.findByKakaoTokenId(code).orElse(null);
        return userEntity;
    }

    public UserEntity getUserByUserNmAndEmail(String userNm, String email) {
        return userRepository.findByUserNmAndEmail(userNm, email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));

    }

    public UserEntity getUserByUserIdAndUserNmAndEmail(String userId, String userNm, String email) {
        return userRepository.findByUserIdAndUserNmAndEmail(userId, userNm, email)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));

    }

    public void updateRefreshToken(String userId, String newToken) {
        UserEntity user = getUser(userId);
        userRepository.updateChkToken(userId, newToken);

    }




}
