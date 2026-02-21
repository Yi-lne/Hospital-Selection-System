package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医院信息实体类
 * 对应表：hospital_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 医院名称（唯一）
     */
    private String hospitalName;

    /**
     * 医院等级（如：grade3A=三甲，grade2A=二甲）
     */
    private String hospitalLevel;

    /**
     * 省份编码（关联area_info.code）
     */
    private String provinceCode;

    /**
     * 城市编码（关联area_info.code）
     */
    private String cityCode;

    /**
     * 区县编码（关联area_info.code）
     */
    private String areaCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 重点科室（逗号分隔）
     */
    private String keyDepartments;

    /**
     * 医疗设备（逗号分隔）
     */
    private String medicalEquipment;

    /**
     * 专家团队简介
     */
    private String expertTeam;

    /**
     * 医院简介
     */
    private String intro;

    /**
     * 用户评分（0.00-5.00）
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 是否医保定点（0=否，1=是）
     */
    private Integer isMedicalInsurance;

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
