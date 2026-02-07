package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.mapper.DiseaseMapper;
import com.chen.HospitalSelection.model.Disease;
import com.chen.HospitalSelection.vo.DiseaseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 疾病分类服务测试类
 *
 * @author chen
 */
public class DiseaseServiceTest {

    @Mock
    private DiseaseMapper diseaseMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.DiseaseServiceImpl diseaseService;

    private Disease testDisease;
    private Disease testChildDisease;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建一级测试疾病
        testDisease = new Disease();
        testDisease.setId(1L);
        testDisease.setParentId(0L);
        testDisease.setDiseaseName("心血管疾病");
        testDisease.setDiseaseCode("cardiovascular");
        testDisease.setSort(100);
        testDisease.setIsDeleted(0);

        // 创建二级测试疾病
        testChildDisease = new Disease();
        testChildDisease.setId(11L);
        testChildDisease.setParentId(1L);
        testChildDisease.setDiseaseName("高血压");
        testChildDisease.setDiseaseCode("hypertension");
        testChildDisease.setSort(50);
        testChildDisease.setIsDeleted(0);
    }

    @Test
    @DisplayName("测试获取所有疾病分类树 - 成功")
    public void testGetDiseaseTree_Success() {
        // Arrange
        when(diseaseMapper.selectLevel1()).thenReturn(Arrays.asList(testDisease));
        when(diseaseMapper.selectByParentId(1L)).thenReturn(Arrays.asList(testChildDisease));

        // Act
        List<DiseaseVO> result = diseaseService.getDiseaseTree();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("心血管疾病", result.get(0).getDiseaseName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("高血压", result.get(0).getChildren().get(0).getDiseaseName());

        verify(diseaseMapper, times(1)).selectLevel1();
        verify(diseaseMapper, times(1)).selectByParentId(1L);
    }

    @Test
    @DisplayName("测试获取一级分类列表 - 成功")
    public void testGetLevel1Diseases_Success() {
        // Arrange
        Disease disease2 = new Disease();
        disease2.setId(2L);
        disease2.setParentId(0L);
        disease2.setDiseaseName("内分泌疾病");
        disease2.setDiseaseCode("endocrine");

        List<Disease> diseaseList = Arrays.asList(testDisease, disease2);
        when(diseaseMapper.selectLevel1()).thenReturn(diseaseList);

        // Act
        List<DiseaseVO> result = diseaseService.getLevel1Diseases();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("心血管疾病", result.get(0).getDiseaseName());
        assertEquals("内分泌疾病", result.get(1).getDiseaseName());

        verify(diseaseMapper, times(1)).selectLevel1();
    }

    @Test
    @DisplayName("测试获取二级分类列表 - 成功")
    public void testGetLevel2Diseases_Success() {
        // Arrange
        Disease child2 = new Disease();
        child2.setId(12L);
        child2.setParentId(1L);
        child2.setDiseaseName("冠心病");
        child2.setDiseaseCode("coronary");

        List<Disease> diseaseList = Arrays.asList(testChildDisease, child2);
        when(diseaseMapper.selectByParentId(1L)).thenReturn(diseaseList);

        // Act
        List<DiseaseVO> result = diseaseService.getLevel2Diseases(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("高血压", result.get(0).getDiseaseName());
        assertEquals("冠心病", result.get(1).getDiseaseName());

        verify(diseaseMapper, times(1)).selectByParentId(1L);
    }

    @Test
    @DisplayName("测试根据编码查询疾病 - 成功")
    public void testGetDiseaseByCode_Success() {
        // Arrange
        when(diseaseMapper.selectByCode("hypertension")).thenReturn(testChildDisease);

        // Act
        DiseaseVO result = diseaseService.getDiseaseByCode("hypertension");

        // Assert
        assertNotNull(result);
        assertEquals("高血压", result.getDiseaseName());
        assertEquals("hypertension", result.getDiseaseCode());

        verify(diseaseMapper, times(1)).selectByCode("hypertension");
    }

    @Test
    @DisplayName("测试搜索疾病 - 成功")
    public void testSearchDiseasesByName_Success() {
        // Arrange
        Disease disease2 = new Disease();
        disease2.setId(12L);
        disease2.setParentId(1L);
        disease2.setDiseaseName("冠心病");
        disease2.setDiseaseCode("coronary");

        when(diseaseMapper.searchByName("高压")).thenReturn(Arrays.asList(testChildDisease));

        // Act
        List<DiseaseVO> result = diseaseService.searchDiseasesByName("高压");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("高血压", result.get(0).getDiseaseName());

        verify(diseaseMapper, times(1)).searchByName("高压");
    }

    @Test
    @DisplayName("测试获取疾病分类树 - 空列表")
    public void testGetDiseaseTree_Empty() {
        // Arrange
        when(diseaseMapper.selectLevel1()).thenReturn(Collections.emptyList());

        // Act
        List<DiseaseVO> result = diseaseService.getDiseaseTree();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试获取疾病路径 - 成功")
    public void testGetDiseasePath_Success() {
        // Arrange
        when(diseaseMapper.selectById(11L)).thenReturn(testChildDisease);
        when(diseaseMapper.selectById(1L)).thenReturn(testDisease);

        // Act
        String result = diseaseService.getDiseasePath(11L);

        // Assert
        assertNotNull(result);
        assertEquals("心血管疾病 > 高血压", result);
    }
}
