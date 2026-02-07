package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.PasswordResetDTO;
import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.UserProfileVO;
import com.chen.HospitalSelection.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试用户注册、登录、信息管理等功能
 *
 * @author chen
 */
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.UserServiceImpl userService;

    private User testUser;
    private static final String TEST_PHONE = "13800000001";
    private static final String TEST_PASSWORD = "Test123456";
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 手动注入jwtUtil（因为@Autowired字段注入，Mockito的@InjectMocks无法自动注入）
        try {
            Field jwtUtilField = com.chen.HospitalSelection.service.impl.UserServiceImpl.class.getDeclaredField("jwtUtil");
            jwtUtilField.setAccessible(true);
            jwtUtilField.set(userService, jwtUtil);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject jwtUtil mock", e);
        }

        // 创建测试用户
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setPhone(TEST_PHONE);
        testUser.setPassword("encoded-password-hash"); // 加密后的密码
        testUser.setNickname("测试用户");
        testUser.setAvatar("http://example.com/default-avatar.png");
        testUser.setGender(1);
        testUser.setStatus(1);
        testUser.setIsDeleted(0);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试用户注册 - 成功")
    public void testRegister_Success() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setPhone("13900000001");
            dto.setPassword("Test123456");
            dto.setNickname("新用户");

            when(userMapper.selectByPhone(anyString())).thenReturn(null);
            // Mock密码编码
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.encode(anyString()))
                    .thenReturn("encoded-password");
            when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(2L);
                return null;
            });

            // Act
            UserVO result = userService.register(dto);

            // Assert
            assertNotNull(result);
            assertEquals("新用户", result.getNickname());
            assertEquals("139****0001", result.getPhone()); // 手机号脱敏

            verify(userMapper, times(1)).selectByPhone("13900000001");
            verify(userMapper, times(1)).insert(any(User.class));
        }
    }

    @Test
    @DisplayName("测试用户注册 - 手机号已存在")
    public void testRegister_PhoneAlreadyExists() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone(TEST_PHONE);
        dto.setPassword(TEST_PASSWORD);

        when(userMapper.selectByPhone(TEST_PHONE)).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.register(dto);
        });

        assertEquals("手机号已被注册", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("测试用户登录 - 成功")
    public void testLogin_Success() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            UserLoginDTO dto = new UserLoginDTO();
            dto.setPhone(TEST_PHONE);
            dto.setPassword(TEST_PASSWORD);

            when(userMapper.selectByPhone(TEST_PHONE)).thenReturn(testUser);
            // Mock密码验证为成功
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.matches(eq(TEST_PASSWORD), anyString()))
                    .thenReturn(true);

            // Act
            UserVO result = userService.login(dto);

            // Assert
            assertNotNull(result);
            assertEquals(TEST_USER_ID, result.getId());
            assertEquals("测试用户", result.getNickname());
            assertEquals("138****0001", result.getPhone());

            verify(userMapper, times(1)).selectByPhone(TEST_PHONE);
        }
    }

    @Test
    @DisplayName("测试用户登录 - 用户不存在")
    public void testLogin_UserNotExists() {
        // Arrange
        UserLoginDTO dto = new UserLoginDTO();
        dto.setPhone("13900000001");
        dto.setPassword(TEST_PASSWORD);

        when(userMapper.selectByPhone("13900000001")).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(dto);
        });

        assertEquals("手机号或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试用户登录 - 账号被禁用")
    public void testLogin_AccountDisabled() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            testUser.setStatus(0);
            UserLoginDTO dto = new UserLoginDTO();
            dto.setPhone(TEST_PHONE);
            dto.setPassword(TEST_PASSWORD);

            when(userMapper.selectByPhone(TEST_PHONE)).thenReturn(testUser);
            // Mock密码验证为成功
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.matches(eq(TEST_PASSWORD), anyString()))
                    .thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.login(dto);
            });

            assertEquals("账号已被禁用", exception.getMessage());
        }
    }

    @Test
    @DisplayName("测试获取用户信息 - 成功")
    public void testGetUserInfo_Success() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        UserVO result = userService.getUserInfo(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getId());
        assertEquals("测试用户", result.getNickname());
        assertEquals("138****0001", result.getPhone());

        verify(userMapper, times(1)).selectById(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取用户信息 - 用户不存在")
    public void testGetUserInfo_UserNotExists() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserInfo(999L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取用户完整资料 - 成功")
    public void testGetUserProfile_Success() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        UserProfileVO result = userService.getUserProfile(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getId());
        assertEquals("测试用户", result.getNickname());
        assertEquals("138****0001", result.getPhone());
        assertEquals(Integer.valueOf(1), result.getGender());
    }

    @Test
    @DisplayName("测试更新用户信息 - 成功")
    public void testUpdateUserInfo_Success() {
        // Arrange
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");
        dto.setGender(2);

        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            testUser.setNickname(user.getNickname());
            testUser.setGender(user.getGender());
            return null;
        });

        // Act
        UserVO result = userService.updateUserInfo(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals("新昵称", result.getNickname());
        assertEquals(Integer.valueOf(2), result.getGender());

        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("测试修改密码 - 成功")
    public void testUpdatePassword_Success() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            PasswordUpdateDTO dto = new PasswordUpdateDTO();
            dto.setOldPassword(TEST_PASSWORD);
            dto.setNewPassword("NewPassword123");

            when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
            // Mock密码验证为成功
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.matches(eq(TEST_PASSWORD), anyString()))
                    .thenReturn(true);
            // Mock密码编码 - 使用anyString()来匹配任何参数
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.encode(anyString()))
                    .thenReturn("encoded-new-password");
            when(userMapper.updatePassword(eq(TEST_USER_ID), anyString())).thenReturn(1);

            // Act
            assertDoesNotThrow(() -> userService.updatePassword(TEST_USER_ID, dto));

            // Assert
            verify(userMapper, times(1)).updatePassword(eq(TEST_USER_ID), eq("encoded-new-password"));
        }
    }

    @Test
    @DisplayName("测试修改密码 - 原密码错误")
    public void testUpdatePassword_WrongOldPassword() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            PasswordUpdateDTO dto = new PasswordUpdateDTO();
            dto.setOldPassword("WrongPassword");
            dto.setNewPassword("NewPassword123");

            when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
            // Mock密码验证为失败
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.matches(eq("WrongPassword"), anyString()))
                    .thenReturn(false);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                userService.updatePassword(TEST_USER_ID, dto);
            });

            assertEquals("原密码错误", exception.getMessage());
            verify(userMapper, never()).updatePassword(anyLong(), anyString());
        }
    }

    @Test
    @DisplayName("测试重置密码 - 成功")
    public void testResetPassword_Success() {
        // 使用Mockito.mockito-inline来mock静态方法
        try (MockedStatic<com.chen.HospitalSelection.util.PasswordUtil> passwordUtilMock = mockStatic(com.chen.HospitalSelection.util.PasswordUtil.class)) {
            // Arrange
            PasswordResetDTO dto = new PasswordResetDTO();
            dto.setPhone(TEST_PHONE);
            dto.setVerificationCode("123456");
            dto.setNewPassword("NewPassword123");

            when(userMapper.selectByPhone(TEST_PHONE)).thenReturn(testUser);
            // Mock密码编码
            passwordUtilMock.when(() -> com.chen.HospitalSelection.util.PasswordUtil.encode(anyString()))
                    .thenReturn("encoded-new-password");
            when(userMapper.updatePassword(eq(TEST_USER_ID), anyString())).thenReturn(1);

            // Act
            assertDoesNotThrow(() -> userService.resetPassword(dto));

            // Assert
            verify(userMapper, times(1)).updatePassword(eq(TEST_USER_ID), anyString());
        }
    }

    @Test
    @DisplayName("测试重置密码 - 验证码错误")
    public void testResetPassword_WrongVerificationCode() {
        // Arrange
        PasswordResetDTO dto = new PasswordResetDTO();
        dto.setPhone(TEST_PHONE);
        dto.setVerificationCode("000000");
        dto.setNewPassword("NewPassword123");

        when(userMapper.selectByPhone(TEST_PHONE)).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.resetPassword(dto);
        });

        assertEquals("验证码错误", exception.getMessage());
        verify(userMapper, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    @DisplayName("测试分页查询用户列表 - 成功")
    public void testGetUserList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        User user2 = new User();
        user2.setId(2L);
        user2.setPhone("13900000002");
        user2.setNickname("用户2");

        List<User> userList = Arrays.asList(testUser, user2);
        when(userMapper.selectAll()).thenReturn(userList);

        // Act
        PageResult<UserVO> result = userService.getUserList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getList().size());

        verify(userMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试更新用户状态 - 成功")
    public void testUpdateUserStatus_Success() {
        // Arrange
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(userMapper.updateStatus(TEST_USER_ID, 0)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> userService.updateUserStatus(TEST_USER_ID, 0));

        // Assert
        verify(userMapper, times(1)).updateStatus(TEST_USER_ID, 0);
    }

    @Test
    @DisplayName("测试上传头像 - 成功")
    public void testUploadAvatar_Success() {
        // Arrange
        String newAvatarUrl = "http://example.com/new-avatar.png";

        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(userMapper.updateAvatar(TEST_USER_ID, newAvatarUrl)).thenReturn(1);

        // Act
        UserVO result = userService.uploadAvatar(TEST_USER_ID, newAvatarUrl);

        // Assert
        assertNotNull(result);
        assertEquals(newAvatarUrl, result.getAvatar());

        verify(userMapper, times(1)).updateAvatar(TEST_USER_ID, newAvatarUrl);
    }
}
