package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色Mapper接口
 * 对应表：sys_role
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色对象
     */
    Role selectById(@Param("id") Long id);
}
