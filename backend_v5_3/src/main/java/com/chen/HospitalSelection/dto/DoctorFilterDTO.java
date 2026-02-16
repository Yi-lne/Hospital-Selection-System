package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 医生筛选条件DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "医生筛选条件请求参数")
public class DoctorFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医院ID（可选）
     */
    @Schema(description = "医院ID", example = "1")
    private Long hospitalId;

    /**
     * 科室ID（可选）
     */
    @Schema(description = "科室ID", example = "10")
    private Long deptId;

    /**
     * 职称（可选）
     */
    @Schema(description = "职称（主任医师/副主任医师/主治医师）", example = "主任医师")
    private String title;

    /**
     * 疾病编码（可选）
     */
    @Schema(description = "疾病编码", example = "cardiovascular")
    private String diseaseCode;

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
     * 排序字段（rating = 按评分排序，default = 默认排序）
     */
    @Schema(description = "排序字段（rating/default）", example = "rating")
    private String sortBy;
}
