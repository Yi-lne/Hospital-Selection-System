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
}
