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
}
