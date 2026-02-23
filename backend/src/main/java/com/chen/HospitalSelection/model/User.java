package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 对应表：sys_user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 手机号（唯一，用于注册、登录）
     */
    private String phone;

    /**
     * 密码（MD5/BCrypt加密）
     */
    private String password;

    /**
     * 用户昵称（初次注册随机生成，后续可修改）
     */
    private String nickname;

    /**
     * 头像URL（初次注册统一使用默认头像，后续可修改）
     */
    private String avatar;

    /**
     * 性别（0=未知，1=男，2=女）
     */
    private Integer gender;

    /**
     * 状态（1=正常，0=禁用）
     */
    private Integer status;

    /**
     * 封禁开始时间
     */
    private LocalDateTime banStartTime;

    /**
     * 封禁结束时间（null表示永久封禁）
     */
    private LocalDateTime banEndTime;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
