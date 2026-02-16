package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 系统角色实体类
 * 对应表：sys_role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 角色名称（如：普通用户、管理员）
     */
    private String roleName;

    /**
     * 角色编码（如：user、admin）
     */
    private String roleCode;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
