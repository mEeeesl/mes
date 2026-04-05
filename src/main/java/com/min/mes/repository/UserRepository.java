package com.min.mes.repository;

import com.min.mes.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(String userId); // JPA 메서드 > "SELECT * FROM USER_BASICINF WHERE USER_ID = ?" 쿼리

    @Modifying(clearAutomatically = true) // 실행 후 영속성 컨텍스트 초기화
    @Transactional
    @Query("UPDATE UserEntity u SET u.chkToken = :token WHERE u.id = :userId")
    int updateChkToken(@Param("userId") String userId, @Param("token") String token);
}
