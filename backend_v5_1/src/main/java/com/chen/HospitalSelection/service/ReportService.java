package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.HandleReportDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.ReportDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.ReportVO;

/**
 * 举报服务接口
 */
public interface ReportService {

    /**
     * 创建举报
     */
    Long createReport(Long userId, ReportDTO dto);

    /**
     * 获取举报列表（分页）
     */
    PageResult<ReportVO> getReportList(Integer status, PageQueryDTO dto);

    /**
     * 处理举报
     */
    void handleReport(Long reportId, HandleReportDTO dto, Long handlerId);

    /**
     * 删除举报记录
     */
    void deleteReport(Long reportId);
}
