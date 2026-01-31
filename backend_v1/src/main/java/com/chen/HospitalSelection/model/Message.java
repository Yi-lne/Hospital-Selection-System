package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户私信实体类
 * 对应表：user_message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 私信内容
     */
    private String content;

    /**
     * 是否已读（0=未读，1=已读）
     */
    private Integer isRead;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
