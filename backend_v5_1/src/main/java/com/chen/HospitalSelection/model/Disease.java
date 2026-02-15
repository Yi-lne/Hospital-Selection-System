package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疾病分类实体类
 * 对应表：disease_type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父分类ID（0=一级分类）
     */
    private Long parentId;

    /**
     * 疾病分类名称（如：心血管疾病、高血压）
     */
    private String diseaseName;

    /**
     * 疾病编码（唯一）
     */
    private String diseaseCode;

    /**
     * 排序权重（值越大越靠前）
     */
    private Integer sort;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;
}
