package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 话题信息返回对象（简要信息）
 * 用于列表展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicVO {

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
     * 一级板块（心血管区、内分泌区、肿瘤区、儿科区等）
     */
    private String boardLevel1;

    /**
     * 二级板块（具体疾病，如高血压、冠心病、糖尿病等）
     */
    private String boardLevel2;

    /**
     * 板块类型（1 = 疾病板块，2 = 医院评价区，3 = 就医经验区，4 = 康复护理区）
     */
    private Integer boardType;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 话题内容摘要（100字以内）
     */
    private String contentSummary;

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
     * 是否已点赞（需要登录时返回）
     */
    private Boolean isLiked;

    /**
     * 是否已收藏（需要登录时返回）
     */
    private Boolean isCollected;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
