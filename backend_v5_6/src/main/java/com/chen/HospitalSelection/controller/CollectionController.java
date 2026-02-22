package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.CollectionService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.CollectionVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 收藏接口
 * 基础路径：/api/collection
 *
 * @author chen
 */
@RestController
@RequestMapping("/collection")
@Api(tags = "收藏管理")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    /**
     * 添加收藏
     * 接口路径：POST /api/collection/add
     * 是否需要登录：是
     *
     * @param dto 收藏信息（收藏类型：1=医院，2=医生，3=话题；目标ID）
     * @return 收藏结果
     */
    @PostMapping("/add")
    @ApiOperation("添加收藏")
    public Result<Void> addCollection(@RequestBody @Valid CollectionDTO dto,
                                  HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        collectionService.addCollection(userId, dto);
        return Result.success(null, "收藏成功");
    }

    /**
     * 取消收藏
     * 接口路径：DELETE /api/collection/cancel
     * 是否需要登录：是
     *
     * @param dto 收藏信息（收藏类型、目标ID）
     * @return 取消收藏结果
     */
    @DeleteMapping("/cancel")
    @ApiOperation("取消收藏")
    public Result<Void> cancelCollection(@RequestBody @Valid CollectionDTO dto,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        collectionService.cancelCollection(userId, dto);
        return Result.success(null, "取消收藏成功");
    }

    /**
     * 收藏列表
     * 接口路径：GET /api/collection/list
     * 是否需要登录：是
     *
     * @param dto       分页查询参数
     * @param targetType 收藏类型（可选：1=医院，2=医生，3=话题）
     * @return 我的收藏列表
     */
    @GetMapping("/list")
    @ApiOperation("收藏列表")
    public Result<PageResult<CollectionVO>> getCollectionList(@Valid PageQueryDTO dto,
                                                               @RequestParam(required = false) Integer targetType,
                                                               HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<CollectionVO> pageResult = collectionService.getCollectionList(userId, targetType, dto);
        return Result.success(pageResult);
    }

    /**
     * 检查是否已收藏
     * 接口路径：GET /api/collection/check
     * 是否需要登录：是
     *
     * @param targetType 收藏类型（1=医院，2=医生，3=话题）
     * @param targetId   目标ID
     * @return 是否已收藏
     */
    @GetMapping("/check")
    @ApiOperation("检查是否已收藏")
    public Result<Boolean> checkCollection(@RequestParam Integer targetType,
                                           @RequestParam Long targetId,
                                           HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Boolean isCollected = collectionService.checkCollection(userId, targetType, targetId);
        return Result.success(isCollected);
    }

    /**
     * 收藏数量统计
     * 接口路径：GET /api/collection/count
     * 是否需要登录：是
     *
     * @return 各类型收藏数量统计
     */
    @GetMapping("/count")
    @ApiOperation("收藏数量统计")
    public Result<Map<String, Long>> getCollectionCount(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Map<Integer, Long> countMap = collectionService.getCollectionCount(userId);

        // 转换为字符串key的Map
        Map<String, Long> resultMap = new java.util.HashMap<>();
        if (countMap != null) {
            countMap.forEach((key, value) -> {
                String typeName = getTargetTypeName(key);
                resultMap.put(typeName, value);
            });
        }

        return Result.success(resultMap);
    }

    /**
     * 获取当前登录用户ID
     * 优先从SecurityContext获取，其次从JWT token解析
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            // 1. 优先从SecurityContext获取（JWT过滤器已设置）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long) {
                return (Long) authentication.getPrincipal();
            }

            // 2. 如果SecurityContext中没有，尝试从JWT token解析
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer "
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    return userId;
                }
            }

            // 3. 都获取不到，抛出异常
            throw new RuntimeException("用户未登录");
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
    }

    /**
     * 获取收藏类型名称
     *
     * @param targetType 类型编号
     * @return 类型名称
     */
    private String getTargetTypeName(Integer targetType) {
        if (targetType == null) {
            return "未知";
        }
        switch (targetType) {
            case 1:
                return "医院";
            case 2:
                return "医生";
            case 3:
                return "话题";
            default:
                return "未知";
        }
    }
}
