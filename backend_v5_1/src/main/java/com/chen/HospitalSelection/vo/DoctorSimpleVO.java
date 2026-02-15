package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 医生简要信息返回对象
 * 用于列表展示（不包含详细信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSimpleVO {

    /**
     * 医生ID
     */
    private Long id;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 所属医院ID
     */
    private Long hospitalId;

    /**
     * 所属医院名称
     */
    private String hospitalName;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 职称
     */
    private String title;

    /**
     * 专业特长（简要，100字以内）
     */
    private String specialty;

    /**
     * 坐诊时间
     */
    private String scheduleTime;

    /**
     * 挂号/咨询费
     */
    private BigDecimal consultationFee;

    /**
     * 患者评分
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 是否已收藏
     */
    private Boolean isCollected;
}
