package com.min.mes.service.auth;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.dto.auth.FindIdPwRequest;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.UserRepository;
import com.min.mes.service.sendSNS.brevo.BrevoSVC;
import com.min.mes.service.user.UserService;
import com.min.mes.util.*;
import com.min.mes.walker.BaseWalker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindSVC extends BaseWalker {

    private final UserRepository userRepository;
    private final UserService userService;
    private final BrevoSVC brevoSVC;
    private final VerificationCodeUtil verificationCodeUtil;
    private final RedisUtil redisUtil;

    // 인증코드 발송
    public String findAccountAuthChk(FindIdPwRequest request) {

        String authCode = "";
        String type = StringUtil.checkNull(request.getType());
        String userNm = StringUtil.checkNull(request.getUserNm());
        String userId = StringUtil.checkNull(request.getUserId());
        String email = StringUtil.checkNull(request.getEmail());

        //try {

            if(!"".equals(type)
                    && !"".equals(userNm) && !"".equals(email)){

                authCode = verificationCodeUtil.generateAlphanumericCode();
                UserEntity user = null;

                if("id".equalsIgnoreCase(type)){
                    user = userService.getUserByUserNmAndEmail(userNm, email);
                    /*
                    user = userRepository.findByUserNmAndEmail(userNm, email)
                            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));

                     */

                } else if("pw".equalsIgnoreCase(type)){
                    user = userService.getUserByUserIdAndUserNmAndEmail(userId, userNm, email);

                    /*
                    user = userRepository.findByUserIdAndUserNmAndEmail(userId, userNm, email)
                            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자가 없습니다."));
                     */

                }

                if(user == null) {
                    logErr("유저 정보가 없습니다.");
                    new GlobalException(ErrorCode.USER_NOT_FOUND);
                }


                Map dataMap = new HashMap<String, String>();
                dataMap.put("type", "authChk");
                dataMap.put("authCode", authCode);

                Boolean isSend = brevoSVC.snedEmail(dataMap, user);

                if(isSend){
                    // Redis(Upstash Cloud)에 저장 (이메일을 키로 사용, 3분간 유효)
                     redisUtil.setValue(email, authCode, 3L);
                } else {
                    authCode = "X";
                    logErr("이메일 발송에 실패했습니다.");
                    new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
                }

            } else {
                logErr("유저 정보가 없습니다.");
                new GlobalException(ErrorCode.USER_NOT_FOUND);
            }
/*
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
*/
        return authCode;
    }

    // 아이디 찾기
    public String findId(FindIdPwRequest request) {
        String userId = "";

        try {

            // 파라미터로 넘겨받은 인증코드와 Redis(Upstash Cloud) 저장된 인증코드 유효성 검증 후 아이디 내려줌
            String saveCode = StringUtil.checkNull(redisUtil.getValue(request.getEmail()));
            String inputCode =StringUtil.checkNull(request.getAuthCode());

            if(!"".equals(saveCode) && !"".equals(inputCode)
                && saveCode.equals(inputCode)) {

                UserEntity user = userService.getUserByUserNmAndEmail(request.getUserNm(), request.getEmail());

                if(user != null)
                    return user.getUserId();

            } else {
                throw new GlobalException(ErrorCode.INCORRECT_AUTH_CODE);
            }
            /*
            UserEntity user = userRepository.findByUserNmAndEmail(request.getUserNm(), request.getEmail())
                    .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND, "일치하는 회원이 없습니다."));
            */

        } catch (Exception e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return userId;
    }

    // 비밀번호 찾기
    public String findPw(FindIdPwRequest request) {
        //SecureRandomUtil secureRandomUtil = new SecureRandomUtil();
        VerificationCodeUtil verificationCodeUtil = new VerificationCodeUtil();
        String tmpPw = "";

        try {

            // 파라미터로 넘겨받은 인증코드와 Redis(Upstash Cloud) 저장된 인증코드 유효성 검증 후 아이디 내려줌
            String saveCode = StringUtil.checkNull(redisUtil.getValue(request.getEmail()));
            String inputCode =StringUtil.checkNull(request.getAuthCode());

            if(!"".equals(saveCode) && !"".equals(inputCode)
                    && saveCode.equals(inputCode)) {

                UserEntity user = userService.getUserByUserIdAndUserNmAndEmail(request.getUserId(), request.getUserNm(), request.getEmail());

                if(user != null) {

                    // 임시 비밀번호
                    //tmpPw = UUID.randomUUID().toString().substring(0, 10);

                    tmpPw = "minari" + verificationCodeUtil.generateNumericCode();
                    String encSHA256tmpPw = EncryptionUtils.encSHA256(tmpPw);

                    // DB 업데이트
                    userRepository.updateChkPass(user.getUserId(), encSHA256tmpPw);

                }

            } else {
                logErr("인증번호가 유효하지 않습니다.");
                throw new GlobalException(ErrorCode.INCORRECT_AUTH_CODE);
            }

            /*
            UserEntity user = userRepository.findByUserIdAndUserNmAndEmail(request.getUserId(), request.getUserNm(), request.getEmail())
                    .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
            //.orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "일치하는 회원정보가 없습니다."));
            */

            // 파라미터로 넘겨받은 인증코드와 Redis(Upstash Cloud) 저장된 인증코드 유효성 검증 후 임시 비밀번호 내려줌

        } catch (Exception e) {
            logErr(e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return tmpPw;
    }

    // 비밀번호 변경
    public String updatePw(FindIdPwRequest dto) {
        String tmpPw = "";
        String encSHA256tmpPw = "";

        try {
            UserEntity user = userRepository.findByUserIdAndUserNmAndEmail(dto.getUserId(), dto.getUserNm(), dto.getEmail())
                    .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
            //.orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR, "일치하는 회원정보가 없습니다."));

            Map dataMap = new HashMap<String, String>();
            // 임시 비밀번호
            tmpPw = UUID.randomUUID().toString().substring(0, 10);
            encSHA256tmpPw = EncryptionUtils.encSHA256(tmpPw);

            // DB 업데이트
            userRepository.updateChkPass(encSHA256tmpPw, user.getUserId());

        } catch (Exception e) {
            logErr(e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return tmpPw;
    }




}
