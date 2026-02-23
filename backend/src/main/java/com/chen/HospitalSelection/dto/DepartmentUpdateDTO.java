package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 更新科室DTO（管理员专用）
 *
 * @author chen
 * @since 2025-02-16
 */
@Data
@Schema(description = "更新科室请求参数")
public class DepartmentUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
