package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 医院简要信息返回对象
 * 用于列表展示（不包含详细信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalSimpleVO {

    /**
     * 医院ID
     */
    private Long id;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 医院等级
     */
    private String hospitalLevel;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县名称
     */
    private String areaName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 重点科室（逗号分隔字符串）
     */
    private String keyDepartments;

    /**
     * 用户评分
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 是否医保定点
     */
    private Integer isMedicalInsurance;

    /**
     * 是否已收藏（需要登录时返回）
     */
    private Boolean isCollected;
}
