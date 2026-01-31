package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户信息修改DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "用户信息修改请求参数")
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "健康达人")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "http://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别（0 = 未知，1 = 男，2 = 女）
     */
    @Schema(description = "性别（0 = 未知，1 = 男，2 = 女）", example = "1")
    private Integer gender;
}
