package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.RoleDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.RoleMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.mapper.UserRoleMapper;
import com.chen.HospitalSelection.model.Role;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.model.UserRole;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.RoleVO;
import com.chen.HospitalSelection.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 角色服务测试类
 * 测试角色权限管理功能
 *
 * @author chen
 */
@DisplayName("角色服务测试")
public class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.RoleServiceImpl roleService;

    private User testUser;
    private Role testRole;
    private UserRole testUserRole;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ROLE_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试用户
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setPhone("13800000001");
        testUser.setNickname("测试用户");
        testUser.setAvatar("http://example.com/avatar.png");
        testUser.setGender(1);
        testUser.setStatus(1);
        testUser.setIsDeleted(0);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());

        // 创建测试角色
        testRole = new Role();
        testRole.setId(TEST_ROLE_ID);
        testRole.setRoleName("普通用户");
        testRole.setRoleCode("user");
        testRole.setIsDeleted(0);
        testRole.setCreateTime(LocalDateTime.now());

        // 创建测试用户角色关联
        testUserRole = new UserRole();
        testUserRole.setId(1L);
        testUserRole.setUserId(TEST_USER_ID);
        testUserRole.setRoleId(TEST_ROLE_ID);
        testUserRole.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试分配角色 - 成功")
    public void testAssignRole_Success() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);
        when(userRoleMapper.countByUserAndRole(TEST_USER_ID, TEST_ROLE_ID)).thenReturn(0);
        when(userRoleMapper.insert(any(UserRole.class))).thenAnswer(invocation -> {
            UserRole ur = invocation.getArgument(0);
            ur.setId(100L);
            return null;
        });

        // Act
        assertDoesNotThrow(() -> roleService.assignRole(TEST_USER_ID, TEST_ROLE_ID));

        // Assert
        verify(userMapper, times(1)).selectById(TEST_USER_ID);
        verify(roleMapper, times(1)).selectById(TEST_ROLE_ID);
        verify(userRoleMapper, times(1)).countByUserAndRole(TEST_USER_ID, TEST_ROLE_ID);
        verify(userRoleMapper, times(1)).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("测试分配角色 - 用户不存在")
    public void testAssignRole_UserNotExists() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.assignRole(TEST_USER_ID, TEST_ROLE_ID);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("测试分配角色 - 角色不存在")
    public void testAssignRole_RoleNotExists() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.assignRole(TEST_USER_ID, TEST_ROLE_ID);
        });

        assertEquals("角色不存在", exception.getMessage());
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("测试分配角色 - 已拥有该角色")
    public void testAssignRole_AlreadyHasRole() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);
        when(userRoleMapper.countByUserAndRole(TEST_USER_ID, TEST_ROLE_ID)).thenReturn(1);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.assignRole(TEST_USER_ID, TEST_ROLE_ID);
        });

        assertEquals("用户已拥有该角色", exception.getMessage());
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("测试取消角色 - 成功")
    public void testRemoveRole_Success() {
        // Arrange
        when(userRoleMapper.delete(TEST_USER_ID, TEST_ROLE_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> roleService.removeRole(TEST_USER_ID, TEST_ROLE_ID));

        // Assert
        verify(userRoleMapper, times(1)).delete(TEST_USER_ID, TEST_ROLE_ID);
    }

    @Test
    @DisplayName("测试获取用户角色列表 - 成功")
    public void testGetUserRoles_Success() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        List<String> result = roleService.getUserRoles(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user", result.get(0));

        verify(userRoleMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取用户角色列表 - 无角色")
    public void testGetUserRoles_NoRoles() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<String> result = roleService.getUserRoles(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRoleMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试检查用户是否拥有角色 - 拥有")
    public void testHasRole_True() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        boolean result = roleService.hasRole(TEST_USER_ID, "user");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查用户是否拥有角色 - 不拥有")
    public void testHasRole_False() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        boolean result = roleService.hasRole(TEST_USER_ID, "admin");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("测试检查用户是否为管理员 - 是管理员")
    public void testIsAdmin_True() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setRoleName("管理员");
        adminRole.setRoleCode("admin");

        UserRole adminUserRole = new UserRole();
        adminUserRole.setId(2L);
        adminUserRole.setUserId(TEST_USER_ID);
        adminUserRole.setRoleId(2L);

        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(adminUserRole));
        when(roleMapper.selectById(2L)).thenReturn(adminRole);

        // Act
        boolean result = roleService.isAdmin(TEST_USER_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查用户是否为管理员 - 不是管理员")
    public void testIsAdmin_False() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        boolean result = roleService.isAdmin(TEST_USER_ID);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("测试根据角色查询用户列表 - 成功")
    public void testGetUsersByRole_Success() {
        // Arrange
        when(roleMapper.selectByCode("user")).thenReturn(testRole);
        when(userRoleMapper.selectByRoleId(TEST_ROLE_ID)).thenReturn(Arrays.asList(testUserRole));
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        List<UserVO> result = roleService.getUsersByRole("user");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_USER_ID, result.get(0).getId());
        assertEquals("测试用户", result.get(0).getNickname());

        verify(roleMapper, times(1)).selectByCode("user");
        verify(userRoleMapper, times(1)).selectByRoleId(TEST_ROLE_ID);
    }

    @Test
    @DisplayName("测试根据角色查询用户列表 - 角色不存在")
    public void testGetUsersByRole_RoleNotExists() {
        // Arrange
        when(roleMapper.selectByCode("user")).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.getUsersByRole("user");
        });

        assertEquals("角色不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试批量为用户分配角色 - 成功")
    public void testAssignRoles_Success() {
        // Arrange
        Long roleId1 = 1L;
        Long roleId2 = 2L;
        List<Long> roleIds = Arrays.asList(roleId1, roleId2);

        when(userRoleMapper.insert(any(UserRole.class))).thenAnswer(invocation -> {
            UserRole ur = invocation.getArgument(0);
            ur.setId(100L);
            return null;
        });

        // Act
        assertDoesNotThrow(() -> roleService.assignRoles(TEST_USER_ID, roleIds));

        // Assert
        verify(userRoleMapper, times(1)).deleteByUserId(TEST_USER_ID);
        verify(userRoleMapper, times(2)).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("测试获取用户权限列表 - 管理员")
    public void testGetUserPermissions_Admin() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setRoleCode("admin");

        UserRole adminUserRole = new UserRole();
        adminUserRole.setUserId(TEST_USER_ID);
        adminUserRole.setRoleId(2L);

        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(adminUserRole));
        when(roleMapper.selectById(2L)).thenReturn(adminRole);

        // Act
        List<String> result = roleService.getUserPermissions(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("*:*:*"));
    }

    @Test
    @DisplayName("测试获取用户权限列表 - 普通用户")
    public void testGetUserPermissions_User() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        List<String> result = roleService.getUserPermissions(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("user:info:view"));
        assertTrue(result.contains("hospital:view"));
    }

    @Test
    @DisplayName("测试检查用户是否拥有权限 - 有权限")
    public void testHasPermission_True() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setRoleCode("admin");

        UserRole adminUserRole = new UserRole();
        adminUserRole.setUserId(TEST_USER_ID);
        adminUserRole.setRoleId(2L);

        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(adminUserRole));
        when(roleMapper.selectById(2L)).thenReturn(adminRole);

        // Act
        boolean result = roleService.hasPermission(TEST_USER_ID, "any:permission");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("测试检查用户是否拥有权限 - 无权限")
    public void testHasPermission_False() {
        // Arrange
        when(userRoleMapper.selectByUserId(TEST_USER_ID)).thenReturn(Arrays.asList(testUserRole));
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        boolean result = roleService.hasPermission(TEST_USER_ID, "admin:permission");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("测试分页查询角色列表 - 成功")
    public void testGetRoleList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("管理员");
        role2.setRoleCode("admin");

        List<Role> roleList = Arrays.asList(testRole, role2);
        when(roleMapper.selectAll()).thenReturn(roleList);

        // Act
        PageResult<RoleVO> result = roleService.getRoleList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getList().size());

        verify(roleMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试获取角色详情 - 成功")
    public void testGetRoleDetail_Success() {
        // Arrange
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);

        // Act
        RoleVO result = roleService.getRoleDetail(TEST_ROLE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ROLE_ID, result.getId());
        assertEquals("普通用户", result.getRoleName());
        assertEquals("user", result.getRoleCode());

        verify(roleMapper, times(1)).selectById(TEST_ROLE_ID);
    }

    @Test
    @DisplayName("测试获取角色详情 - 角色不存在")
    public void testGetRoleDetail_NotExists() {
        // Arrange
        when(roleMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.getRoleDetail(999L);
        });

        assertEquals("角色不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试添加角色 - 成功")
    public void testAddRole_Success() {
        // Arrange
        RoleDTO dto = new RoleDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("tester");

        when(roleMapper.selectByCode("tester")).thenReturn(null);
        when(roleMapper.insert(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(100L);
            return null;
        });

        // Act
        RoleVO result = roleService.addRole(dto);

        // Assert
        assertNotNull(result);
        assertEquals("测试角色", result.getRoleName());
        assertEquals("tester", result.getRoleCode());

        verify(roleMapper, times(1)).selectByCode("tester");
        verify(roleMapper, times(1)).insert(any(Role.class));
    }

    @Test
    @DisplayName("测试添加角色 - 角色编码已存在")
    public void testAddRole_CodeExists() {
        // Arrange
        RoleDTO dto = new RoleDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("user");

        when(roleMapper.selectByCode("user")).thenReturn(testRole);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.addRole(dto);
        });

        assertEquals("角色编码已存在", exception.getMessage());
        verify(roleMapper, never()).insert(any(Role.class));
    }

    @Test
    @DisplayName("测试修改角色 - 成功")
    public void testUpdateRole_Success() {
        // Arrange
        RoleDTO dto = new RoleDTO();
        dto.setRoleName("新角色名");
        dto.setRoleCode("newcode");

        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);
        when(roleMapper.selectByCode("newcode")).thenReturn(null);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        // Act
        RoleVO result = roleService.updateRole(TEST_ROLE_ID, dto);

        // Assert
        assertNotNull(result);
        verify(roleMapper, times(1)).updateById(any(Role.class));
    }

    @Test
    @DisplayName("测试修改角色 - 角色不存在")
    public void testUpdateRole_NotExists() {
        // Arrange
        RoleDTO dto = new RoleDTO();
        dto.setRoleName("新角色名");
        dto.setRoleCode("newcode");

        when(roleMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.updateRole(999L, dto);
        });

        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @DisplayName("测试删除角色 - 成功")
    public void testDeleteRole_Success() {
        // Arrange
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);
        when(userRoleMapper.selectByRoleId(TEST_ROLE_ID)).thenReturn(Collections.emptyList());
        when(roleMapper.deleteById(TEST_ROLE_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> roleService.deleteRole(TEST_ROLE_ID));

        // Assert
        verify(roleMapper, times(1)).deleteById(TEST_ROLE_ID);
    }

    @Test
    @DisplayName("测试删除角色 - 角色不存在")
    public void testDeleteRole_NotExists() {
        // Arrange
        when(roleMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(999L);
        });

        assertEquals("角色不存在", exception.getMessage());
        verify(roleMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试删除角色 - 角色正在使用")
    public void testDeleteRole_RoleInUse() {
        // Arrange
        when(roleMapper.selectById(TEST_ROLE_ID)).thenReturn(testRole);
        when(userRoleMapper.selectByRoleId(TEST_ROLE_ID)).thenReturn(Arrays.asList(testUserRole));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(TEST_ROLE_ID);
        });

        assertEquals("该角色正在被使用，无法删除", exception.getMessage());
        verify(roleMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试获取所有角色列表 - 成功")
    public void testGetAllRoles_Success() {
        // Arrange
        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("管理员");
        role2.setRoleCode("admin");

        List<Role> roleList = Arrays.asList(testRole, role2);
        when(roleMapper.selectAll()).thenReturn(roleList);

        // Act
        List<RoleVO> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("普通用户", result.get(0).getRoleName());
        assertEquals("管理员", result.get(1).getRoleName());

        verify(roleMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试获取所有角色列表 - 空列表")
    public void testGetAllRoles_Empty() {
        // Arrange
        when(roleMapper.selectAll()).thenReturn(Collections.emptyList());

        // Act
        List<RoleVO> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleMapper, times(1)).selectAll();
    }
}
