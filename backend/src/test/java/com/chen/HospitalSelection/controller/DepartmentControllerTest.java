package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.exception.ResourceNotFoundException;
import com.chen.HospitalSelection.service.DepartmentService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 科室控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private DepartmentVO departmentVO1;
    private DepartmentVO departmentVO2;

    @BeforeEach
    public void setUp() {
        // 科室1：心内科
        departmentVO1 = new DepartmentVO();
        departmentVO1.setId(1L);
        departmentVO1.setHospitalId(1L);
        departmentVO1.setDeptName("心内科");
        departmentVO1.setDeptIntro("国家临床重点专科");

        // 科室2：心血管外科
        departmentVO2 = new DepartmentVO();
        departmentVO2.setId(2L);
        departmentVO2.setHospitalId(1L);
        departmentVO2.setDeptName("心血管外科");
        departmentVO2.setDeptIntro("肿瘤综合治疗中心");
    }

    @Test
    @DisplayName("测试医院科室列表 - 成功")
    public void testGetHospitalDepartments_Success() throws Exception {
        // Arrange
        when(departmentService.getDepartmentsByHospital(1L)).thenReturn(Arrays.asList(departmentVO1, departmentVO2));

        // Act & Assert
        mockMvc.perform(get("/department/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].deptName").value("心内科"))
                .andExpect(jsonPath("$.data[1].deptName").value("心血管外科"));
    }

    @Test
    @DisplayName("测试医院科室列表 - 空结果")
    public void testGetHospitalDepartments_Empty() throws Exception {
        // Arrange
        when(departmentService.getDepartmentsByHospital(anyLong())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/department/hospital/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试科室详情 - 成功")
    public void testGetDepartmentDetail_Success() throws Exception {
        // Arrange
        when(departmentService.getDepartmentDetail(1L)).thenReturn(departmentVO1);

        // Act & Assert
        mockMvc.perform(get("/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.deptName").value("心内科"))
                .andExpect(jsonPath("$.data.deptIntro").value("国家临床重点专科"));
    }

    @Test
    @DisplayName("测试科室详情 - 科室不存在")
    public void testGetDepartmentDetail_NotFound() throws Exception {
        // Arrange
        // 后端实际会抛出ResourceNotFoundException，这里模拟真实的业务行为
        when(departmentService.getDepartmentDetail(999L))
                .thenThrow(ResourceNotFoundException.departmentNotFound());

        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(get("/department/999"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(404))  // 业务状态码在响应体中
                .andExpect(jsonPath("$.message").exists());
    }
}
