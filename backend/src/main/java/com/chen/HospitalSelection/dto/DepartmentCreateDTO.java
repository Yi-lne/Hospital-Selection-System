package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建科室DTO（管理员专用）
 *
 * @author chen
 * @since 2025-02-07
 */
@Data
@Schema(description = "创建科室请求参数")
public class DepartmentCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医院ID
     */
    @NotNull(message = "医院ID不能为空")
    @Schema(description = "医院ID", required = true)
    private Long hospitalId;

    /**
     * 科室名称
     */
    @NotBlank(message = "科室名称不能为空")
    @Schema(description = "科室名称", example = "心内科", required = true)
    private String deptName;

    /**
     * 科室简介
     */
    @Schema(description = "科室简介", example = "心内科擅长治疗心血管疾病")
    private String deptIntro;
}