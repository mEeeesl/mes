package com.min.mes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_BASICINF")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity { //Repository 조회 편의

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) -- 자동증가
    @Column(name="user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="chk_pass", nullable = false, length = 64)
    private String chkPass; // SHA-256 HASH
    @Column(name="chk_token")
    private String chkToken;

    @Column(name="user_nm", nullable = false)
    private String userNm;
    @Column(name="birth")
    private String birth;
    @Column(name="email")
    private String email;
    @Column(name="phone_num")
    private String phoneNum;
    @Column(name="user_sex")
    private String userSex;
    @Column(name="chk_ci")
    private String chkCI;
    @Column(name="chk_di")
    private String chkDI;
    @Column(name="USE_YN")
    private String useYn;
    @Column(name="DEL_YN")
    private String delYn;
    @Column(name="reg_dt")
    private String regDt;
    @Column(name="ju1")
    private String ju1;
    @Column(name="ju2")
    private String ju2;
    @Column(name="kakao_token_id", unique = true)
    private String kakaoTokenId;

}
