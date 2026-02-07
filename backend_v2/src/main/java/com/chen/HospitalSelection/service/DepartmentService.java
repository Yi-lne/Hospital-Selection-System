package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.vo.DepartmentVO;

import java.util.List;

/**
 * 科室服务接口
 * 提供医院科室信息查询功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface DepartmentService {

    /**
     * 获取指定医院的科室列表
     *
     * @param hospitalId 医院ID
     * @return 科室列表
     */
    List<DepartmentVO> getDepartmentsByHospital(Long hospitalId);

    /**
     * 获取科室详情
     *
     * @param deptId 科室ID
     * @return 科室详细信息
     */
    DepartmentVO getDepartmentDetail(Long deptId);

    /**
     * 根据科室名称查询科室列表（模糊搜索）
     *
     * @param deptName 科室名称（支持模糊搜索）
     * @return 科室列表
     */
    List<DepartmentVO> searchDepartmentsByName(String deptName);

    /**
     * 获取所有科室类型（用于筛选）
     *
     * @return 科室名称列表（去重）
     */
    List<String> getAllDepartmentTypes();
}
