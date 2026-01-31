package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;

/**
 * 医生服务接口
 * 提供医生信息查询、筛选等功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface DoctorService {

    /**
     * 分页查询医生列表
     *
     * @param dto 分页查询参数
     * @return 医生分页列表
     */
    PageResult<DoctorSimpleVO> getDoctorList(PageQueryDTO dto);

    /**
     * 多条件筛选医生
     *
     * @param dto 筛选条件（医院、科室、职称等）
     * @return 筛选后的医生分页列表
     */
    PageResult<DoctorSimpleVO> filterDoctors(DoctorFilterDTO dto);

    /**
     * 获取医生详情
     *
     * @param doctorId 医生ID
     * @return 医生详细信息
     */
    DoctorVO getDoctorDetail(Long doctorId);

    /**
     * 根据科室ID查询医生列表
     *
     * @param deptId 科室ID
     * @return 医生列表
     */
    List<DoctorSimpleVO> getDoctorsByDepartment(Long deptId);

    /**
     * 根据医院ID查询医生列表
     *
     * @param hospitalId 医院ID
     * @return 医生列表
     */
    List<DoctorSimpleVO> getDoctorsByHospital(Long hospitalId);

    /**
     * 根据医院ID和科室ID查询医生列表
     *
     * @param hospitalId 医院ID
     * @param deptId     科室ID
     * @return 医生列表
     */
    List<DoctorSimpleVO> getDoctorsByHospitalAndDept(Long hospitalId, Long deptId);

    /**
     * 搜索医生（关键词搜索）
     *
     * @param keyword 搜索关键词（医生姓名）
     * @param dto     分页查询参数
     * @return 搜索结果
     */
    PageResult<DoctorSimpleVO> searchDoctors(String keyword, PageQueryDTO dto);

    /**
     * 根据职称查询医生列表
     *
     * @param title 职称（如：主任医师）
     * @param dto   分页查询参数
     * @return 医生列表
     */
    PageResult<DoctorSimpleVO> getDoctorsByTitle(String title, PageQueryDTO dto);
}
