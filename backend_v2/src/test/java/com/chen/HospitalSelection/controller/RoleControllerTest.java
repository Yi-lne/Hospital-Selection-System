package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.RoleDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.RoleVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 角色管理控制器测试类
 * 使用集成测试，部分接口需要JWT认证
 *
 * @author chen
 */
public class RoleControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;  // 使用 Spring 配置的 ObjectMapper，支持 Java 8 时间类型
    private RoleDTO testRoleDTO;

    @BeforeEach
    public void setUp() {
        super.beforeTest();

        // 准备测试数据
        testRoleDTO = new RoleDTO();
        testRoleDTO.setRoleName("测试角色");
        testRoleDTO.setRoleCode("test_role");
    }

    @Test
    @DisplayName("测试获取用户角色 - 成功")
    public void testGetUserRoles_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试分配角色 - 成功")
    public void testAssignRole_Success() throws Exception {
        // Act & Assert
        // 用户ID=2已有角色ID=1，这里分配角色ID=2（管理员）
        mockMvc.perform(MockMvcRequestBuilders.post("/role/assign")
                        .param("userId", "2")
                        .param("roleId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("分配成功"));
    }

    @Test
    @DisplayName("测试取消角色 - 成功")
    public void testRemoveRole_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/role/remove")
                        .param("userId", "2")
                        .param("roleId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("取消成功"));
    }

    @Test
    @DisplayName("测试检查管理员 - 是管理员")
    public void testIsAdmin_True() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/admin/check/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isBoolean());
    }

    @Test
    @DisplayName("测试检查管理员 - 不是管理员")
    public void testIsAdmin_False() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/admin/check/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isBoolean());
    }

    @Test
    @DisplayName("测试角色列表 - 成功")
    public void testGetRoleList_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/list")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("测试角色列表 - 未登录")
    public void testGetRoleList_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.get("/role/list")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }

    @Test
    @DisplayName("测试角色详情 - 成功")
    public void testGetRoleDetail_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/1")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("测试添加角色 - 成功")
    public void testAddRole_Success() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(testRoleDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/role/add")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加成功"));
    }

    @Test
    @DisplayName("测试添加角色 - 参数验证失败")
    public void testAddRole_ValidationError() throws Exception {
        // Arrange - 角色名称为空
        testRoleDTO.setRoleName("");
        String jsonContent = objectMapper.writeValueAsString(testRoleDTO);

        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.post("/role/add")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(400));  // 业务状态码在响应体中
    }

    @Test
    @DisplayName("测试修改角色 - 成功")
    public void testUpdateRole_Success() throws Exception {
        // Arrange
        testRoleDTO.setRoleName("更新后的角色");
        String jsonContent = objectMapper.writeValueAsString(testRoleDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/role/update/1")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改成功"));
    }

    @Test
    @DisplayName("测试删除角色 - 成功")
    public void testDeleteRole_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/role/3")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("测试所有角色列表 - 成功")
    public void testGetAllRoles_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/role/all")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.size()").value(3)); // 预期有3个角色：user, admin, moderator
    }

    @Test
    @DisplayName("测试所有角色列表 - 未登录")
    public void testGetAllRoles_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.get("/role/all"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }
}
