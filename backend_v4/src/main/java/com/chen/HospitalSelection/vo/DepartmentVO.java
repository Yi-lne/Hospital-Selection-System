package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 科室信息返回对象
 * 用于返回医院科室信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentVO {

    /**
     * 科室ID
     */
    private Long id;

    /**
     * 所属医院ID
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
     * 该科室下的医生数量
     */
    private Integer doctorCount;
}
