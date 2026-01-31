package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户-角色关联实体类
 * 对应表：sys_user_role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID（关联sys_user.id）
     */
    private Long userId;

    /**
     * 角色ID（关联sys_role.id）
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
