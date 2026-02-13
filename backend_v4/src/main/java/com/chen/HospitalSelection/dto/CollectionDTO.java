package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收藏操作DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "收藏操作请求参数")
public class CollectionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型（1 = 医院，2 = 医生，3 = 话题）
     */
    @NotNull(message = "收藏类型不能为空")
    @Schema(description = "收藏类型（1=医院，2=医生，3=话题）", example = "1", required = true)
    private Integer targetType;

    /**
     * 收藏目标ID
     */
    @NotNull(message = "收藏目标ID不能为空")
    @Schema(description = "收藏目标ID（医院/医生/话题ID）", example = "123", required = true)
    private Long targetId;
}
