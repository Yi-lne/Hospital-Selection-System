package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 病史信息DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "病史信息请求参数")
public class MedicalHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病名称
     */
    @NotBlank(message = "疾病名称不能为空")
    @Size(max = 100, message = "疾病名称长度不能超过100个字符")
    @Schema(description = "疾病名称", example = "高血压", required = true)
    private String diseaseName;

    /**
     * 诊断日期（可选）
     */
    @Schema(description = "诊断日期", example = "2024-01-15")
    private LocalDate diagnosisDate;

    /**
     * 状态（1 = 治疗中，2 = 已康复）
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（1=治疗中，2=已康复）", example = "1", required = true)
    private Integer status;
}
