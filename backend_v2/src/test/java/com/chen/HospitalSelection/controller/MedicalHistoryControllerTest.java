package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.MedicalHistoryDTO;
import com.chen.HospitalSelection.vo.MedicalHistoryVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 病史控制器测试类
 * 使用集成测试，需要JWT认证
 *
 * @author chen
 */
public class MedicalHistoryControllerTest extends IntegrationTestBase {

    private MedicalHistoryDTO testMedicalHistoryDTO;
    private MedicalHistoryVO testMedicalHistoryVO;

    @Autowired
    private ObjectMapper objectMapper;  // 使用 Spring 配置的 ObjectMapper，支持 Java 8 时间类型

    @BeforeEach
    public void setUp() {
        super.beforeTest();

        // 准备测试数据
        testMedicalHistoryDTO = new MedicalHistoryDTO();
        testMedicalHistoryDTO.setDiseaseName("高血压");
        testMedicalHistoryDTO.setDiagnosisDate(LocalDate.of(2024, 1, 15));
        testMedicalHistoryDTO.setStatus(1); // 治疗中

        testMedicalHistoryVO = new MedicalHistoryVO();
        testMedicalHistoryVO.setId(1L);
        testMedicalHistoryVO.setUserId(TEST_USER_ID);
        testMedicalHistoryVO.setDiseaseName("高血压");
        testMedicalHistoryVO.setDiagnosisDate(LocalDate.of(2024, 1, 15));
        testMedicalHistoryVO.setStatus(1);
    }

    @Test
    @DisplayName("测试病史列表 - 成功")
    public void testGetMedicalHistoryList_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/medical-history/list")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试病史列表 - 未登录")
    public void testGetMedicalHistoryList_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.get("/medical-history/list"))  // 没有提供 token
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }

    @Test
    @DisplayName("测试添加病史 - 成功")
    public void testAddMedicalHistory_Success() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(testMedicalHistoryDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/medical-history/add")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加成功"));
    }

    @Test
    @DisplayName("测试添加病史 - 参数验证失败")
    public void testAddMedicalHistory_ValidationError() throws Exception {
        // Arrange - 疾病名称为空
        testMedicalHistoryDTO.setDiseaseName("");
        String jsonContent = objectMapper.writeValueAsString(testMedicalHistoryDTO);

        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.post("/medical-history/add")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(400))  // 业务状态码在响应体中
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("测试病史详情 - 成功")
    public void testGetMedicalHistoryDetail_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/medical-history/1")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试修改病史 - 成功")
    public void testUpdateMedicalHistory_Success() throws Exception {
        // Arrange
        testMedicalHistoryDTO.setDiseaseName("冠心病");
        testMedicalHistoryDTO.setStatus(2); // 已康复
        String jsonContent = objectMapper.writeValueAsString(testMedicalHistoryDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/medical-history/update/1")
                        .header("Authorization", getDefaultAuthorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改成功"));
    }

    @Test
    @DisplayName("测试删除病史 - 成功")
    public void testDeleteMedicalHistory_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/medical-history/1")
                        .header("Authorization", getDefaultAuthorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("测试删除病史 - 未登录")
    public void testDeleteMedicalHistory_Unauthorized() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(MockMvcRequestBuilders.delete("/medical-history/1"))  // 没有提供 token
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(500));  // 业务状态码在响应体中（系统错误）
    }
}
