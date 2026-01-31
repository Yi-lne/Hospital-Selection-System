package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 医院服务测试类
 * 测试医院查询、筛选、搜索等功能
 *
 * @author chen
 */
public class HospitalServiceTest {

    @Mock
    private HospitalMapper hospitalMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.HospitalServiceImpl hospitalService;

    private Hospital testHospital;
    private static final Long TEST_HOSPITAL_ID = 1L;
    private static final String TEST_HOSPITAL_NAME = "广东省人民医院";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setId(TEST_HOSPITAL_ID);
        testHospital.setHospitalName(TEST_HOSPITAL_NAME);
        testHospital.setHospitalLevel("grade3A");
        testHospital.setProvinceCode("440000");
        testHospital.setCityCode("440100");
        testHospital.setAreaCode("440103");
        testHospital.setAddress("广州市越秀区中山二路");
        testHospital.setPhone("020-83827812");
        testHospital.setKeyDepartments("心内科,心血管外科");
        testHospital.setMedicalEquipment("CT,MRI");
        testHospital.setExpertTeam("张三医生,李四医生");
        testHospital.setIntro("广东省人民医院是一家综合性三级甲等医院");
        testHospital.setRating(new BigDecimal("4.8"));
        testHospital.setReviewCount(2563);
        testHospital.setIsMedicalInsurance(1);
        testHospital.setIsDeleted(0);
        testHospital.setCreateTime(LocalDateTime.now());
        testHospital.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试分页查询医院列表 - 成功")
    public void testGetHospitalList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setHospitalName("广州市第一人民医院");

        List<Hospital> hospitalList = Arrays.asList(testHospital, hospital2);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.getHospitalList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getList().size());
        assertEquals(TEST_HOSPITAL_NAME, result.getList().get(0).getHospitalName());

        verify(hospitalMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试分页查询医院列表 - 空列表")
    public void testGetHospitalList_Empty() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(hospitalMapper.selectAll()).thenReturn(Collections.emptyList());

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.getHospitalList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());

        verify(hospitalMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试多条件筛选医院 - 成功")
    public void testFilterHospitals_Success() {
        // Arrange
        HospitalFilterDTO dto = new HospitalFilterDTO();
        dto.setHospitalLevel("grade3A");
        dto.setCityCode("440100");
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.filterHospitals(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("grade3A", result.getList().get(0).getHospitalLevel());

        verify(hospitalMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试多条件筛选医院 - 等级筛选")
    public void testFilterHospitals_ByLevel() {
        // Arrange
        HospitalFilterDTO dto = new HospitalFilterDTO();
        dto.setHospitalLevel("grade2A");
        dto.setPage(1);
        dto.setPageSize(10);

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setHospitalName("广州市第二人民医院");
        hospital2.setHospitalLevel("grade2A");
        hospital2.setCityCode("440100");

        List<Hospital> hospitalList = Arrays.asList(testHospital, hospital2);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.filterHospitals(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals("grade2A", result.getList().get(0).getHospitalLevel());
    }

    @Test
    @DisplayName("测试多条件筛选医院 - 医保定点筛选")
    public void testFilterHospitals_ByInsurance() {
        // Arrange
        HospitalFilterDTO dto = new HospitalFilterDTO();
        dto.setIsMedicalInsurance(1);
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.filterHospitals(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(Integer.valueOf(1), result.getList().get(0).getIsMedicalInsurance());
    }

    @Test
    @DisplayName("测试获取医院详情 - 成功")
    public void testGetHospitalDetail_Success() {
        // Arrange
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);

        // Act
        HospitalVO result = hospitalService.getHospitalDetail(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_HOSPITAL_ID, result.getId());
        assertEquals(TEST_HOSPITAL_NAME, result.getHospitalName());
        assertEquals("grade3A", result.getHospitalLevel());
        assertEquals("广州市越秀区中山二路", result.getAddress());

        verify(hospitalMapper, times(1)).selectById(TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试获取医院详情 - 医院不存在")
    public void testGetHospitalDetail_NotFound() {
        // Arrange
        when(hospitalMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            hospitalService.getHospitalDetail(999L);
        });

        assertEquals("医院不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试获取医院科室列表 - 成功")
    public void testGetHospitalDepartments_Success() {
        // Arrange
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setHospitalId(TEST_HOSPITAL_ID);
        dept1.setDeptName("心内科");
        dept1.setDeptIntro("心血管疾病诊疗科室");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setHospitalId(TEST_HOSPITAL_ID);
        dept2.setDeptName("心血管外科");
        dept2.setDeptIntro("心血管外科诊疗科室");

        List<Department> departmentList = Arrays.asList(dept1, dept2);
        when(departmentMapper.selectByHospitalId(TEST_HOSPITAL_ID)).thenReturn(departmentList);

        // Act
        List<DepartmentVO> result = hospitalService.getHospitalDepartments(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("心内科", result.get(0).getDeptName());
        assertEquals("心血管外科", result.get(1).getDeptName());

        verify(departmentMapper, times(1)).selectByHospitalId(TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试获取医院医生列表 - 成功")
    public void testGetHospitalDoctors_Success() {
        // Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setHospitalId(TEST_HOSPITAL_ID);
        doctor1.setDoctorName("张三");
        doctor1.setTitle("主任医师");
        doctor1.setDeptId(1L);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setHospitalId(TEST_HOSPITAL_ID);
        doctor2.setDoctorName("李四");
        doctor2.setTitle("副主任医师");
        doctor2.setDeptId(1L);

        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);
        when(doctorMapper.selectByHospitalId(TEST_HOSPITAL_ID)).thenReturn(doctorList);

        // Act
        List<DoctorSimpleVO> result = hospitalService.getHospitalDoctors(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getDoctorName());
        assertEquals("李四", result.get(1).getDoctorName());

        verify(doctorMapper, times(1)).selectByHospitalId(TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试搜索医院 - 成功")
    public void testSearchHospitals_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital);
        when(hospitalMapper.searchByKeyword("人民医院")).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.searchHospitals("人民医院", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(TEST_HOSPITAL_NAME, result.getList().get(0).getHospitalName());

        verify(hospitalMapper, times(1)).searchByKeyword("人民医院");
    }

    @Test
    @DisplayName("测试获取搜索建议 - 成功")
    public void testGetSearchSuggestions_Success() {
        // Arrange
        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setHospitalName("广州市第一人民医院");

        List<Hospital> hospitalList = Arrays.asList(testHospital, hospital2);
        when(hospitalMapper.searchByKeyword("人民")).thenReturn(hospitalList);

        // Act
        List<String> result = hospitalService.getSearchSuggestions("人民");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(TEST_HOSPITAL_NAME));

        verify(hospitalMapper, times(1)).searchByKeyword("人民");
    }

    @Test
    @DisplayName("测试按城市查询医院 - 成功")
    public void testGetHospitalsByCity_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.getHospitalsByCity("440100", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        // 验证医院存在即可，不验证具体字段
        assertTrue(result.getList().get(0).getHospitalName().contains("人民医院"));
    }

    @Test
    @DisplayName("测试按等级查询医院 - 成功")
    public void testGetHospitalsByLevel_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital);
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.getHospitalsByLevel("grade3A", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals("grade3A", result.getList().get(0).getHospitalLevel());
    }

    @Test
    @DisplayName("测试多条件筛选 - 无结果")
    public void testFilterHospitals_NoResults() {
        // Arrange
        HospitalFilterDTO dto = new HospitalFilterDTO();
        dto.setHospitalLevel("grade1A"); // 一级医院
        dto.setPage(1);
        dto.setPageSize(10);

        List<Hospital> hospitalList = Arrays.asList(testHospital); // 三甲医院
        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.filterHospitals(dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试分页查询 - 第二页")
    public void testGetHospitalList_SecondPage() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(2);
        dto.setPageSize(5);

        // 创建15个医院
        java.util.List<Hospital> hospitalList = new java.util.ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Hospital h = new Hospital();
            h.setId((long) i);
            h.setHospitalName("医院" + i);
            hospitalList.add(h);
        }

        when(hospitalMapper.selectAll()).thenReturn(hospitalList);

        // Act
        PageResult<HospitalSimpleVO> result = hospitalService.getHospitalList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(15L, result.getTotal());
        assertEquals(2, result.getPageNum());
        assertEquals(5, result.getList().size());
        assertEquals("医院6", result.getList().get(0).getHospitalName());
    }
}
