package com.min.mes.service;

import com.min.mes.ApiResponse;
import com.min.mes.auth.JWTAuth;
import com.min.mes.dto.user.UserVO;
import com.min.mes.entity.UserEntity;
import com.min.mes.mapper.user.UserMapper;
import com.min.mes.repository.UserRepository;
import com.min.mes.util.EncryptionUtils;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
    @RequiredArgsConstructor
    public class LoginSVC extends BaseWalker {

        private final UserRepository userRepository;
        private final UserMapper userMapper;
        /*
        public LoginSVC(UserRepository repo) {
        this.userRepository = repo;
        */

        public Map loginProc(String userId, String chkPass) throws Exception {
            boolean isSuccess = false;

            // DB조회
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
                    //.orElse(null);

            if(userEntity != null){

                // PW CHK - SHA256
                if(!userEntity.getChkPass().equals(EncryptionUtils.encSHA256(chkPass))){
                    //throw new RuntimeException("비밀번호가 일치하지 않습니다.");

                    return Map.of(
                                "cd", "4001",
                                "msg", "비밀번호가 일치하지 않습니다.",
                                "user", "",
                                "token", ""
                    );

                }

                //UserVO userVO = new UserVO(userEntity);
                //String token = JWTAuth.generateToken(userId);
                //String JWTtoken = JWTAuth.generateToken(userEntity.getUserId(), userEntity.getUserNm());

                return Map.of(
                            "cd", "0000",
                            "msg", "로그인 성공",
                            "user", userEntity/*,
                            "token", JWTtoken*/
                );

                //isSuccess = true;
            } else {
                //throw new RuntimeException("아이디와 비밀번호를 확인해주세요.");

                return Map.of(
                            "cd", "4002",
                            "msg", "아이디와 비밀번호를 확인해주세요.",
                            "user", ""
                );


                //return new ApiResponse<>(402, "아이디와 비밀번호를 확인해주세요.", null);
                //return isSuccess;

            }

            //return new ApiResponse<>(200, "성공", null);
            //return isSuccess;
        }

        public UserEntity getUser(String userId) throws Exception {
            Map resMap = new HashMap();

            try{


                String path = getClass().getClassLoader().getResource("").getPath();
                logInfo("현재 Classpath 루트 경로: " + path);


            } catch (Exception e) {
                logErr(e.getMessage());
            }

            return userRepository.findById(userId)
                    .orElse(null);
        }




    }





