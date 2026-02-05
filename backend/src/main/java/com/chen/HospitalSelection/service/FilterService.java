package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 多条件筛选核心服务
 * 提供基于规则匹配模型的医院和医生筛选功能
 *
 * @author chen
 * @since 2025-01-31
 */
public interface FilterService {

    /**
     * 多条件筛选医院（核心功能）
     * 支持疾病类型、医院等级、地理位置、专科特色、医保定点等条件组合筛选
     *
     * @param dto 筛选条件
     * @return 筛选结果（分页）
     */
    PageResult<HospitalSimpleVO> filterHospitals(HospitalFilterDTO dto);

    /**
     * 多条件筛选医生
     * 支持按医院、科室、职称等条件筛选
     *
     * @param dto 筛选条件
     * @return 筛选结果（分页）
     */
    PageResult<DoctorSimpleVO> filterDoctors(DoctorFilterDTO dto);

    /**
     * 获取筛选条件的统计数据
     * 用于前端展示筛选条件选项时显示匹配数量
     *
     * @param filterMap 筛选条件Map（key为筛选维度，value为筛选值）
     * @return 统计结果
     */
    Map<String, Long> getFilterStats(Map<String, Object> filterMap);

    /**
     * 根据疾病推荐医院
     * 基于疾病分类和医院重点科室进行智能匹配
     *
     * @param diseaseCode 疾病编码
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 推荐医院列表
     */
    PageResult<HospitalSimpleVO> recommendHospitalsByDisease(String diseaseCode, Integer pageNum, Integer pageSize);

    /**
     * 计算医院匹配度
     * 根据用户筛选条件计算医院匹配分数
     *
     * @param hospitalId 医院ID
     * @param dto 筛选条件
     * @return 匹配度分数（0-100）
     */
    Integer calculateMatchScore(Long hospitalId, HospitalFilterDTO dto);
}
