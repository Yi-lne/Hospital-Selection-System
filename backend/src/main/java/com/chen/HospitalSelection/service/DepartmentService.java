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
     * 获取所有科室列表
     *
     * @return 所有科室列表
     */
    List<DepartmentVO> getAllDepartments();

    /**
     * 获取指定医院的科室列表
     *
     * @param hospitalId 医院ID
     * @return 科室列表
     */
    List<DepartmentVO> getDepartmentsByHospital(Long hospitalId);

    /**
     * 获取指定医院的科室列表（可选择是否包含已删除）
     *
     * @param hospitalId 医院ID
     * @param includeDeleted 是否包含已删除的科室
     * @return 科室列表
     */
    List<DepartmentVO> getDepartmentsByHospital(Long hospitalId, Boolean includeDeleted);

    /**
     * 获取某医院所有医生的所属科室（去重）
     *
     * @param hospitalId 医院ID
     * @return 该医院医生所属的科室列表（按科室名称去重）
     */
    List<DepartmentVO> getDepartmentsByHospitalDoctors(Long hospitalId);
}
