package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生信息实体类
 * 对应表：doctor_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    /**
     * 主键ID
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
     * 所属科室ID（关联hospital_department.id）
     */
    private Long deptId;

    /**
     * 职称（如：主任医师、副主任医师）
     */
    private String title;

    /**
     * 专业特长
     */
    private String specialty;

    /**
     * 学术背景（学历、研究方向等）
     */
    private String academicBackground;

    /**
     * 坐诊时间（如：周一上午、周三下午）
     */
    private String scheduleTime;

    /**
     * 挂号/咨询费
     */
    private BigDecimal consultationFee;

    /**
     * 患者评分（0.00-5.00）
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
