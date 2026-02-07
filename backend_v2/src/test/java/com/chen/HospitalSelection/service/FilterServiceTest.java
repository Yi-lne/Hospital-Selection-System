package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 筛选服务测试类
 * 测试多条件筛选核心功能
 *
 * @author chen
 */
@DisplayName("筛选服务测试")
public class FilterServiceTest {

    @Mock
    private HospitalService hospitalService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.FilterServiceImpl filterService;

    private PageResult<HospitalSimpleVO> mockHospitalResult;
    private PageResult<HospitalSimpleVO> emptyHospitalResult;
    private HospitalFilterDTO testFilterDTO;
    private DoctorFilterDTO testDoctorFilterDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建模拟的医院筛选结果
        mockHospitalResult = new PageResult<>();
        mockHospitalResult.setTotal(10L);
        mockHospitalResult.setPageNum(1);
        mockHospitalResult.setPageSize(20);
        mockHospitalResult.setPages(1);

        HospitalSimpleVO hospital1 = new HospitalSimpleVO();
        hospital1.setId(1L);
        hospital1.setHospitalName("广东省人民医院");
        hospital1.setHospitalLevel("grade3A");
        hospital1.setRating(new BigDecimal("4.8"));

        HospitalSimpleVO hospital2 = new HospitalSimpleVO();
        hospital2.setId(2L);
        hospital2.setHospitalName("中山大学附属第一医院");
        hospital2.setHospitalLevel("grade3A");
        hospital2.setRating(new BigDecimal("4.9"));

        mockHospitalResult.setList(Arrays.asList(hospital1, hospital2));

        // 创建空的筛选结果
        emptyHospitalResult = new PageResult<>();
        emptyHospitalResult.setTotal(0L);
        emptyHospitalResult.setPageNum(1);
        emptyHospitalResult.setPageSize(20);
        emptyHospitalResult.setPages(0);
        emptyHospitalResult.setList(Arrays.asList());

        // 创建测试筛选条件
        testFilterDTO = new HospitalFilterDTO();
        testFilterDTO.setDiseaseCode("cardiovascular");
        testFilterDTO.setHospitalLevel("grade3A");
        testFilterDTO.setProvinceCode("440000");
        testFilterDTO.setCityCode("440100");
        testFilterDTO.setPage(1);
        testFilterDTO.setPageSize(20);

        testDoctorFilterDTO = new DoctorFilterDTO();
        testDoctorFilterDTO.setHospitalId(1L);
        testDoctorFilterDTO.setDeptId(1L);
        testDoctorFilterDTO.setTitle("主任医师");
        testDoctorFilterDTO.setPage(1);
        testDoctorFilterDTO.setPageSize(10);
    }

    @Test
    @DisplayName("测试多条件筛选医院 - 成功")
    public void testFilterHospitals_Success() {
        // Arrange
        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(mockHospitalResult);

        // Act
        PageResult<HospitalSimpleVO> result = filterService.filterHospitals(testFilterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(20, result.getPageSize());
        assertEquals(2, result.getList().size());
        assertEquals("广东省人民医院", result.getList().get(0).getHospitalName());

        verify(hospitalService, times(1)).filterHospitals(any(HospitalFilterDTO.class));
    }

    @Test
    @DisplayName("测试多条件筛选医院 - 无结果")
    public void testFilterHospitals_NoResults() {
        // Arrange
        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(emptyHospitalResult);

        // Act
        PageResult<HospitalSimpleVO> result = filterService.filterHospitals(testFilterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());

        verify(hospitalService, times(1)).filterHospitals(any(HospitalFilterDTO.class));
    }

    @Test
    @DisplayName("测试多条件筛选医生 - 成功")
    public void testFilterDoctors_Success() {
        // Arrange
        PageResult<com.chen.HospitalSelection.vo.DoctorSimpleVO> mockDoctorResult = new PageResult<>();
        mockDoctorResult.setTotal(5L);
        mockDoctorResult.setPageNum(1);
        mockDoctorResult.setPageSize(10);
        mockDoctorResult.setPages(1);
        mockDoctorResult.setList(Arrays.asList());

        when(doctorService.filterDoctors(any(DoctorFilterDTO.class))).thenReturn(mockDoctorResult);

        // Act
        PageResult<com.chen.HospitalSelection.vo.DoctorSimpleVO> result = filterService.filterDoctors(testDoctorFilterDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getTotal());

        verify(doctorService, times(1)).filterDoctors(any(DoctorFilterDTO.class));
    }

    @Test
    @DisplayName("测试获取筛选统计数据 - 成功")
    public void testGetFilterStats_Success() {
        // Arrange
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("hospitalLevel", "grade3A");
        filterMap.put("cityCode", "440100");

        // Act
        Map<String, Long> result = filterService.getFilterStats(filterMap);

        // Assert
        assertNotNull(result);
        // 当前实现返回空Map（有TODO）
        assertTrue(result.isEmpty() || result.size() == 0);

        // 注意：由于getFilterStats有TODO，当前只是验证方法可以正常调用
        // 实际功能实现后需要更新此测试
    }

    @Test
    @DisplayName("测试根据疾病推荐医院 - 成功")
    public void testRecommendHospitalsByDisease_Success() {
        // Arrange
        String diseaseCode = "hypertension";
        Integer pageNum = 1;
        Integer pageSize = 10;

        // 修改模拟数据，使其pageSize与传入参数一致
        PageResult<HospitalSimpleVO> mockHospitalResultForDisease = new PageResult<>();
        mockHospitalResultForDisease.setTotal(10L);
        mockHospitalResultForDisease.setPageNum(1);
        mockHospitalResultForDisease.setPageSize(pageSize); // 与传入的pageSize一致
        mockHospitalResultForDisease.setPages(1);

        HospitalSimpleVO hospital1 = new HospitalSimpleVO();
        hospital1.setId(1L);
        hospital1.setHospitalName("广东省人民医院");
        hospital1.setHospitalLevel("grade3A");
        hospital1.setRating(new BigDecimal("4.8"));

        HospitalSimpleVO hospital2 = new HospitalSimpleVO();
        hospital2.setId(2L);
        hospital2.setHospitalName("中山大学附属第一医院");
        hospital2.setHospitalLevel("grade3A");
        hospital2.setRating(new BigDecimal("4.9"));

        mockHospitalResultForDisease.setList(Arrays.asList(hospital1, hospital2));

        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(mockHospitalResultForDisease);

        // Act
        PageResult<HospitalSimpleVO> result = filterService.recommendHospitalsByDisease(diseaseCode, pageNum, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize()); // 现在应该等于传入的pageSize

        // 验证调用了filterHospitals，并验证参数设置
        verify(hospitalService, times(1)).filterHospitals(any(HospitalFilterDTO.class));
    }

    @Test
    @DisplayName("测试根据疾病推荐医院 - 空结果")
    public void testRecommendHospitalsByDisease_NoResults() {
        // Arrange
        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(emptyHospitalResult);

        // Act
        PageResult<HospitalSimpleVO> result = filterService.recommendHospitalsByDisease("diabetes", 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试计算医院匹配度 - 成功")
    public void testCalculateMatchScore_Success() {
        // Arrange
        Long hospitalId = 1L;

        // Act
        Integer result = filterService.calculateMatchScore(hospitalId, testFilterDTO);

        // Assert
        assertNotNull(result);
        // 当前实现返回固定分数75（TODO状态）
        assertEquals(75, result.intValue());
    }

    @Test
    @DisplayName("测试计算医院匹配度 - 不同医院")
    public void testCalculateMatchScore_DifferentHospitals() {
        // Arrange
        Long hospitalId1 = 1L;
        Long hospitalId2 = 2L;

        // Act
        Integer result1 = filterService.calculateMatchScore(hospitalId1, testFilterDTO);
        Integer result2 = filterService.calculateMatchScore(hospitalId2, testFilterDTO);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        // 当前实现返回相同分数（TODO状态）
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("测试多条件筛选 - 空筛选条件")
    public void testFilterHospitals_EmptyFilters() {
        // Arrange
        HospitalFilterDTO emptyDTO = new HospitalFilterDTO();
        emptyDTO.setPage(1);
        emptyDTO.setPageSize(20);

        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(mockHospitalResult);

        // Act
        PageResult<HospitalSimpleVO> result = filterService.filterHospitals(emptyDTO);

        // Assert
        assertNotNull(result);
        verify(hospitalService, times(1)).filterHospitals(any(HospitalFilterDTO.class));
    }

    @Test
    @DisplayName("测试医生筛选 - 空筛选条件")
    public void testFilterDoctors_EmptyFilters() {
        // Arrange
        DoctorFilterDTO emptyDTO = new DoctorFilterDTO();
        emptyDTO.setPage(1);
        emptyDTO.setPageSize(10);

        PageResult<com.chen.HospitalSelection.vo.DoctorSimpleVO> mockResult = new PageResult<>();
        mockResult.setTotal(0L);
        mockResult.setPageNum(1);
        mockResult.setPageSize(10);
        mockResult.setList(Arrays.asList());

        when(doctorService.filterDoctors(any(DoctorFilterDTO.class))).thenReturn(mockResult);

        // Act
        PageResult<com.chen.HospitalSelection.vo.DoctorSimpleVO> result = filterService.filterDoctors(emptyDTO);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());

        verify(doctorService, times(1)).filterDoctors(any(DoctorFilterDTO.class));
    }

    @Test
    @DisplayName("测试获取筛选统计 - 空条件")
    public void testGetFilterStats_EmptyFilters() {
        // Arrange
        Map<String, Object> emptyFilterMap = new HashMap<>();

        // Act
        Map<String, Long> result = filterService.getFilterStats(emptyFilterMap);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试推荐医院 - 不同疾病编码")
    public void testRecommendHospitalsByDisease_DifferentDiseases() {
        // Arrange
        when(hospitalService.filterHospitals(any(HospitalFilterDTO.class))).thenReturn(mockHospitalResult);

        // Act
        PageResult<HospitalSimpleVO> result1 = filterService.recommendHospitalsByDisease("hypertension", 1, 10);
        PageResult<HospitalSimpleVO> result2 = filterService.recommendHospitalsByDisease("diabetes", 1, 10);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        // 两次调用都应该成功

        verify(hospitalService, times(2)).filterHospitals(any(HospitalFilterDTO.class));
    }

    @Test
    @DisplayName("测试计算匹配度 - 不同筛选条件")
    public void testCalculateMatchScore_DifferentFilters() {
        // Arrange
        HospitalFilterDTO filterDTO1 = new HospitalFilterDTO();
        filterDTO1.setHospitalLevel("grade3A");
        filterDTO1.setIsMedicalInsurance(1);

        HospitalFilterDTO filterDTO2 = new HospitalFilterDTO();
        filterDTO2.setHospitalLevel("grade2A");
        filterDTO2.setIsMedicalInsurance(0);

        // Act
        Integer result1 = filterService.calculateMatchScore(1L, filterDTO1);
        Integer result2 = filterService.calculateMatchScore(1L, filterDTO2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        // 当前实现返回相同分数（TODO状态）
        assertEquals(result1, result2);
    }
}
