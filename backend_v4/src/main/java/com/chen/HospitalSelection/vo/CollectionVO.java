package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏列表返回对象
 * 用于返回用户的收藏列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVO {

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 收藏类型（1 = 医院，2 = 医生，3 = 话题）
     */
    private Integer targetType;

    /**
     * 收藏目标ID
     */
    private Long targetId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;

    /**
     * 医院信息（当targetType=1时返回）
     */
    private HospitalSimpleVO hospital;

    /**
     * 医生信息（当targetType=2时返回）
     */
    private DoctorSimpleVO doctor;

    /**
     * 话题信息（当targetType=3时返回）
     */
    private TopicVO topic;
}
