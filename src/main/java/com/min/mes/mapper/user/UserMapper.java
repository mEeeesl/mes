package com.min.mes.mapper.user;

import com.min.mes.dto.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserMapper {
    Map<String, Object> selectUser(@Param("userId") String userId);
    UserVO findByUserId(String userId);

}
