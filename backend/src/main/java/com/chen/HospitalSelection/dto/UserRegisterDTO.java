package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户注册DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000", required = true)
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456", required = true)
    private String password;

    /**
     * 昵称（可选）
     */
    @Schema(description = "昵称", example = "健康达人")
    private String nickname;

    /**
     * 验证码（可选）
     */
    @Schema(description = "验证码", example = "123456")
    private String code;

    /**
     * 图片验证码ID
     */
    @NotBlank(message = "验证码ID不能为空")
    @Schema(description = "验证码ID", example = "1234567890-1234", required = true)
    private String captchaId;

    /**
     * 滑块移动距离
     */
    @NotNull(message = "滑块移动距离不能为空")
    @Schema(description = "滑块移动距离", example = "150", required = true)
    private Integer moveX;
}
