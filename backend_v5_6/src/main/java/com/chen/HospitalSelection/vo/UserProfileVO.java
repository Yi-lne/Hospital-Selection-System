package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户个人资料完整信息返回对象
 * 用于返回用户中心页面的完整信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 手机号（脱敏处理）
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
     * 用户状态
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 角色列表（如：["user", "admin"]）
     */
    private List<String> roles;

    /**
     * 话题数量
     */
    private Integer topicCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 收藏数量
     */
    private Integer collectionCount;

    /**
     * 未读消息数
     */
    private Integer unreadMessageCount;
}
