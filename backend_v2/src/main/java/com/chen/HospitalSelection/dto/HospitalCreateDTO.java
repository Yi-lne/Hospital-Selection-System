package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 添加医院DTO（管理员专用）
 *
 * @author chen
 * @since 2025-02-07
 */
@Data
@Schema(description = "添加医院请求参数")
public class HospitalCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医院名称
     */
    @NotBlank(message = "医院名称不能为空")
    @Schema(description = "医院名称", example = "广东省人民医院", required = true)
    private String hospitalName;

    /**
     * 医院等级
     */
    @NotBlank(message = "医院等级不能为空")
    @Schema(description = "医院等级", example = "grade3A", required = true)
    private String hospitalLevel;

    /**
     * 省份编码
     */
    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份编码", example = "440000", required = true)
    private String provinceCode;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市编码", example = "440100", required = true)
    private String cityCode;

    /**
     * 区县编码
     */
    @NotNull(message = "区县不能为空")
    @Schema(description = "区县编码", example = "440103", required = true)
    private String areaCode;

    /**
     * 详细地址
     */
    @NotBlank(message = "地址不能为空")
    @Schema(description = "详细地址", example = "广州市越秀区xx路123号", required = true)
    private String address;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话", example = "020-12345678")
    private String phone;

    /**
     * 重点科室
     */
    @Schema(description = "重点科室（逗号分隔）", example = "心内科,心血管外科")
    private String keyDepartments;

    /**
     * 医疗设备
     */
    @Schema(description = "医疗设备（逗号分隔）", example = "核磁共振,CT机")
    private String medicalEquipment;

    /**
     * 专家团队
     */
    @Schema(description = "专家团队简介")
    private String expertTeam;

    /**
     * 医院简介
     */
    @Schema(description = "医院简介")
    private String intro;

    /**
     * 用户评分（0.00-5.00）
     */
    @Schema(description = "用户评分", example = "4.8")
    private BigDecimal rating;

    /**
     * 是否医保定点
     */
    @NotNull(message = "医保定点不能为空")
    @Schema(description = "是否医保定点（0=否，1=是）", example = "1")
    private Integer isMedicalInsurance;
}