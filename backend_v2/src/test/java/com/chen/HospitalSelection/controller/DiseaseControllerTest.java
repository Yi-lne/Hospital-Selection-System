package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.DiseaseService;
import com.chen.HospitalSelection.vo.DiseaseVO;
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
 * 疾病分类控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DiseaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiseaseService diseaseService;

    private DiseaseVO level1Disease;
    private DiseaseVO level2Disease1;
    private DiseaseVO level2Disease2;

    @BeforeEach
    public void setUp() {
        // 一级分类：心血管疾病
        level1Disease = new DiseaseVO();
        level1Disease.setId(1L);
        level1Disease.setParentId(0L);
        level1Disease.setDiseaseName("心血管疾病");
        level1Disease.setDiseaseCode("cardiovascular");
        level1Disease.setSort(100);

        // 二级分类：高血压
        level2Disease1 = new DiseaseVO();
        level2Disease1.setId(11L);
        level2Disease1.setParentId(1L);
        level2Disease1.setDiseaseName("高血压");
        level2Disease1.setDiseaseCode("hypertension");
        level2Disease1.setSort(10);

        // 二级分类：冠心病
        level2Disease2 = new DiseaseVO();
        level2Disease2.setId(12L);
        level2Disease2.setParentId(1L);
        level2Disease2.setDiseaseName("冠心病");
        level2Disease2.setDiseaseCode("coronary");
        level2Disease2.setSort(9);
    }

    @Test
    @DisplayName("测试疾病分类树 - 成功")
    public void testGetDiseaseTree_Success() throws Exception {
        // Arrange
        level1Disease.setChildren(Arrays.asList(level2Disease1, level2Disease2));

        when(diseaseService.getDiseaseTree()).thenReturn(Arrays.asList(level1Disease));

        // Act & Assert
        mockMvc.perform(get("/disease/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].diseaseName").value("心血管疾病"))
                .andExpect(jsonPath("$.data[0].children[0].diseaseName").value("高血压"))
                .andExpect(jsonPath("$.data[0].children[1].diseaseName").value("冠心病"));
    }

    @Test
    @DisplayName("测试疾病分类树 - 空结果")
    public void testGetDiseaseTree_Empty() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseTree()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/disease/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试一级分类列表 - 成功")
    public void testGetLevel1Diseases_Success() throws Exception {
        // Arrange
        DiseaseVO level2 = new DiseaseVO();
        level2.setId(2L);
        level2.setParentId(0L);
        level2.setDiseaseName("内分泌疾病");
        level2.setDiseaseCode("endocrine");
        level2.setSort(90);

        when(diseaseService.getLevel1Diseases()).thenReturn(Arrays.asList(level1Disease, level2));

        // Act & Assert
        mockMvc.perform(get("/disease/level1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].diseaseName").value("心血管疾病"))
                .andExpect(jsonPath("$.data[1].diseaseName").value("内分泌疾病"));
    }

    @Test
    @DisplayName("测试一级分类列表 - 空结果")
    public void testGetLevel1Diseases_Empty() throws Exception {
        // Arrange
        when(diseaseService.getLevel1Diseases()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/disease/level1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试二级分类列表 - 成功")
    public void testGetLevel2Diseases_Success() throws Exception {
        // Arrange
        when(diseaseService.getLevel2Diseases(1L)).thenReturn(Arrays.asList(level2Disease1, level2Disease2));

        // Act & Assert
        mockMvc.perform(get("/disease/level2/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].diseaseName").value("高血压"))
                .andExpect(jsonPath("$.data[1].diseaseName").value("冠心病"));
    }

    @Test
    @DisplayName("测试二级分类列表 - 空结果")
    public void testGetLevel2Diseases_Empty() throws Exception {
        // Arrange
        when(diseaseService.getLevel2Diseases(anyLong())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/disease/level2/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
