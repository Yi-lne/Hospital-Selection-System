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
     * 根据ID查询用户角色关联
     * @param id 主键ID
     * @return 用户角色关联对象
     */
    UserRole selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<UserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询所有用户
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    List<UserRole> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有用户角色关联
     * @return 用户角色关联列表
     */
    List<UserRole> selectAll();

    /**
     * 插入用户角色关联
     * @param userRole 用户角色关联对象
     * @return 影响行数
     */
    int insert(UserRole userRole);

    /**
     * 批量插入用户角色关联
     * @param userRoles 用户角色关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("userRoles") List<UserRole> userRoles);

    /**
     * 删除用户的指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int delete(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据ID删除
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 存在数量
     */
    int countByUserAndRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
