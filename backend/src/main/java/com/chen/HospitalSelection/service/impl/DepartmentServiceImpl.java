package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.exception.ResourceNotFoundException;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.service.DepartmentService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 科室服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentVO> getAllDepartments() {
        log.info("查询所有有医生的科室列表");

        // 只查询那些至少有一个医生的科室
        List<Department> departmentList = departmentMapper.selectDepartmentsWithDoctors();

        return departmentList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentVO> getDepartmentsByHospital(Long hospitalId) {
        log.info("查询医院科室列表，医院ID：{}", hospitalId);

        List<Department> departmentList = departmentMapper.selectByHospitalId(hospitalId);

        return departmentList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentVO> getDepartmentsByHospital(Long hospitalId, Boolean includeDeleted) {
        log.info("查询医院科室列表，医院ID：{}，包含已删除：{}", hospitalId, includeDeleted);

        List<Department> departmentList;
        if (includeDeleted != null && includeDeleted) {
            departmentList = departmentMapper.selectByHospitalIdIncludingDeleted(hospitalId, true);
        } else {
            departmentList = departmentMapper.selectByHospitalId(hospitalId);
        }

        return departmentList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentVO> getDepartmentsByHospitalDoctors(Long hospitalId) {
        log.info("查询医院医生所属科室列表，医院ID：{}", hospitalId);

        // 从医生表中获取该医院所有医生的所属科室（按科室名称去重）
        List<Department> departmentList = departmentMapper.selectDepartmentsByHospitalDoctors(hospitalId);

        return departmentList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentVO getDepartmentDetail(Long deptId) {
        log.info("查询科室详情，科室ID：{}", deptId);

        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw ResourceNotFoundException.departmentNotFound();
        }

        return convertToVO(department);
    }

    @Override
    public List<DepartmentVO> searchDepartmentsByName(String deptName) {
        log.info("搜索科室，科室名称：{}", deptName);

        List<Department> departmentList = departmentMapper.searchByName(deptName);

        return departmentList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllDepartmentTypes() {
        log.info("获取所有科室类型");

        List<Department> allDepartments = departmentMapper.selectByHospitalId(1L);

        return allDepartments.stream()
                .map(Department::getDeptName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 转换为科室VO
     */
    private DepartmentVO convertToVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        // 手动处理isDeleted字段：Integer(0/1) -> Boolean(false/true)
        vo.setIsDeleted(department.getIsDeleted() != null && department.getIsDeleted() == 1);
        return vo;
    }
}
