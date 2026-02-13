package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 密码修改DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "密码修改请求参数")
public class PasswordUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "123456", required = true)
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "654321", required = true)
    private String newPassword;
}
