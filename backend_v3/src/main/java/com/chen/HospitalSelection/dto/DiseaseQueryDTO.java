package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 疾病查询DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "疾病查询请求参数")
public class DiseaseQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父分类ID（可选，0 = 查询一级分类）
     */
    @Schema(description = "父分类ID（0 = 查询一级分类）", example = "0")
    private Long parentId;

    /**
     * 疾病名称关键词（可选，支持模糊搜索）
     */
    @Schema(description = "疾病名称关键词", example = "心血管")
    private String keyword;

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1", required = true)
    private Integer pageNum;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    @Schema(description = "每页大小", example = "10", required = true)
    private Integer pageSize;
}
