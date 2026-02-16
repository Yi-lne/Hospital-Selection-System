package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 话题详情返回对象（完整信息）
 * 用于返回话题详情页面
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDetailVO {

    /**
     * 话题ID
     */
    private Long id;

    /**
     * 发布用户ID
     */
    private Long userId;

    /**
     * 发布用户昵称
     */
    private String nickname;

    /**
     * 发布用户头像
     */
    private String avatar;

    /**
     * 关联疾病编码
     */
    private String diseaseCode;

    /**
     * 关联疾病名称
     */
    private String diseaseName;

    /**
     * 一级板块
     */
    private String boardLevel1;

    /**
     * 二级板块
     */
    private String boardLevel2;

    /**
     * 板块类型
     */
    private Integer boardType;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 话题内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 是否已点赞
     */
    private Boolean isLiked;

    /**
     * 是否已收藏
     */
    private Boolean isCollected;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 评论列表（包含一级评论和回复）
     */
    private List<CommentVO> comments;
}
