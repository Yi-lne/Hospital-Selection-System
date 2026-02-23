package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 医院筛选条件DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "医院筛选条件请求参数")
public class HospitalFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 科室名称（可选）
     */
    @Schema(description = "科室名称", example = "心内科")
    private String deptName;

    /**
     * 医院等级（可选）
     */
    @Schema(description = "医院等级（grade3A=三甲，grade2A=二甲）", example = "grade3A")
    private String hospitalLevel;

    /**
     * 省份编码（可选）
     */
    @Schema(description = "省份编码", example = "440000")
    private String provinceCode;

    /**
     * 城市编码（可选）
     */
    @Schema(description = "城市编码", example = "440100")
    private String cityCode;

    /**
     * 区县编码（可选）
     */
    @Schema(description = "区县编码", example = "440103")
    private String areaCode;

    /**
     * 是否医保定点（可选）
     */
    @Schema(description = "是否医保定点（0 = 否，1 = 是）", example = "1")
    private Integer isMedicalInsurance;

    /**
     * 重点科室（可选）
     */
    @Schema(description = "重点科室", example = "心内科")
    private String keyDepartments;

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1", required = true)
    private Integer page;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    @Schema(description = "每页大小", example = "10", required = true)
    private Integer pageSize;

    /**
     * 排序字段（level = 级别优先，rating = 评分优先，default = 默认排序）
     */
    @Schema(description = "排序字段（level/rating）", example = "level")
    private String sortBy;
}
