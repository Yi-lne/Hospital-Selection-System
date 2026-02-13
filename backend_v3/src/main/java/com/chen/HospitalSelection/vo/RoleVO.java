package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色信息返回VO
 *
 * @author 陈文滔
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（0-禁用，1-正常）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户数量（该角色的用户数）
     */
    private Integer userCount;

    /**
     * 权限数量（该角色的权限数）
     */
    private Integer permissionCount;
}
