package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.service.UserService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private com.chen.HospitalSelection.util.FileUploadUtil fileUploadUtil;

    private UserVO testUserVO;
    private static final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    public void setUp() {
        testUserVO = new UserVO();
        testUserVO.setId(1L);
        testUserVO.setPhone("138****0001");
        testUserVO.setNickname("测试用户");
        testUserVO.setAvatar("http://example.com/avatar.png");
        testUserVO.setGender(1);

        when(jwtUtil.getUserIdFromToken(TEST_TOKEN)).thenReturn(1L);
        when(jwtUtil.getPhoneFromToken(TEST_TOKEN)).thenReturn("13800000001");
    }

    @Test
    @DisplayName("测试用户注册 - 成功")
    public void testRegister_Success() throws Exception {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone("13900000001");
        dto.setPassword("Test123456");
        dto.setNickname("新用户");

        when(userService.register(any(UserRegisterDTO.class))).thenReturn(testUserVO);

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("测试用户注册 - 参数校验失败")
    public void testRegister_ValidationFailed() throws Exception {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone("123"); // 无效的手机号
        dto.setPassword("123"); // 无效的密码

        // Act & Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试用户登录 - 成功")
    public void testLogin_Success() throws Exception {
        // Arrange
        UserLoginDTO dto = new UserLoginDTO();
        dto.setPhone("13800000001");
        dto.setPassword("Test123456");

        when(userService.login(any(UserLoginDTO.class))).thenReturn(testUserVO);

        // Act & Assert
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.phone").value("138****0001"));
    }

    @Test
    @DisplayName("测试用户登出 - 成功")
    public void testLogout_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    @DisplayName("测试修改密码 - 成功")
    public void testUpdatePassword_Success() throws Exception {
        // Arrange
        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("Test123456");
        dto.setNewPassword("NewPassword123");

        // Act & Assert
        mockMvc.perform(put("/user/password")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码修改成功"));
    }

    @Test
    @DisplayName("测试更新用户信息 - 成功")
    public void testUpdateUserInfo_Success() throws Exception {
        // Arrange
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");
        dto.setGender(2);

        when(userService.updateUserInfo(anyLong(), any(UserUpdateDTO.class))).thenReturn(testUserVO);

        // Act & Assert
        mockMvc.perform(put("/user/info")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    @DisplayName("测试获取用户信息 - 成功")
    public void testGetUserInfo_Success() throws Exception {
        // Arrange
        com.chen.HospitalSelection.vo.UserProfileVO profileVO = new com.chen.HospitalSelection.vo.UserProfileVO();
        profileVO.setId(1L);
        profileVO.setPhone("138****0001");
        profileVO.setNickname("测试用户");

        when(userService.getUserProfile(anyLong())).thenReturn(profileVO);

        // Act & Assert
        mockMvc.perform(get("/user/info")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }
}