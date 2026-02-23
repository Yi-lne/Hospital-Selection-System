package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息返回对象
 * 用于返回用户基本信息（不包含敏感信息）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号（脱敏处理，如：138****8000）
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别（0 = 未知，1 = 男，2 = 女）
     */
    private Integer gender;

    /**
     * 性别文本（用于前端显示）
     */
    private String genderText;

    /**
     * 用户状态（1 = 正常，0 = 禁用）
     */
    private Integer status;

    /**
     * 封禁开始时间
     */
    private String banStartTime;

    /**
     * 封禁结束时间
     */
    private String banEndTime;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 角色列表（如：["user", "admin"]）
     */
    private List<String> roles;
}
