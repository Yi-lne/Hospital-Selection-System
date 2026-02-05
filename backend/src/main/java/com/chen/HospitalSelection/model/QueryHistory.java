package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户查询历史实体类
 * 对应表：user_query_history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryHistory {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID（关联sys_user.id）
     */
    private Long userId;

    /**
     * 查询类型（1=医院，2=医生，3=话题）
     */
    private Integer queryType;

    /**
     * 查询目标ID
     */
    private Long targetId;

    /**
     * 查询条件（JSON格式，记录筛选条件）
     */
    private String queryParams;

    /**
     * 逻辑删除（0=未删，1=已删）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
