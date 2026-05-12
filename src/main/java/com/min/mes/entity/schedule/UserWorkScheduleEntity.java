package com.min.mes.entity.schedule;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_work_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자 (접근제한)
public class UserWorkScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "apnt_dt", nullable = false)
    private LocalDateTime apntDt;

    @CreationTimestamp // reg_dt는 직접 넣지 않고 JPA가 자동으로 인서트 시점에 넣음
    @Column(name = "reg_dt", updatable = false)
    private LocalDateTime regDt;

    @Column(name = "group_cd", length = 20)
    private String groupCd;

    @Column(name = "brand_cd", length = 20)
    private String brandCd;

    @Column(name = "chk_yn", length = 1)
    private String chkYn;

    @Column(name = "work_cnfm_yn", length = 1)
    private String workCnfmYn;

    @Column(name = "work_yn", length = 1)
    private String workYn;

    @Column(name = "sttl_area")
    private String sttlArea;

    @Column(name = "sttl_lctn")
    private String sttlLctn;

    // [빌더 패턴 적용 생성자]
    @Builder
    public UserWorkScheduleEntity(String userId, LocalDateTime apntDt, String groupCd, String brandCd,
                                  String sttlArea, String sttlLctn, String chkYn, String workCnfmYn, String workYn) {
        this.userId = userId;
        this.apntDt = apntDt;
        this.groupCd = groupCd;
        this.brandCd = brandCd;
        this.sttlArea = sttlArea;
        this.sttlLctn = sttlLctn;
        this.chkYn = chkYn != null ? chkYn : "N";
        this.workCnfmYn = workCnfmYn != null ? workCnfmYn : "N";
        this.workYn = workYn != null ? workYn : "N";
    }

    // --- 비즈니스 메서드 (수정 및 취소) ---

    /**
     * 신청 내역 수정 (셔틀 정보 변경 등)
     */
    public void updateSchedule(String sttlArea, String sttlLctn) {
        // 확정된 상태(workCnfmYn == 'Y')에서는 수정 불가하게 막는 로직을 서비스에서 추가 필요
        this.sttlArea = sttlArea;
        this.sttlLctn = sttlLctn;
    }

    /**
     * 출근 확인 상태 변경 (나중에 일정조회 시 사용)
     */
    public void markAsChecked() {
        this.chkYn = "Y";
    }

    /**
     * 취소 로직: 데이터를 실제 DELETE 할 수도 있고, 상태값(cancel_yn 등)만 바꿀 수도 있습니다.
     * 대기업 시스템은 이력을 위해 주로 상태값을 바꾸거나 로그 테이블로 옮깁니다.
     */
}