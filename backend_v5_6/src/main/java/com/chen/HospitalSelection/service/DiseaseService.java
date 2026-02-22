package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.vo.DiseaseVO;

import java.util.List;

/**
 * 疾病分类服务接口
 * 提供疾病分类查询功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface DiseaseService {

    /**
     * 获取完整的疾病分类树
     *
     * @return 疾病分类树（包含一级和二级分类）
     */
    List<DiseaseVO> getDiseaseTree();

    /**
     * 获取所有一级分类
     *
     * @return 一级分类列表
     */
    List<DiseaseVO> getLevel1Diseases();

    /**
     * 根据父分类ID获取二级分类
     *
     * @param parentId 父分类ID
     * @return 二级分类列表
     */
    List<DiseaseVO> getLevel2Diseases(Long parentId);

    /**
     * 根据疾病编码查询疾病信息
     *
     * @param diseaseCode 疾病编码
     * @return 疾病信息
     */
    DiseaseVO getDiseaseByCode(String diseaseCode);

    /**
     * 根据疾病名称搜索疾病
     *
     * @param diseaseName 疾病名称（支持模糊搜索）
     * @return 疾病列表
     */
    List<DiseaseVO> searchDiseasesByName(String diseaseName);

    /**
     * 根据疾病ID获取完整路径（包含所有父级）
     *
     * @param diseaseId 疾病ID
     * @return 疾病路径（如：心血管疾病 > 高血压）
     */
    String getDiseasePath(Long diseaseId);
}
