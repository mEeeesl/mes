package com.min.mes.service.user;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.entity.UserDtlEntity;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.jpa.UserDtlRepository;
import com.min.mes.repository.jpa.UserRepository;
import com.min.mes.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // JPA 주입
    private final UserDtlRepository userDtlRepository;

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
                //.orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));
                .orElse(null);

    }

    public UserEntity getUserByUserIdAndUserNmAndEmail(String userId, String userNm, String email) {
        return userRepository.findByUserIdAndUserNmAndEmail(userId, userNm, email)
                //.orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));
                .orElse(null);

    }

    public void updateRefreshToken(String userId, String newToken) {
        UserEntity user = getUser(userId);
        userRepository.updateChkToken(userId, newToken);

    }


    @Transactional
    public void updateUserDtl(Map paramMap) throws Exception {

        try {
            /*
            paramMap :: {
            brand=Zara, dates=[2026-05-14],
            shuttleRegion=서울, shuttleStop=2호선 잠실역 8번출구 앞 잠실시그마타워 앞,
            ju1=900101, ju2=1111111, user_sex=M,
             bank_nm=신한은행, accnt_num=111-11111-1111111}
             */

            if(!paramMap.isEmpty()) {


                UserDtlEntity userDtlEntity = userDtlRepository.findById(StringUtil.checkNull(paramMap.get("userId"))).orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

                if(userDtlEntity != null) {
                    userDtlEntity.updateUserProfile(
                            StringUtil.checkNull(paramMap.get("ju1")),
                            StringUtil.checkNull(paramMap.get("ju2")),
                            StringUtil.checkNull(paramMap.get("user_sex")),
                            StringUtil.checkNull(paramMap.get("bank_nm")),
                            StringUtil.checkNull(paramMap.get("accnt_num"))
                    );

                    userDtlRepository.save(userDtlEntity);
                }
            }



        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }


    }


}
