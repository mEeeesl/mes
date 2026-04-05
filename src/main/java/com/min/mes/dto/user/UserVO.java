package com.min.mes.dto.user;

import com.min.mes.entity.UserEntity;
import lombok.Getter;

@Getter
public class UserVO { //세션, Controller용
    private final String userId;
    private final String chkPass = "";
    private final String chkToken = "";

    private final String userNm;
    private final String birth;

    private final String email = "";
    private final String phoneNum;
    private final String userSex;
    private final String ci = "";
    private final String di = "";

    public UserVO(UserEntity entity){
        this.userId = entity.getUserId();

        this.userNm = entity.getUserNm();
        this.birth = entity.getBirth();

        this.phoneNum = entity.getPhoneNum();
        this.userSex = entity.getUserSex();
    }

}
