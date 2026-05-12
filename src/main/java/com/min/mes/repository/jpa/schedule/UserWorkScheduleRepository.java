package com.min.mes.repository.jpa.schedule;

import com.min.mes.entity.schedule.UserWorkScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserWorkScheduleRepository extends JpaRepository<UserWorkScheduleEntity, Long> {

    // 사용자가 특정 기간 내에 신청한 내역 조회
    List<UserWorkScheduleEntity> findByUserIdAndApntDtBetween(String userId, LocalDateTime start, LocalDateTime end);

    // 중복 체크를 위한 메서드
    boolean existsByUserIdAndApntDt(String userId, LocalDateTime apntDt);
}