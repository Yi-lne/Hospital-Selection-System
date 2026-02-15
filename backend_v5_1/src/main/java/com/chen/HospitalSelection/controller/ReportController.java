package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.HandleReportDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.ReportDTO;
import com.chen.HospitalSelection.service.ReportService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.ReportVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 举报管理接口
 * 基础路径：/api/report
 */
@RestController
@RequestMapping("/report")
@Api(tags = "举报管理")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 创建举报
     * 接口路径：POST /api/report
     * 是否需要登录：是
     */
    @PostMapping
    @ApiOperation("创建举报")
    public Result<Long> createReport(@RequestBody @Valid ReportDTO dto,
                                    HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long reportId = reportService.createReport(userId, dto);
        return Result.success(reportId, "举报成功");
    }

    /**
     * 获取举报列表
     * 接口路径：GET /api/report/list
     * 是否需要登录：是（管理员）
     */
    @GetMapping("/list")
    @ApiOperation("获取举报列表")
    public Result<PageResult<ReportVO>> getReportList(
            @RequestParam(required = false) Integer status,
            @Valid PageQueryDTO dto) {
        PageResult<ReportVO> pageResult = reportService.getReportList(status, dto);
        return Result.success(pageResult);
    }

    /**
     * 处理举报
     * 接口路径：PUT /api/report/{id}/handle
     * 是否需要登录：是（管理员）
     */
    @PutMapping("/{id}/handle")
    @ApiOperation("处理举报")
    public Result<Void> handleReport(@PathVariable Long id,
                                     @RequestBody @Valid HandleReportDTO dto,
                                     HttpServletRequest request) {
        Long handlerId = getCurrentUserId(request);
        reportService.handleReport(id, dto, handlerId);
        return Result.success(null, "处理成功");
    }

    /**
     * 删除举报记录
     * 接口路径：DELETE /api/report/{id}
     * 是否需要登录：是（管理员）
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除举报记录")
    public Result<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return Result.success(null, "删除成功");
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
    }
}
