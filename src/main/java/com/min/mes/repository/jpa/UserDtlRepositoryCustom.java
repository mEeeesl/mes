package com.min.mes.repository.jpa;

/**
 * Querydsl을 사용하기 위한 사용자 정의 인터페이스
 */
public interface UserDtlRepositoryCustom {
    boolean isJuNotEmpty(String userId);
}