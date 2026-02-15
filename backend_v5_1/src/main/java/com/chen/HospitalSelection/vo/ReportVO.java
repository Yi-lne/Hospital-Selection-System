package com.chen.HospitalSelection.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 举报VO
 */
@Data
public class ReportVO {
    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer targetType;
    private String targetTypeName;  // 话题/评论
    private Long targetId;
    private String targetTitle;      // 话题标题或评论内容
    private Long topicId;          // 话题ID（用于评论跳转）
    private String reasonType;
    private String reasonTypeDesc;   // 举报原因描述
    private String reason;
    private Integer status;
    private String statusDesc;       // 状态描述
    private String handleResult;
    private Long handlerId;
    private String handlerName;      // 处理人昵称
    private LocalDateTime createTime;
}
