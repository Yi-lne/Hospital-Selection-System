package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.RoleDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.RoleMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.mapper.UserRoleMapper;
import com.chen.HospitalSelection.model.Role;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.model.UserRole;
import com.chen.HospitalSelection.service.RoleService;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.RoleVO;
import com.chen.HospitalSelection.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void assignRole(Long userId, Long roleId) {
        log.info("为用户分配角色，用户ID：{}，角色ID：{}", userId, roleId);

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否已经分配过该角色
        int count = userRoleMapper.countByUserAndRole(userId, roleId);
        if (count > 0) {
            throw new BusinessException("用户已拥有该角色");
        }

        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(LocalDateTime.now());

        userRoleMapper.insert(userRole);

        log.info("角色分配成功，用户ID：{}，角色ID：{}", userId, roleId);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        log.info("取消用户角色，用户ID：{}，角色ID：{}", userId, roleId);

        userRoleMapper.delete(userId, roleId);

        log.info("角色取消成功，用户ID：{}，角色ID：{}", userId, roleId);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        log.info("获取用户角色列表，用户ID：{}", userId);

        // 查询用户的角色关联
        List<UserRole> userRoleList = userRoleMapper.selectByUserId(userId);

        // 根据roleId查询角色信息
        List<String> roleCodes = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            Role role = roleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                roleCodes.add(role.getRoleCode());
            }
        }

        return roleCodes;
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        log.info("检查用户是否拥有角色，用户ID：{}，角色编码：{}", userId, roleCode);

        List<String> userRoles = getUserRoles(userId);
        return userRoles.contains(roleCode);
    }

    @Override
    public boolean isAdmin(Long userId) {
        return hasRole(userId, "admin");
    }

    @Override
    public List<UserVO> getUsersByRole(String roleCode) {
        log.info("根据角色查询用户列表，角色编码：{}", roleCode);

        Role role = roleMapper.selectByCode(roleCode);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 查询拥有该角色的用户角色关联
        List<UserRole> userRoleList = userRoleMapper.selectByRoleId(role.getId());

        // 根据userId查询用户信息
        List<UserVO> userVOList = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            User user = userMapper.selectById(userRole.getUserId());
            if (user != null) {
                UserVO vo = new UserVO();
                vo.setId(user.getId());
                vo.setPhone(user.getPhone());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
                vo.setGender(user.getGender());
                vo.setStatus(user.getStatus());
                userVOList.add(vo);
            }
        }

        return userVOList;
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        log.info("批量为用户分配角色，用户ID：{}，角色列表：{}", userId, roleIds);

        // 先删除用户的所有角色
        userRoleMapper.deleteByUserId(userId);

        // 批量添加新角色
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }

        log.info("角色批量分配成功，用户ID：{}", userId);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        log.info("获取用户权限列表，用户ID：{}", userId);

        // 根据用户的角色获取权限
        // 这里简化实现，实际应该有RolePermission表
        List<String> permissions = new ArrayList<>();

        if (isAdmin(userId)) {
            // 管理员拥有所有权限
            permissions.add("*:*:*");
        } else {
            // 普通用户的基本权限
            permissions.add("user:info:view");
            permissions.add("user:info:update");
            permissions.add("hospital:view");
            permissions.add("doctor:view");
            permissions.add("community:post");
            permissions.add("community:comment");
        }

        return permissions;
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        log.info("检查用户是否拥有权限，用户ID：{}，权限编码：{}", userId, permissionCode);

        List<String> permissions = getUserPermissions(userId);

        // 检查是否有通配符权限
        if (permissions.contains("*:*:*")) {
            return true;
        }

        return permissions.contains(permissionCode);
    }

    // ==================== 角色管理功能实现 ====================

    @Override
    public PageResult<RoleVO> getRoleList(PageQueryDTO dto) {
        log.info("分页查询角色列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        int offset = (dto.getPage() - 1) * dto.getPageSize();
        List<Role> roles = roleMapper.selectAll();

        // 转换为VO
        List<RoleVO> roleVOs = roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 手动分页
        int total = roleVOs.size();
        int start = offset;
        int end = Math.min(offset + dto.getPageSize(), total);
        List<RoleVO> pageData = start < total ? roleVOs.subList(start, end) : new ArrayList<>();

        // 手动构建PageResult
        PageResult<RoleVO> pageResult = new PageResult<>();
        pageResult.setList(pageData);
        pageResult.setTotal((long) total);
        pageResult.setPageNum(dto.getPage());
        pageResult.setPageSize(dto.getPageSize());
        pageResult.setPages((total + dto.getPageSize() - 1) / dto.getPageSize());

        return pageResult;
    }

    @Override
    public RoleVO getRoleDetail(Long roleId) {
        log.info("获取角色详情，角色ID：{}", roleId);

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        return convertToVO(role);
    }

    @Override
    @Transactional
    public RoleVO addRole(RoleDTO dto) {
        log.info("添加角色，角色名称：{}，角色编码：{}", dto.getRoleName(), dto.getRoleCode());

        // 检查角色编码是否已存在
        Role existingRole = roleMapper.selectByCode(dto.getRoleCode());
        if (existingRole != null) {
            throw new BusinessException("角色编码已存在");
        }

        // 创建角色
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setIsDeleted(0);
        role.setCreateTime(LocalDateTime.now());

        roleMapper.insert(role);

        log.info("角色添加成功，角色ID：{}", role.getId());
        return convertToVO(role);
    }

    @Override
    @Transactional
    public RoleVO updateRole(Long roleId, RoleDTO dto) {
        log.info("修改角色，角色ID：{}", roleId);

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否与其他角色冲突
        Role existingRole = roleMapper.selectByCode(dto.getRoleCode());
        if (existingRole != null && !existingRole.getId().equals(roleId)) {
            throw new BusinessException("角色编码已被其他角色使用");
        }

        // 更新角色信息
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        roleMapper.updateById(role);

        log.info("角色修改成功，角色ID：{}", roleId);
        return convertToVO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        log.info("删除角色，角色ID：{}", roleId);

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否有用户正在使用该角色
        List<UserRole> userRoles = userRoleMapper.selectByRoleId(roleId);
        if (!userRoles.isEmpty()) {
            throw new BusinessException("该角色正在被使用，无法删除");
        }

        // 逻辑删除
        roleMapper.deleteById(roleId);

        log.info("角色删除成功，角色ID：{}", roleId);
    }

    @Override
    public List<RoleVO> getAllRoles() {
        log.info("获取所有角色列表");

        List<Role> roles = roleMapper.selectAll();
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 将Role实体转换为RoleVO
     *
     * @param role 角色实体
     * @return 角色VO
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        return vo;
    }
}
