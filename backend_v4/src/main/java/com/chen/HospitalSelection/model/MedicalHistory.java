package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户病史实体类
 * 对应表：user_medical_history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID（关联sys_user.id）
     */
    private Long userId;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 诊断日期
     */
    private LocalDate diagnosisDate;

    /**
     * 状态（1=治疗中，2=已康复）
     */
    private Integer status;

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
