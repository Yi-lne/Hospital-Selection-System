package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;

/**
 * 医院服务接口
 * 提供医院信息查询、筛选、搜索等功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface HospitalService {

    /**
     * 分页查询医院列表
     *
     * @param dto 分页查询参数
     * @return 医院分页列表
     */
    PageResult<HospitalSimpleVO> getHospitalList(PageQueryDTO dto);

    /**
     * 多条件筛选医院（核心功能）
     *
     * @param dto 筛选条件（疾病类型、医院等级、地区、医保等）
     * @return 筛选后的医院分页列表
     */
    PageResult<HospitalSimpleVO> filterHospitals(HospitalFilterDTO dto);

    /**
     * 获取医院详情
     *
     * @param hospitalId 医院ID
     * @return 医院详细信息
     */
    HospitalVO getHospitalDetail(Long hospitalId);

    /**
     * 获取医院的科室列表
     *
     * @param hospitalId 医院ID
     * @return 科室列表
     */
    List<DepartmentVO> getHospitalDepartments(Long hospitalId);

    /**
     * 获取医院的医生列表
     *
     * @param hospitalId 医院ID
     * @return 医生列表
     */
    List<DoctorSimpleVO> getHospitalDoctors(Long hospitalId);

    /**
     * 搜索医院（关键词搜索）
     *
     * @param keyword  搜索关键词（医院名称）
     * @param dto      分页查询参数
     * @return 搜索结果
     */
    PageResult<HospitalSimpleVO> searchHospitals(String keyword, PageQueryDTO dto);

    /**
     * 获取搜索建议（自动补全）
     *
     * @param keyword 搜索关键词
     * @return 建议列表（医院名称）
     */
    List<String> getSearchSuggestions(String keyword);

    /**
     * 根据地区获取医院列表
     *
     * @param cityCode 城市编码
     * @param dto      分页查询参数
     * @return 医院列表
     */
    PageResult<HospitalSimpleVO> getHospitalsByCity(String cityCode, PageQueryDTO dto);

    /**
     * 根据等级获取医院列表
     *
     * @param level 医院等级（如：grade3A）
     * @param dto   分页查询参数
     * @return 医院列表
     */
    PageResult<HospitalSimpleVO> getHospitalsByLevel(String level, PageQueryDTO dto);
}
