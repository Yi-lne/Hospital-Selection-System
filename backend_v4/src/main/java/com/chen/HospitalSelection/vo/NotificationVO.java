package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 通知VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    /**
     * 通知ID
     */
    private Long id;

    /**
     * 通知类型：comment-话题评论, reply-评论回复, delete_topic-话题删除, delete_comment-评论删除
     */
    private String type;

    /**
     * 通知类型描述
     */
    private String typeDesc;

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
    private Boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
