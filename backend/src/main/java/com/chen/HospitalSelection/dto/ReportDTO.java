package com.chen.HospitalSelection.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 举报DTO
 */
public class ReportDTO {
    /**
     * 举报对象类型：1-话题，2-评论
     */
    @NotNull(message = "举报对象类型不能为空")
    private Integer targetType;

    /**
     * 举报对象ID
     */
    @NotNull(message = "举报对象ID不能为空")
    private Long targetId;

    /**
     * 举报原因类型
     */
    @NotNull(message = "举报原因类型不能为空")
    private String reasonType;

    /**
     * 举报详细说明
     */
    @Size(max = 500, message = "举报说明不能超过500字")
    private String reason;

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
