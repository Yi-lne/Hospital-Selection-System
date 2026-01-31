package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医院科室实体类
 * 对应表：hospital_department
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 医院ID（关联hospital_info.id）
     */
    private Long hospitalId;

    /**
     * 科室名称（如：心内科、肿瘤科）
     */
    private String deptName;

    /**
     * 科室简介
     */
    private String deptIntro;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;
}
