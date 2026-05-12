package com.min.mes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "USER_DETAILINF")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Builder
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

    /**
     * 빌더 생성자
     * empNo는 DB가 생성하므로 빌더에서 제외
     */
    @Builder
    public UserDtlEntity(String userId, String ju1, String ju2, String userSex, String bankNm, String accntNum){
        this.userId = userId;
        this.ju1 = ju1;
        this.ju2 = ju2;
        this.userSex = userSex;
        this.bankNm = bankNm;
        this.accntNum = accntNum;
    }

    /**
     * 일반 회원가입 정적 팩토리 메서드
     */
    public static UserDtlEntity createGeneralUser(String userId/*, String ju1, String ju2, String userSex, String bankNm, String accntNum*/) throws Exception {
        return UserDtlEntity.builder()
                .userId(userId)
                /*
                .ju1(ju1)
                .ju2(ju2)
                .userSex(userSex)
                .bankNm(bankNm)
                .accntNum(accntNum)
                */
                .build();
    }

    /**
     * 카카오 회원가입 정적 팩토리 메서드
     */
    public static UserDtlEntity createKakaoUser(String userId) {
        return UserDtlEntity.builder()
                .userId(userId)
                //.ju1(ju1)
                //.ju2(ju2)
                //.userSex(userSex)
                //.bankNm(bankNm)
                //.accntNum(accntNum)
                .build();
    }

    /**
     * 프로필 업데이트
     */
    public void updateUserProfile(String ju1, String ju2, String userSex, String bankNm, String accntNum) {
        if (ju1 != null) this.ju1 = ju1;
        if (ju2 != null) this.ju2 = ju2;
        if( userSex != null) this.userSex = userSex;
        if (bankNm != null) this.bankNm = bankNm;
        if (accntNum != null) this.accntNum = accntNum;
    }
}
