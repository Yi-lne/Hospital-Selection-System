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
      * 获取所有一级疾病分类
      * @return 一级疾病分类列表
      */
     List<DiseaseVO> getLevel1Diseases();
}
