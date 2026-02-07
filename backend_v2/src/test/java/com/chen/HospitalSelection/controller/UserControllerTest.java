package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户控制器集成测试类
 * 使用真实的Spring容器和H2内存数据库
 *
 * @author chen
 */
public class UserControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("测试用户注册 - 成功")
    public void testRegister_Success() throws Exception {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone("13900000001");
        dto.setPassword("Test123456");
        dto.setNickname("新用户");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.userInfo.phone").value("139****0001"))
                .andExpect(jsonPath("$.data.userInfo.nickname").value("新用户"))
                .andReturn();

        // 验证数据库中真的有这个用户了（集成测试的关键）
        User savedUser = userMapper.selectByPhone("13900000001");
        assertNotNull(savedUser, "用户应该被保存到数据库");
        assertEquals("新用户", savedUser.getNickname());
        assertEquals(1, savedUser.getStatus()); // 正常状态
    }

    @Test
    @DisplayName("测试用户注册 - 手机号已存在")
    public void testRegister_PhoneAlreadyExists() throws Exception {
        // Arrange - 先创建一个用户
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone("13900000001");
        dto.setPassword("Test123456");
        dto.setNickname("用户1");

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Act - 再次用相同手机号注册
        dto.setNickname("用户2");

        // Assert
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400)) // 业务错误
                .andExpect(jsonPath("$.message").value("手机号已被注册"));
    }

    @Test
    @DisplayName("测试用户注册 - 参数校验失败")
    public void testRegister_ValidationFailed() throws Exception {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setPhone("123"); // 无效的手机号
        dto.setPassword("123"); // 无效的密码

        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(400));  // 业务状态码在响应体中
    }

    @Test
    @DisplayName("测试用户登录 - 成功")
    public void testLogin_Success() throws Exception {
        // Arrange - 先注册一个用户
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000002");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("测试用户");

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        // Act - 登录
        UserLoginDTO dto = new UserLoginDTO();
        dto.setPhone("13900000002");
        dto.setPassword("Test123456");

        // Assert
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.userInfo.phone").value("139****0002"))
                .andExpect(jsonPath("$.data.userInfo.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("测试用户登录 - 密码错误")
    public void testLogin_WrongPassword() throws Exception {
        // Arrange
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000003");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("测试用户");

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        // Act
        UserLoginDTO dto = new UserLoginDTO();
        dto.setPhone("13900000003");
        dto.setPassword("WrongPassword");

        // Assert
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));
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
    @DisplayName("测试获取用户信息 - 成功")
    public void testGetUserInfo_Success() throws Exception {
        // Arrange - 先创建一个用户
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000004");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("测试用户");

        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        // 提取token
        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        // Act & Assert
        mockMvc.perform(get("/user/info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("139****0004"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("测试更新用户信息 - 成功")
    public void testUpdateUserInfo_Success() throws Exception {
        // Arrange - 先创建一个用户
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000005");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("原昵称");

        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        // Act
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setNickname("新昵称");
        dto.setGender(2);

        // Assert
        mockMvc.perform(put("/user/info")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.nickname").value("新昵称"));

        // 验证数据库
        User updatedUser = userMapper.selectByPhone("13900000005");
        assertEquals("新昵称", updatedUser.getNickname());
        assertEquals(2, updatedUser.getGender());
    }

    @Test
    @DisplayName("测试修改密码 - 成功")
    public void testUpdatePassword_Success() throws Exception {
        // Arrange
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000006");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("测试用户");

        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        // Act
        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("Test123456");
        dto.setNewPassword("NewPassword123");

        // Assert
        mockMvc.perform(put("/user/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        // 验证新密码可以登录
        UserLoginDTO loginDto = new UserLoginDTO();
        loginDto.setPhone("13900000006");
        loginDto.setPassword("NewPassword123");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试修改密码 - 原密码错误")
    public void testUpdatePassword_WrongOldPassword() throws Exception {
        // Arrange
        UserRegisterDTO registerDto = new UserRegisterDTO();
        registerDto.setPhone("13900000007");
        registerDto.setPassword("Test123456");
        registerDto.setNickname("测试用户");

        MvcResult result = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).path("data").path("token").asText();

        // Act
        PasswordUpdateDTO dto = new PasswordUpdateDTO();
        dto.setOldPassword("WrongPassword");
        dto.setNewPassword("NewPassword123");

        // Assert
        mockMvc.perform(put("/user/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("原密码错误"));
    }
}
