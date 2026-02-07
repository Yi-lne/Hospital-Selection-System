package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.RoleDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.RoleVO;
import com.chen.HospitalSelection.vo.UserVO;

import java.util.List;

/**
 * 角色服务接口
 * 提供角色权限管理功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface RoleService {

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @throws RuntimeException 当角色不存在或已分配时抛出异常
     */
    void assignRole(Long userId, Long roleId);

    /**
     * 取消用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色 ID
     */
    void removeRole(Long userId, Long roleId);

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色编码列表（如：["user", "admin"]）
     */
    List<String> getUserRoles(Long userId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return true=拥有，false=不拥有
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 检查用户是否为管理员
     *
     * @param userId 用户ID
     * @return true=是管理员，false=不是管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 获取所有用户列表（按角色筛选）
     *
     * @param roleCode 角色编码（可选）
     * @return 用户列表
     */
    List<UserVO> getUsersByRole(String roleCode);

    /**
     * 批量为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的所有权限（基于角色）
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 检查用户是否拥有指定权限
     *
     * @param userId       用户ID
     * @param permissionCode 权限编码
     * @return true=拥有，false=不拥有
     */
    boolean hasPermission(Long userId, String permissionCode);

    // ==================== 角色管理功能 ====================

    /**
     * 分页查询角色列表
     *
     * @param dto 分页查询参数
     * @return 角色分页列表
     */
    PageResult<RoleVO> getRoleList(PageQueryDTO dto);

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详细信息
     */
    RoleVO getRoleDetail(Long roleId);

    /**
     * 添加角色
     *
     * @param dto 角色信息
     * @return 添加后的角色信息
     */
    RoleVO addRole(RoleDTO dto);

    /**
     * 修改角色
     *
     * @param roleId 角色ID
     * @param dto    角色信息
     * @return 修改后的角色信息
     */
    RoleVO updateRole(Long roleId, RoleDTO dto);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<RoleVO> getAllRoles();
}
