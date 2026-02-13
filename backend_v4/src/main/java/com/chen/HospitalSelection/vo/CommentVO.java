package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息返回对象
 * 用于返回评论及其回复
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 话题ID
     */
    private Long topicId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像
     */
    private String avatar;

    /**
     * 父评论ID（0 = 一级评论，>0 = 回复）
     */
    private Long parentId;

    /**
     * 被回复用户昵称（仅在回复时显示）
     */
    private String replyToNickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否已点赞（需要登录时返回）
     */
    private Boolean isLiked;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 回复列表（仅一级评论时包含）
     */
    private List<CommentVO> replies;
}
