package com.min.mes.repository.jpa;

import com.min.mes.entity.UserDtlEntity;
import com.min.mes.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserDtlRepository extends JpaRepository<UserDtlEntity, String>,UserDtlRepositoryCustom {

    //boolean existsByJu1(String userId);

    //JPQL
    //@Query("SELECT CASE WHEN u.ju1 IS NOT NULL THEN true ELSE false END FROM UserDtlEntity u WHERE u.userId = :userId")
    //boolean isJuNotEmpty(@Param("userId") String userId);

    Optional<UserDtlEntity> findById(String userId);


}
