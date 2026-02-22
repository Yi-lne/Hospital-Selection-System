package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.NotificationService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.NotificationVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通知接口
 * 基础路径：/api/notification
 */
@RestController
@RequestMapping("/notification")
@Api(tags = "通知管理")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取用户的通知列表
     * 接口路径：GET /api/notification/list
     * 是否需要登录：是
     *
     * @return 通知列表
     */
    @GetMapping("/list")
    @ApiOperation("获取通知列表")
    public Result<List<NotificationVO>> getNotifications(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<NotificationVO> notifications = notificationService.getUserNotifications(userId);
        return Result.success(notifications);
    }

    /**
     * 获取未读通知数量
     * 接口路径：GET /api/notification/unread/count
     * 是否需要登录：是
     *
     * @return 未读通知数量
     */
    @GetMapping("/unread/count")
    @ApiOperation("获取未读通知数量")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Integer unreadCount = notificationService.getUnreadCount(userId);
        return Result.success(unreadCount);
    }

    /**
     * 标记通知为已读
     * 接口路径：PUT /api/notification/read/{notificationId}
     * 是否需要登录：是
     *
     * @param notificationId 通知ID
     * @return 操作结果
     */
    @PutMapping("/read/{notificationId}")
    @ApiOperation("标记通知为已读")
    public Result<Void> markAsRead(@PathVariable Long notificationId,
                                  HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        notificationService.markAsRead(notificationId);
        return Result.success(null, "标记成功");
    }

    /**
     * 批量标记为已读
     * 接口路径：PUT /api/notification/read/all
     * 是否需要登录：是
     *
     * @return 操作结果
     */
    @PutMapping("/read/all")
    @ApiOperation("批量标记为已读")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        notificationService.markAllAsRead(userId);
        return Result.success(null, "标记成功");
    }

    /**
     * 删除通知
     * 接口路径：DELETE /api/notification/{notificationId}
     * 是否需要登录：是
     *
     * @param notificationId 通知ID
     * @return 操作结果
     */
    @DeleteMapping("/{notificationId}")
    @ApiOperation("删除通知")
    public Result<Void> deleteNotification(@PathVariable Long notificationId,
                                         HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        notificationService.deleteNotification(notificationId, userId);
        return Result.success(null, "删除成功");
    }

    /**
     * 删除所有已读通知
     * 接口路径：DELETE /api/notification/read/all
     * 是否需要登录：是
     *
     * @return 删除结果
     */
    @DeleteMapping("/read/all")
    @ApiOperation("删除所有已读通知")
    public Result<Void> deleteAllReadNotifications(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        int deletedCount = notificationService.deleteAllReadNotifications(userId);
        return Result.success(null, "已删除 " + deletedCount + " 条已读通知");
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
