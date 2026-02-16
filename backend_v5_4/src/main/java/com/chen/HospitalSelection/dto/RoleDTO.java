package com.chen.HospitalSelection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 角色数据传输对象
 *
 * @author chen
 * @since 2025-01-31
 */
@Data
@Schema(description = "角色信息")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    @Schema(description = "角色名称", example = "普通用户")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50")
    @Schema(description = "角色编码", example = "user")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200")
    @Schema(description = "角色描述", example = "普通用户角色")
    private String description;
}
