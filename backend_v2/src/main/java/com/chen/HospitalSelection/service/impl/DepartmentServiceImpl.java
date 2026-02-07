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

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<DepartmentVO> getDepartmentsByHospital(Long hospitalId) {
        log.info("查询医院科室列表，医院ID：{}", hospitalId);

        List<Department> departmentList = departmentMapper.selectByHospitalId(hospitalId);

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

        // 这里需要从数据库查询所有不重复的科室名称
        // 简化实现，实际应该在Mapper中添加相应方法
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
        return vo;
    }
}
