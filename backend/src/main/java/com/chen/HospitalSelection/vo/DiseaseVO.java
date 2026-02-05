package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 疾病分类返回对象（树形结构）
 * 用于返回疾病分类树
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseVO {

    /**
     * 疾病分类ID
     */
    private Long id;

    /**
     * 父分类ID（0 = 一级分类）
     */
    private Long parentId;

    /**
     * 疾病分类名称（如：心血管疾病、高血压）
     */
    private String diseaseName;

    /**
     * 疾病编码（唯一，用于筛选查询）
     */
    private String diseaseCode;

    /**
     * 排序权重（值越大越靠前）
     */
    private Integer sort;

    /**
     * 子分类列表（树形结构）
     */
    private List<DiseaseVO> children;
}
