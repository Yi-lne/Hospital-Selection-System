package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 * 对应表：user_collection
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectionItem {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 收藏用户ID
     */
    private Long userId;

    /**
     * 收藏类型（1=医院，2=医生，3=话题）
     */
    private Integer targetType;

    /**
     * 收藏目标ID（医院/医生/话题ID）
     */
    private Long targetId;

    /**
     * 逻辑删除（0=未删，1=已删，取消收藏=标记为1）
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
