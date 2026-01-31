package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * ID参数DTO（通用）
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "ID参数请求（通用）")
public class IdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    @Schema(description = "主键ID", example = "1", required = true)
    private Long id;
}
