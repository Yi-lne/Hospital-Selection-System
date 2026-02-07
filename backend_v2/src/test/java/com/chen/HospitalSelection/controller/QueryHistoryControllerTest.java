package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.QueryHistoryVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 查询历史控制器测试类
 * 使用集成测试，需要JWT认证
 *
 * @author chen
 */
public class QueryHistoryControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;  // 使用 Spring 配置的 ObjectMapper，支持 Java 8 时间类型

    @BeforeEach
    public void setUp() {
        super.beforeTest();
    }

    @Test
    @DisplayName("测试查询历史列表 - 成功")
    public void testGetQueryHistoryList_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/query-history/list")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("测试查询历史列表 - 未登录")
    public void testGetQueryHistoryList_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.get("/query-history/list")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }

    @Test
    @DisplayName("测试记录查询 - 成功（医院查询）")
    public void testRecordQuery_Hospital_Success() throws Exception {
        // Arrange
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("queryType", 1); // 医院查询
        recordData.put("targetId", 1L);
        recordData.put("queryParams", "{\"hospitalLevel\":\"grade3A\",\"cityCode\":\"440100\"}");

        String jsonContent = objectMapper.writeValueAsString(recordData);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/query-history/record")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("记录成功"));
    }

    @Test
    @DisplayName("测试记录查询 - 成功（医生查询）")
    public void testRecordQuery_Doctor_Success() throws Exception {
        // Arrange
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("queryType", 2); // 医生查询
        recordData.put("targetId", 1L);
        recordData.put("queryParams", "{\"title\":\"主任医师\"}");

        String jsonContent = objectMapper.writeValueAsString(recordData);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/query-history/record")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("记录成功"));
    }

    @Test
    @DisplayName("测试记录查询 - 成功（话题查询）")
    public void testRecordQuery_Topic_Success() throws Exception {
        // Arrange
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("queryType", 3); // 话题查询
        recordData.put("targetId", 1L);
        recordData.put("queryParams", "{\"boardLevel1\":\"心血管区\"}");

        String jsonContent = objectMapper.writeValueAsString(recordData);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/query-history/record")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("记录成功"));
    }

    @Test
    @DisplayName("测试记录查询 - 未登录")
    public void testRecordQuery_Unauthorized() throws Exception {
        // Arrange
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("queryType", 1);
        recordData.put("targetId", 1L);

        String jsonContent = objectMapper.writeValueAsString(recordData);

        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.post("/query-history/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }

    @Test
    @DisplayName("测试删除历史记录 - 成功")
    public void testDeleteQueryHistory_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/query-history/1")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("测试删除历史记录 - 未登录")
    public void testDeleteQueryHistory_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.delete("/query-history/1"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }

    @Test
    @DisplayName("测试清空历史 - 成功")
    public void testClearQueryHistory_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/query-history/clear")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("清空成功"));
    }

    @Test
    @DisplayName("测试清空历史 - 未登录")
    public void testClearQueryHistory_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.delete("/query-history/clear"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }
}
