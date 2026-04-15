package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户角色关联Mapper接口
 * 对应表：sys_user_role
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 根据用户ID查询所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<UserRole> selectByUserId(@Param("userId") Long userId);
}
