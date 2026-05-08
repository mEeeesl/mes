package com.min.mes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "USER_DETAILINF")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserDtlEntity {

    @Id
    @Column(name="user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="ju1")
    private String ju1;
    @Column(name="ju2")
    private String ju2;

    @Column(name="user_sex")
    private String userSex;

    @Column(name="bank_nm")
    private String bankNm;

    @Column(name="accnt_num")
    private String accntNum;

    public UserDtlEntity(String userId, String ju1, String ju2, String userSex, String bankNm, String accntNum){
        userId = this.userId;
        ju1 = this.ju1;
        ju2 = this.ju2;
        userSex = this.userSex;
        bankNm = this.bankNm;
        accntNum = this.accntNum;

    }
}
