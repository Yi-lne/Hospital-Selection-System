package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 社区话题实体类
 * 对应表：community_topic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 发布用户ID（关联sys_user.id）
     */
    private Long userId;

    /**
     * 关联疾病编码（可选，用于分类）
     */
    private String diseaseCode;

    /**
     * 话题板块大类（1=疾病板块，2=医院评价区，3=就医经验区，4=康复护理区）
     */
    private Integer boardType;

    /**
     * 话题板块子类（心血管、内分泌、肿瘤、儿科等）
     */
    private String boardSub;

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
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
