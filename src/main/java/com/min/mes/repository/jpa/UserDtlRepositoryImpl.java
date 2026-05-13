package com.min.mes.repository.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import static com.min.mes.entity.QUserDtlEntity.userDtlEntity; // Q클래스 import

@RequiredArgsConstructor
public class UserDtlRepositoryImpl implements UserDtlRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean isJuNotEmpty(String userId) {
        // userId가 일치하고 ju1이 null이 아닌 데이터가 있는지 체크
        Integer fetchOne = queryFactory
                .selectOne()
                .from(userDtlEntity)
                .where(
                        userDtlEntity.userId.eq(userId),
                        userDtlEntity.ju1.isNotNull()
                )
                .fetchFirst(); // findFirst()와 유사, 데이터가 없으면 null 반환

        return fetchOne != null;
    }
}