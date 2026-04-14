package com.min.mes.service.auth;

import com.min.mes.ApiResponse;
import com.min.mes.controller.kakao.KakaoSVC;
import com.min.mes.dto.auth.SignupDTO;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.UserRepository;
import com.min.mes.util.EncryptionUtils;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignupSVC extends BaseWalker {

    private final UserRepository userRepository;
    private final KakaoSVC kakaoSVC; // 주입

    public boolean selectUser(SignupDTO dto) throws Exception {
        boolean isExist = false;

        if(userRepository.findById(dto.getUserId()).isPresent()){
            isExist = true;
        }

        return isExist;

    }

    @Transactional
    public Map<String, Object> insertUser(SignupDTO dto, Map paramMap) throws Exception {

        Map resMap = new HashMap();

        // --- 카카오 유효성 검증 ---
        String kakaoAccessToken = StringUtil.checkNull(kakaoSVC.getKakaoAccessToken(dto.getKakaoCode()));
        logInfo((kakaoAccessToken));
        if ("".equals(kakaoAccessToken)) {
            return Map.of("cd", "2001", "msg", "카카오 인증 코드가 유효하지 않습니다.");
        }

        Map<String, Object> kakaoInfo = kakaoSVC.getKakaoUserInfo(kakaoAccessToken);
        logInfo(kakaoInfo);
        if (kakaoInfo == null) {
            return Map.of("cd", "2002", "msg", "카카오 사용자 정보를 불러올 수 없습니다.");
        }

        // 이름 일치 여부 검증 (선택 사항)
        //Map<String, Object> properties = (Map<String, Object>) kakaoInfo.get("properties");
        //String kakaoNickname = (String) properties.get("nickname");

        String kakaoId = StringUtil.checkNull(kakaoInfo.get("id")); // 카카오 사용자 고유 ID

        // Entity 변환 및 비밀번호 암호화
        UserEntity newUser = UserEntity.builder()
                .userNm(dto.getUserNm())
                .userId(dto.getUserId())
                .chkPass(EncryptionUtils.encSHA256(dto.getUserPw()))
                .phoneNum(dto.getTelNo())
                .birth(dto.getBirthDate())
                .email(dto.getEmail())
                .useYn("Y")
                .delYn("N")
                .ju1(dto.getJu1())
                .ju2(dto.getJu2())
                .kakaoTokenId(kakaoId)
                .build();

        // DB 저장(JPA)
        /////userRepository.save(newUser);

        resMap.put("cd", "0000");
        resMap.put("msg", "회원가입이 완료되었습니다.");
        resMap.put("data", "");


        return resMap;
    }




}
