package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病史信息返回对象
 * 用于返回用户的病史记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryVO {

    /**
     * 病史ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 疾病编码（关联disease_type表）
     */
    private String diseaseCode;

    /**
     * 诊断日期
     */
    private LocalDate diagnosisDate;

    /**
     * 状态（1 = 治疗中，2 = 已康复）
     */
    private Integer status;

    /**
     * 状态描述（中文）
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "治疗中";
            case 2:
                return "已康复";
            default:
                return "未知";
        }
    }
}
