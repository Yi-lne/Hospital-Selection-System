package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 通知实体类
 * 对应表：user_notification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 接收通知的用户ID
     */
    private Long userId;

    /**
     * 通知类型：comment-话题评论, reply-评论回复, delete_topic-话题删除, delete_comment-评论删除
     */
    private String type;

    /**
     * 关联ID（话题ID或评论ID）
     */
    private Long relatedId;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 已读：0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
