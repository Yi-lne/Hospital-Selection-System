package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.QueryHistoryService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.QueryHistoryVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 查询历史接口
 * 基础路径：/api/query-history
 *
 * @author chen
 */
@RestController
@RequestMapping("/query-history")
@Api(tags = "查询历史管理")
public class QueryHistoryController {

    @Autowired
    private QueryHistoryService queryHistoryService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    /**
     * 查询历史列表
     * 接口路径：GET /api/query-history/list
     * 是否需要登录：是
     *
     * @param dto 分页查询参数
     * @return 查询浏览历史列表
     */
    @GetMapping("/list")
    @ApiOperation("查询历史列表")
    public Result<PageResult<QueryHistoryVO>> getQueryHistoryList(@Valid PageQueryDTO dto,
                                                                  HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<QueryHistoryVO> pageResult = queryHistoryService.getQueryHistoryList(userId, dto);
        return Result.success(pageResult);
    }

    /**
     * 记录查询
     * 接口路径：POST /api/query-history/record
     * 是否需要登录：是
     *
     * @param recordData 查询记录数据（查询类型、目标ID、查询参数等）
     * @return 记录结果
     */
    @PostMapping("/record")
    @ApiOperation("记录查询")
    public Result<Void> recordQuery(@RequestBody Map<String, Object> recordData,
                                    HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        // Extract parameters from recordData
        Integer queryType = (Integer) recordData.get("queryType");
        Long targetId = recordData.get("targetId") != null ?
            Long.valueOf(recordData.get("targetId").toString()) : null;
        String queryParams = recordData.get("queryParams") != null ?
            recordData.get("queryParams").toString() : null;
        queryHistoryService.recordQuery(userId, queryType, targetId, queryParams);
        return Result.success(null, "记录成功");
    }

    /**
     * 删除历史记录
     * 接口路径：DELETE /api/query-history/{id}
     * 是否需要登录：是
     *
     * @param id 历史记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除历史记录")
    public Result<Void> deleteQueryHistory(@PathVariable Long id,
                                          HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        queryHistoryService.deleteQueryHistory(id, userId);
        return Result.success(null, "删除成功");
    }

    /**
     * 清空历史
     * 接口路径：DELETE /api/query-history/clear
     * 是否需要登录：是
     *
     * @return 清空结果
     */
    @DeleteMapping("/clear")
    @ApiOperation("清空历史")
    public Result<Void> clearQueryHistory(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        queryHistoryService.clearQueryHistory(userId);
        return Result.success(null, "清空成功");
    }

    /**
     * 获取当前登录用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer "
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
