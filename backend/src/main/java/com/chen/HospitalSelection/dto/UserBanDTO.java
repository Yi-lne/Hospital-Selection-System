package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户封禁DTO
 *
 * @author chen
 * @since 2025-02-16
 */
@Data
@Schema(description = "用户封禁请求参数")
public class UserBanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封禁时长类型
     * 2_MINUTES, 2_HOURS, 1_DAY, 7_DAYS, 1_MONTH, 3_MONTHS, PERMANENT
     */
    @NotNull(message = "封禁时长不能为空")
    @Schema(description = "封禁时长类型", example = "7_DAYS", required = true)
    private String durationType;

    /**
     * 封禁原因（可选）
     */
    @Schema(description = "封禁原因", example = "发布违规内容")
    private String reason;
}
