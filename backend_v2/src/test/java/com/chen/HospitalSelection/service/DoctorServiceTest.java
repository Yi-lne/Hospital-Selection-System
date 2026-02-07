package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * 医生服务测试类
 * 测试医生查询、筛选、详情等功能
 *
 * @author chen
 */
public class DoctorServiceTest {

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private HospitalMapper hospitalMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.DoctorServiceImpl doctorService;

    private Doctor testDoctor;
    private Doctor testDoctor2; // Different hospital
    private Doctor testDoctor3; // Different department
    private Hospital testHospital;
    private Department testDepartment;
    private static final Long TEST_DOCTOR_ID = 1L;
    private static final Long TEST_HOSPITAL_ID = 1L;
    private static final Long TEST_HOSPITAL_ID_2 = 2L;
    private static final Long TEST_DEPT_ID = 1L;
    private static final Long TEST_DEPT_ID_2 = 2L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setId(TEST_HOSPITAL_ID);
        testHospital.setHospitalName("广东省人民医院");
        testHospital.setHospitalLevel("grade3A");

        // 创建测试科室
        testDepartment = new Department();
        testDepartment.setId(TEST_DEPT_ID);
        testDepartment.setHospitalId(TEST_HOSPITAL_ID);
        testDepartment.setDeptName("心内科");

        // 创建测试医生
        testDoctor = new Doctor();
        testDoctor.setId(TEST_DOCTOR_ID);
        testDoctor.setDoctorName("张三");
        testDoctor.setHospitalId(TEST_HOSPITAL_ID);
        testDoctor.setDeptId(TEST_DEPT_ID);
        testDoctor.setTitle("主任医师");
        testDoctor.setSpecialty("冠心病、高血压、心律失常的诊治");
        testDoctor.setAcademicBackground("医学博士，博士生导师");
        testDoctor.setScheduleTime("周一上午、周三下午");
        testDoctor.setConsultationFee(new BigDecimal("100.00"));
        testDoctor.setRating(new BigDecimal("4.8"));
        testDoctor.setReviewCount(156);
        testDoctor.setIsDeleted(0);
        testDoctor.setCreateTime(LocalDateTime.now());
        testDoctor.setUpdateTime(LocalDateTime.now());

        // 创建不同医院的医生
        testDoctor2 = new Doctor();
        testDoctor2.setId(2L);
        testDoctor2.setDoctorName("李四");
        testDoctor2.setHospitalId(TEST_HOSPITAL_ID_2);
        testDoctor2.setDeptId(TEST_DEPT_ID);
        testDoctor2.setTitle("副主任医师");

        // 创建不同科室的医生
        testDoctor3 = new Doctor();
        testDoctor3.setId(3L);
        testDoctor3.setDoctorName("王五");
        testDoctor3.setHospitalId(TEST_HOSPITAL_ID);
        testDoctor3.setDeptId(TEST_DEPT_ID_2);
        testDoctor3.setTitle("主任医师");
    }

    @Test
    @DisplayName("测试分页查询医生列表 - 成功")
    public void testGetDoctorList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDoctorName("李四");

        List<Doctor> doctorList = Arrays.asList(testDoctor, doctor2);
        when(doctorMapper.selectAll()).thenReturn(doctorList);

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.getDoctorList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getList().size());
        assertEquals("张三", result.getList().get(0).getDoctorName());

        verify(doctorMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试分页查询医生列表 - 空列表")
    public void testGetDoctorList_Empty() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(doctorMapper.selectAll()).thenReturn(Collections.emptyList());

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.getDoctorList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试获取医生详情 - 成功")
    public void testGetDoctorDetail_Success() {
        // Arrange
        // 注意：实际实现使用selectWithDetailsById，所以应该mock这个方法
        when(doctorMapper.selectWithDetailsById(TEST_DOCTOR_ID)).thenReturn(testDoctor);

        // Act
        DoctorVO result = doctorService.getDoctorDetail(TEST_DOCTOR_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_DOCTOR_ID, result.getId());
        assertEquals("张三", result.getDoctorName());
        assertEquals("主任医师", result.getTitle());
        assertEquals("冠心病、高血压、心律失常的诊治", result.getSpecialty());

        verify(doctorMapper, times(1)).selectWithDetailsById(TEST_DOCTOR_ID);
        // 不验证hospitalMapper和departmentMapper，因为实际实现没有调用
    }

    @Test
    @DisplayName("测试获取医生详情 - 医生不存在")
    public void testGetDoctorDetail_NotFound() {
        // Arrange
        when(doctorMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            doctorService.getDoctorDetail(999L);
        });

        assertEquals("医生不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试筛选医生 - 按医院筛选")
    public void testFilterDoctors_ByHospital() {
        // Arrange
        DoctorFilterDTO dto = new DoctorFilterDTO();
        dto.setHospitalId(TEST_HOSPITAL_ID);
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectBySimpleCondition然后数据库过滤
        when(doctorMapper.selectBySimpleCondition(eq(TEST_HOSPITAL_ID), isNull(), isNull()))
            .thenReturn(Arrays.asList(testDoctor, testDoctor3)); // 两个医生属于TEST_HOSPITAL_ID

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.filterDoctors(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // testDoctor和testDoctor3属于TEST_HOSPITAL_ID

        verify(doctorMapper, times(1)).selectBySimpleCondition(eq(TEST_HOSPITAL_ID), isNull(), isNull());
    }

    @Test
    @DisplayName("测试筛选医生 - 按科室筛选")
    public void testFilterDoctors_ByDepartment() {
        // Arrange
        DoctorFilterDTO dto = new DoctorFilterDTO();
        dto.setDeptId(TEST_DEPT_ID);
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectBySimpleCondition然后数据库过滤
        when(doctorMapper.selectBySimpleCondition(isNull(), eq(TEST_DEPT_ID), isNull()))
            .thenReturn(Arrays.asList(testDoctor, testDoctor2)); // testDoctor和testDoctor2属于TEST_DEPT_ID

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.filterDoctors(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // testDoctor和testDoctor2属于TEST_DEPT_ID

        verify(doctorMapper, times(1)).selectBySimpleCondition(isNull(), eq(TEST_DEPT_ID), isNull());
    }

    @Test
    @DisplayName("测试筛选医生 - 按职称筛选")
    public void testFilterDoctors_ByTitle() {
        // Arrange
        DoctorFilterDTO dto = new DoctorFilterDTO();
        dto.setTitle("主任医师");
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectBySimpleCondition然后数据库过滤
        when(doctorMapper.selectBySimpleCondition(isNull(), isNull(), eq("主任医师")))
            .thenReturn(Arrays.asList(testDoctor, testDoctor3)); // testDoctor和testDoctor3是主任医师

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.filterDoctors(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // testDoctor和testDoctor3是主任医师

        verify(doctorMapper, times(1)).selectBySimpleCondition(isNull(), isNull(), eq("主任医师"));
    }

    @Test
    @DisplayName("测试按科室查询医生 - 成功")
    public void testGetDoctorsByDepartment_Success() {
        // Arrange
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDoctorName("李四");
        doctor2.setDeptId(TEST_DEPT_ID);

        List<Doctor> doctorList = Arrays.asList(testDoctor, doctor2);
        when(doctorMapper.selectByDeptId(TEST_DEPT_ID)).thenReturn(doctorList);

        // Act
        List<DoctorSimpleVO> result = doctorService.getDoctorsByDepartment(TEST_DEPT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getDoctorName());

        verify(doctorMapper, times(1)).selectByDeptId(TEST_DEPT_ID);
    }

    @Test
    @DisplayName("测试按医院查询医生 - 成功")
    public void testGetDoctorsByHospital_Success() {
        // Arrange
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDoctorName("李四");
        doctor2.setHospitalId(TEST_HOSPITAL_ID);

        List<Doctor> doctorList = Arrays.asList(testDoctor, doctor2);
        when(doctorMapper.selectByHospitalId(TEST_HOSPITAL_ID)).thenReturn(doctorList);

        // Act
        List<DoctorSimpleVO> result = doctorService.getDoctorsByHospital(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(doctorMapper, times(1)).selectByHospitalId(TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试搜索医生 - 成功")
    public void testSearchDoctors_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Doctor> doctorList = Arrays.asList(testDoctor);
        when(doctorMapper.searchByKeyword("张")).thenReturn(doctorList);

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.searchDoctors("张", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals("张三", result.getList().get(0).getDoctorName());

        verify(doctorMapper, times(1)).searchByKeyword("张");
    }

    @Test
    @DisplayName("测试按职称查询医生 - 成功")
    public void testGetDoctorsByTitle_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(doctorMapper.selectBySimpleCondition(isNull(), isNull(), eq("主任医师")))
            .thenReturn(Arrays.asList(testDoctor));

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.getDoctorsByTitle("主任医师", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        verify(doctorMapper, times(1)).selectBySimpleCondition(isNull(), isNull(), eq("主任医师"));
    }

    @Test
    @DisplayName("测试多条件筛选 - 无结果")
    public void testFilterDoctors_NoResults() {
        // Arrange
        DoctorFilterDTO dto = new DoctorFilterDTO();
        dto.setTitle("主任医师");
        dto.setHospitalId(999L); // 不存在的医院ID
        dto.setPage(1);
        dto.setPageSize(10);

        when(doctorMapper.selectBySimpleCondition(eq(999L), isNull(), eq("主任医师")))
            .thenReturn(Collections.emptyList());

        // Act
        PageResult<DoctorSimpleVO> result = doctorService.filterDoctors(dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }
}