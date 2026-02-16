package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 找回密码DTO
 *
 * @author chen
 * @since 2025-01-31
 */
@Data
@Schema(description = "找回密码请求参数")
public class PasswordResetDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000", required = true)
    private String phone;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 6, message = "验证码长度不正确")
    @Schema(description = "短信验证码", example = "123456", required = true)
    private String verificationCode;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "新密码", example = "123456", required = true)
    private String newPassword;
}
