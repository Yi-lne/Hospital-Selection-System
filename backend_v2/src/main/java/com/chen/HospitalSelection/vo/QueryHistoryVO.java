package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询历史返回对象
 * 用于返回用户的查询浏览历史
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryHistoryVO {

    /**
     * 历史记录ID
     */
    private Long id;

    /**
     * 查询类型（1 = 医院，2 = 医生，3 = 话题）
     */
    private Integer queryType;

    /**
     * 查询类型描述（中文）
     */
    private String queryTypeDesc;

    /**
     * 查询目标ID（可选）
     */
    private Long targetId;

    /**
     * 查询目标名称（可选）
     */
    private String targetName;

    /**
     * 查询条件（JSON格式，记录筛选条件）
     */
    private String queryParams;

    /**
     * 查询时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 医院信息（当queryType=1且有targetId时返回）
     */
    private HospitalSimpleVO hospital;

    /**
     * 医生信息（当queryType=2且有targetId时返回）
     */
    private DoctorSimpleVO doctor;

    /**
     * 话题信息（当queryType=3且有targetId时返回）
     */
    private TopicVO topic;

    /**
     * 获取查询类型描述
     */
    public String getQueryTypeDesc() {
        if (queryType == null) {
            return "未知";
        }
        switch (queryType) {
            case 1:
                return "医院";
            case 2:
                return "医生";
            case 3:
                return "话题";
            default:
                return "未知";
        }
    }
}
