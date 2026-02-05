package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 私信信息返回对象
 * 用于返回私信聊天记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 发送者昵称
     */
    private String senderNickname;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 接收者昵称
     */
    private String receiverNickname;

    /**
     * 接收者头像
     */
    private String receiverAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读（0 = 未读，1 = 已读）
     */
    private Integer isRead;

    /**
     * 发送时间
     */
    private LocalDateTime createTime;

    /**
     * 是否是当前用户发送的消息（用于前端判断消息显示方向）
     */
    private Boolean isSelf;
}
