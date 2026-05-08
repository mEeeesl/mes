package com.min.mes.repository.jpa;

import com.min.mes.entity.UserDtlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDtlRepository extends JpaRepository<UserDtlEntity, String> {
    boolean existsByJu1(String userId);
}
