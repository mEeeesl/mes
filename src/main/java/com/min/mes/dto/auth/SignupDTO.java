package com.min.mes.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupDTO {
    private String userNm;
    private String userId;
    private String userPw;
    private String telNo;
    private String birthDate;
    private String email;
    private String kakaoCode;
    private String ju1;
    private String ju2;
}
