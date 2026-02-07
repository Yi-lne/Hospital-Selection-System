package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.HospitalService;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 医院控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HospitalService hospitalService;

    private HospitalSimpleVO testHospitalSimpleVO;
    private HospitalVO testHospitalVO;
    private PageResult<HospitalSimpleVO> testPageResult;

    @BeforeEach
    public void setUp() {
        testHospitalSimpleVO = new HospitalSimpleVO();
        testHospitalSimpleVO.setId(1L);
        testHospitalSimpleVO.setHospitalName("广东省人民医院");
        testHospitalSimpleVO.setHospitalLevel("grade3A");
        testHospitalSimpleVO.setAddress("广州市越秀区中山二路");
        testHospitalSimpleVO.setRating(new BigDecimal("4.8"));
        testHospitalSimpleVO.setReviewCount(2563);

        testHospitalVO = new HospitalVO();
        testHospitalVO.setId(1L);
        testHospitalVO.setHospitalName("广东省人民医院");
        testHospitalVO.setHospitalLevel("grade3A");
        testHospitalVO.setIntro("广东省人民医院是一家综合性三级甲等医院");

        testPageResult = new PageResult<>(1L, 1, 10, Arrays.asList(testHospitalSimpleVO));
    }

    @Test
    @DisplayName("测试医院列表 - 成功")
    public void testGetHospitalList_Success() throws Exception {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(hospitalService.getHospitalList(any(PageQueryDTO.class))).thenReturn(testPageResult);

        // Act & Assert
        mockMvc.perform(get("/hospital/list")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].hospitalName").value("广东省人民医院"));
    }

    @Test
    @DisplayName("测试医院筛选 - 成功")
    public void testFilterHospitals_Success() throws Exception {
        // Arrange
        HospitalFilterDTO dto = new HospitalFilterDTO();
        dto.setHospitalLevel("grade3A");
        dto.setCityCode("440100");
        dto.setPage(1);
        dto.setPageSize(10);

        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(testPageResult);

        // Act & Assert
        mockMvc.perform(post("/hospital/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("测试医院详情 - 成功")
    public void testGetHospitalDetail_Success() throws Exception {
        // Arrange
        when(hospitalService.getHospitalDetail(1L)).thenReturn(testHospitalVO);

        // Act & Assert
        mockMvc.perform(get("/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.hospitalName").value("广东省人民医院"))
                .andExpect(jsonPath("$.data.hospitalLevel").value("grade3A"));
    }

    @Test
    @DisplayName("测试医院科室列表 - 成功")
    public void testGetHospitalDepartments_Success() throws Exception {
        // Arrange
        com.chen.HospitalSelection.vo.DepartmentVO deptVO = new com.chen.HospitalSelection.vo.DepartmentVO();
        deptVO.setId(1L);
        deptVO.setDeptName("心内科");

        when(hospitalService.getHospitalDepartments(1L)).thenReturn(Arrays.asList(deptVO));

        // Act & Assert
        mockMvc.perform(get("/hospital/1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].deptName").value("心内科"));
    }

    @Test
    @DisplayName("测试医院医生列表 - 成功")
    public void testGetHospitalDoctors_Success() throws Exception {
        // Arrange
        com.chen.HospitalSelection.vo.DoctorSimpleVO doctorVO = new com.chen.HospitalSelection.vo.DoctorSimpleVO();
        doctorVO.setId(1L);
        doctorVO.setDoctorName("张三");

        when(hospitalService.getHospitalDoctors(1L)).thenReturn(Arrays.asList(doctorVO));

        // Act & Assert
        mockMvc.perform(get("/hospital/1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].doctorName").value("张三"));
    }

    @Test
    @DisplayName("测试医院搜索建议 - 成功")
    public void testSearchSuggest_Success() throws Exception {
        // Arrange
        when(hospitalService.getSearchSuggestions("人民")).thenReturn(Arrays.asList("广东省人民医院", "广州市第一人民医院"));

        // Act & Assert
        mockMvc.perform(get("/hospital/search/suggest")
                        .param("keyword", "人民"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0]").value("广东省人民医院"));
    }

    @Test
    @DisplayName("测试医院列表 - 空结果")
    public void testGetHospitalList_Empty() throws Exception {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        PageResult<HospitalSimpleVO> emptyResult = new PageResult<>(0L, 1, 10, Collections.emptyList());
        when(hospitalService.getHospitalList(any(PageQueryDTO.class))).thenReturn(emptyResult);

        // Act & Assert
        mockMvc.perform(get("/hospital/list")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }
}