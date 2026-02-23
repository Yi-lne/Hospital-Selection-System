package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 医院信息返回对象（详细信息）
 * 用于返回医院详情页面的完整信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalVO {

    /**
     * 医院ID
     */
    private Long id;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 医院等级（如：grade3A = 三甲，grade2A = 二甲）
     */
    private String hospitalLevel;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县编码
     */
    private String areaCode;

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
     * 是否医保定点（0 = 否，1 = 是）
     */
    private Integer isMedicalInsurance;

    /**
     * 科室列表
     */
    private List<DepartmentVO> departments;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
