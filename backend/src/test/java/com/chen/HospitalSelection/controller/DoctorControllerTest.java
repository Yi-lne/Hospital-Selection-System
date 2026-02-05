package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 医生控制器集成测试类
 *
 * @author chen
 */
@Transactional
public class DoctorControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private HospitalMapper hospitalMapper;

    private Hospital testHospital;
    private Doctor testDoctor;

    /**
     * 初始化测试数据
     */
    @Override
    protected void beforeTest() {
        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setHospitalName("测试医院");
        testHospital.setHospitalLevel("grade3A");
        testHospital.setProvinceCode("440000");
        testHospital.setCityCode("440100");
        testHospital.setAreaCode("440103");
        testHospital.setAddress("测试地址");
        testHospital.setPhone("020-12345678");
        testHospital.setKeyDepartments("心内科");
        testHospital.setRating(new BigDecimal("4.5"));
        testHospital.setReviewCount(100);
        testHospital.setIsMedicalInsurance(1);
        testHospital.setIsDeleted(0);
        testHospital.setCreateTime(LocalDateTime.now());
        testHospital.setUpdateTime(LocalDateTime.now());
        hospitalMapper.insert(testHospital);

        // 创建测试科室
        Department department = new Department();
        department.setHospitalId(testHospital.getId());
        department.setDeptName("心内科");
        department.setDeptIntro("心脏内科诊疗");
        department.setIsDeleted(0);
        // departmentMapper.insert(department); // 假设有这个方法

        // 创建测试医生
        testDoctor = new Doctor();
        testDoctor.setDoctorName("张医生");
        testDoctor.setHospitalId(testHospital.getId());
        testDoctor.setDeptId(1L);
        testDoctor.setTitle("主任医师");
        testDoctor.setSpecialty("心脏病诊疗");
        testDoctor.setScheduleTime("周一上午");
        testDoctor.setConsultationFee(new BigDecimal("50.00"));
        testDoctor.setRating(new BigDecimal("4.8"));
        testDoctor.setReviewCount(200);
        testDoctor.setIsDeleted(0);
        testDoctor.setCreateTime(LocalDateTime.now());
        testDoctor.setUpdateTime(LocalDateTime.now());
        doctorMapper.insert(testDoctor);
    }

    @Test
    @DisplayName("测试获取医生详情 - 成功")
    public void testGetDoctorDetail_Success() throws Exception {
        // Arrange - 使用类变量中的测试医生

        // Act & Assert
        mockMvc.perform(get("/doctor/" + testDoctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.doctorName").value("张医生"))
                .andExpect(jsonPath("$.data.title").value("主任医师"))
                .andExpect(jsonPath("$.data.specialty").value("心脏病诊疗"));
    }

    @Test
    @DisplayName("测试获取医生列表 - 成功")
    public void testGetDoctorList_Success() throws Exception {
        // Act & Assert - GET 请求，参数通过查询字符串传递
        mockMvc.perform(get("/doctor/list")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").exists());
    }

    @Test
    @DisplayName("测试按医院查询医生 - 成功")
    public void testGetDoctorsByHospital_Success() throws Exception {
        // Arrange - 使用类变量中的测试医院

        // Act & Assert
        mockMvc.perform(get("/doctor/hospital/" + testHospital.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());  // 修正：检查 $.data 而不是 $
    }

    @Test
    @DisplayName("测试医生筛选 - 成功")
    public void testFilterDoctors_Success() throws Exception {
        // Arrange
        DoctorFilterDTO dto = new DoctorFilterDTO();
        dto.setPage(1);
        dto.setPageSize(10);
        dto.setTitle("主任医师");

        // Act & Assert
        mockMvc.perform(post("/doctor/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @DisplayName("测试获取医生详情 - 不存在")
    public void testGetDoctorDetail_NotFound() throws Exception {
        // Act & Assert
        // 注意：项目架构采用统一返回 HTTP 200，通过响应体中的 code 字段区分业务状态
        mockMvc.perform(get("/doctor/99999"))
                .andExpect(status().isOk())  // 架构设计：统一返回 HTTP 200
                .andExpect(jsonPath("$.code").value(404))  // 业务状态码在响应体中
                .andExpect(jsonPath("$.message").exists());
    }
}
