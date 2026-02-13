package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话信息返回对象（用于会话列表）
 * 包含与某用户的最新一条消息及未读数量
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageUnreadVO {

    /**
     * 对话用户ID
     */
    private Long userId;

    /**
     * 对话用户昵称
     */
    private String nickname;

    /**
     * 对话用户头像
     */
    private String avatar;

    /**
     * 最新消息内容
     */
    private String lastMessage;

    /**
     * 最新消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;
}
