package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
     * 用户状态（1 = 正常，0 = 禁用）
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;
}
