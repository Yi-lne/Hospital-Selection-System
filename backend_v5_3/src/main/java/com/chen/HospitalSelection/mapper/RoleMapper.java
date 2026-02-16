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

    /**
     * 根据角色编码查询角色
     * @param roleCode 角色编码
     * @return 角色对象
     */
    Role selectByCode(@Param("roleCode") String roleCode);

    /**
     * 根据角色名称查询角色
     * @param roleName 角色名称
     * @return 角色对象
     */
    Role selectByName(@Param("roleName") String roleName);

    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<Role> selectAll();

    /**
     * 插入角色
     * @param role 角色对象
     * @return 影响行数
     */
    int insert(Role role);

    /**
     * 更新角色信息
     * @param role 角色对象
     * @return 影响行数
     */
    int updateById(Role role);

    /**
     * 逻辑删除角色
     * @param id 角色ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除角色
     * @param ids 角色ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 检查角色编码是否存在
     * @param roleCode 角色编码
     * @return 存在数量
     */
    int countByCode(@Param("roleCode") String roleCode);
}
