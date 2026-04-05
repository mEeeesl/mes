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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="USER_ID", unique = true, nullable = false)
    private String userId;

    @Column(nullable = false, length = 64)
    private String chkPass; // SHA-256 HASH
    @Column(name="CHK_TOKEN")
    private String chkToken;

    @Column(name="USER_NM", nullable = false)
    private String userNm;
    private String birth;

    private String email;
    @Column(name="PHOME_NUM")
    private String phoneNum;

    private String userSex;

    private String chkCI;
    private String chkDI;

    @Column(name="USE_YN")
    private String useYn;
    @Column(name="DEL_YN")
    private String delYn;

}
