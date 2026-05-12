package com.min.mes.entity;

import com.min.mes.dto.auth.SignupDTO;
import com.min.mes.util.EncryptionUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "USER_BASICINF")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA
////@AllArgsConstructor
////@Builder
public class UserEntity { //Repository 조회 편의

    @Id
    @Column(name="user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="chk_pass", nullable = false, length = 64)
    private String chkPass; // SHA-256 HASH

    @Column(name="chk_token")
    private String chkToken;

    @Column(name="user_nm", nullable = false, unique = true)
    private String userNm;

    @Column(name="birth")
    private String birth;

    @Column(name="email", unique = true)
    private String email;

    @Column(name="phone_num")
    private String phoneNum;

    @Column(name="user_sex")
    private String userSex;

    @Column(name="chk_ci")
    private String chkCI;

    @Column(name="chk_di")
    private String chkDI;

    @Column(name="USE_YN", length = 1)
    private String useYn;

    @Column(name="DEL_YN", length = 1)
    private String delYn;

    @Column(name="reg_dt")
    private String regDt;

    @Column(name="kakao_token_id")
    private String kakaoTokenId;

    //@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가 (SERIAL) 처리 // DB가 시리얼 처리를 해주니까 패스
    @Column(name="emp_no", unique = true, insertable = false, updatable = false)
    private int empNo;

    /**
     * 빌더 생성자
     * empNo는 DB가 생성하므로 빌더에서 제외
     */
    @Builder
    private UserEntity(String userId, String chkPass, String userNm, String phoneNum,
                       String birth, String email, String useYn, String delYn, String regDt,
                       String ju1, String ju2, String kakaoTokenId) {
        this.userId = userId;
        this.chkPass = chkPass;
        this.userNm = userNm;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.email = email;
        this.useYn = useYn;
        this.delYn = delYn;
        this.regDt = regDt;
        //this.ju1 = ju1;
        //this.ju2 = ju2;
        this.kakaoTokenId = kakaoTokenId;
    }

    /**
     * 일반 회원가입 정적 팩토리 메서드
     */
    public static UserEntity createGeneralUser(String userId, String userNm, String encSHA256Password,
                                               String phoneNum, String birth, String email, String kakaoTokenId
            /*,String ju1, String ju2*/) throws Exception {
        return UserEntity.builder()
                .userId(userId)
                .userNm(userNm)
                .chkPass(encSHA256Password)
                .phoneNum(phoneNum)
                .birth(birth)
                .email(email)
                //.ju1(ju1)
                //.ju2(ju2)
                .useYn("Y")
                .delYn("N")
                .regDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .kakaoTokenId(kakaoTokenId)
                .build();
    }

    /**
     * 카카오 회원가입 정적 팩토리 메서드
     */
    public static UserEntity createKakaoUser(String userId, String userNm, String kakaoTokenId,
                                             String phoneNum, String birth, String email) {
        return UserEntity.builder()
                .userId(userId)
                .userNm(userNm)
                .kakaoTokenId(kakaoTokenId)
                .chkPass("KAKAO_AUTH_USER") // 카카오는 별도 비밀번호 없음
                .phoneNum(phoneNum)
                .birth(birth)
                .email(email)
                .useYn("Y")
                .delYn("N")
                .regDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .build();
    }

    /**
     * 프로필 업데이트
     */
    public void updateUserProfile(String email, String phoneNum, String encSHA256Password/*, String ju1, String ju2*/) {
        if (email != null) this.email = email;
        if (phoneNum != null) this.phoneNum = phoneNum;
        if (encSHA256Password != null) this.chkPass = encSHA256Password;
        //if (ju1 != null) this.ju1 = ju1;
        //if (ju2 != null) this.ju2 = ju2;
    }
}
