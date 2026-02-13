package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建医生DTO（管理员专用）
 *
 * @author chen
 * @since 2025-02-07
 */
@Data
@Schema(description = "创建医生请求参数")
public class DoctorCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生姓名
     */
    @NotBlank(message = "医生姓名不能为空")
    @Schema(description = "医生姓名", example = "张医生", required = true)
    private String doctorName;

    /**
     * 所属医院ID
     */
    @NotNull(message = "医院ID不能为空")
    @Schema(description = "医院ID", required = true)
    private Long hospitalId;

    /**
     * 科室ID
     */
    @NotNull(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private Long deptId;

    /**
     * 职称
     */
    @NotBlank(message = "职称不能为空")
    @Schema(description = "职称", example = "主任医师", required = true)
    private String title;

    /**
     * 专业特长
     */
    @Schema(description = "专业特长", example = "擅长冠心病、高血压治疗")
    private String specialty;

    /**
     * 学术背景
     */
    @Schema(description = "学术背景", example = "医学博士，博士生导师")
    private String academicBackground;

    /**
     * 坐诊时间
     */
    @Schema(description = "坐诊时间", example = "周一上午、周三下午")
    private String scheduleTime;

    /**
     * 挂号/咨询费
     */
    @Schema(description = "挂号/咨询费", example = "50.00")
    private BigDecimal consultationFee;

    /**
     * 患者评分（0.00-5.00）
     */
    @Schema(description = "患者评分", example = "4.8")
    private BigDecimal rating;

    /**
     * 评价数量
     */
    @Schema(description = "评价数量", example = "50")
    private Integer reviewCount;
}