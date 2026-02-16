package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 点赞实体类
 * 对应表：community_like
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 点赞用户ID
     */
    private Long userId;

    /**
     * 点赞类型（1=话题，2=评论）
     */
    private Integer targetType;

    /**
     * 点赞目标ID（话题/评论ID）
     */
    private Long targetId;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
