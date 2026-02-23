package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 举报实体类
 * 对应表：community_report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 举报用户ID
     */
    private Long userId;

    /**
     * 举报对象类型：1-话题，2-评论
     */
    private Integer targetType;

    /**
     * 举报对象ID
     */
    private Long targetId;

    /**
     * 举报原因类型
     */
    private String reasonType;

    /**
     * 举报详细说明
     */
    private String reason;

    /**
     * 处理状态：0-待处理，1-已处理，2-已驳回
     */
    private Integer status;

    /**
     * 处理结果说明
     */
    private String handleResult;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
