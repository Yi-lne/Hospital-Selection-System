package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 * 对应表：community_comment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 话题ID（关联community_topic.id）
     */
    private Long topicId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID（0=一级评论，>0=回复）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户昵称（非数据库字段，通过JOIN查询获取）
     */
    private transient String userNickname;

    /**
     * 用户头像（非数据库字段，通过JOIN查询获取）
     */
    private transient String userAvatar;

    /**
     * 回复列表（非数据库字段，通过嵌套查询获取）
     */
    private transient List<Comment> replies;
}
