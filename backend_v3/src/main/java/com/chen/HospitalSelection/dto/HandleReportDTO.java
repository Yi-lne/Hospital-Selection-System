package com.chen.HospitalSelection.dto;

import javax.validation.constraints.NotNull;

/**
 * 处理举报DTO
 */
public class HandleReportDTO {
    /**
     * 处理状态：1-已处理，2-已驳回
     */
    @NotNull(message = "处理状态不能为空")
    private Integer status;

    /**
     * 处理结果说明
     */
    private String handleResult;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }
}
