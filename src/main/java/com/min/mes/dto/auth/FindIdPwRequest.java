package com.min.mes.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindIdPwRequest {
    private String userId;
    private String userNm;
    private String email;

}
