package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.vo.DepartmentVO;
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
 * 科室服务测试类
 *
 * @author chen
 */
public class DepartmentServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.DepartmentServiceImpl departmentService;

    private Department testDepartment;
    private Hospital testHospital;
    private static final Long TEST_DEPT_ID = 1L;
    private static final Long TEST_HOSPITAL_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setId(TEST_HOSPITAL_ID);
        testHospital.setHospitalName("广东省人民医院");

        // 创建测试科室（Department没有createTime字段）
        testDepartment = new Department();
        testDepartment.setId(TEST_DEPT_ID);
        testDepartment.setHospitalId(TEST_HOSPITAL_ID);
        testDepartment.setDeptName("心内科");
        testDepartment.setDeptIntro("心血管疾病诊疗科室");
        testDepartment.setIsDeleted(0);
    }

    @Test
    @DisplayName("测试获取医院科室列表 - 成功")
    public void testGetDepartmentsByHospital_Success() {
        // Arrange
        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setHospitalId(TEST_HOSPITAL_ID);
        dept2.setDeptName("心血管外科");

        List<Department> departmentList = Arrays.asList(testDepartment, dept2);
        when(departmentMapper.selectByHospitalId(TEST_HOSPITAL_ID)).thenReturn(departmentList);

        // Act
        List<DepartmentVO> result = departmentService.getDepartmentsByHospital(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("心内科", result.get(0).getDeptName());
        assertEquals("心血管外科", result.get(1).getDeptName());

        verify(departmentMapper, times(1)).selectByHospitalId(TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试获取科室详情 - 成功")
    public void testGetDepartmentDetail_Success() {
        // Arrange
        when(departmentMapper.selectById(TEST_DEPT_ID)).thenReturn(testDepartment);

        // Act
        DepartmentVO result = departmentService.getDepartmentDetail(TEST_DEPT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_DEPT_ID, result.getId());
        assertEquals("心内科", result.getDeptName());
        assertEquals("心血管疾病诊疗科室", result.getDeptIntro());

        verify(departmentMapper, times(1)).selectById(TEST_DEPT_ID);
    }

    @Test
    @DisplayName("测试获取科室详情 - 科室不存在")
    public void testGetDepartmentDetail_NotFound() {
        // Arrange
        when(departmentMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.getDepartmentDetail(999L);
        });

        assertEquals("科室不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试搜索科室 - 成功")
    public void testSearchDepartmentsByName_Success() {
        // Arrange
        when(departmentMapper.searchByName("心")).thenReturn(Arrays.asList(testDepartment));

        // Act
        List<DepartmentVO> result = departmentService.searchDepartmentsByName("心");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("心内科", result.get(0).getDeptName());

        verify(departmentMapper, times(1)).searchByName("心");
    }

    @Test
    @DisplayName("测试获取医院科室列表 - 空列表")
    public void testGetDepartmentsByHospital_Empty() {
        // Arrange
        when(departmentMapper.selectByHospitalId(TEST_HOSPITAL_ID)).thenReturn(Collections.emptyList());

        // Act
        List<DepartmentVO> result = departmentService.getDepartmentsByHospital(TEST_HOSPITAL_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试获取所有科室类型 - 成功")
    public void testGetAllDepartmentTypes_Success() {
        // Arrange
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setHospitalId(1L);
        dept1.setDeptName("心内科");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setHospitalId(1L);
        dept2.setDeptName("心血管外科");

        Department dept3 = new Department();
        dept3.setId(3L);
        dept3.setHospitalId(1L);
        dept3.setDeptName("心内科"); // 重复

        when(departmentMapper.selectByHospitalId(1L)).thenReturn(Arrays.asList(dept1, dept2, dept3));

        // Act
        List<String> result = departmentService.getAllDepartmentTypes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // distinct()会去重，所以是2个不同的科室名称

        verify(departmentMapper, times(1)).selectByHospitalId(1L);
    }
}
