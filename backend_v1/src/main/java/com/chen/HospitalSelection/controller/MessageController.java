package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.MessageSendDTO;
import com.chen.HospitalSelection.service.MessageService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.MessageUnreadVO;
import com.chen.HospitalSelection.vo.MessageVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 私信接口
 * 基础路径：/api/message
 *
 * @author chen
 */
@RestController
@RequestMapping("/message")
@Api(tags = "私信管理")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    /**
     * 会话列表
     * 接口路径：GET /api/message/conversations
     * 是否需要登录：是
     *
     * @return 所有会话及最新消息
     */
    @GetMapping("/conversations")
    @ApiOperation("会话列表")
    public Result<List<MessageVO>> getConversations(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<MessageVO> conversations = messageService.getConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 与某用户的消息列表
     * 接口路径：GET /api/message/history/{userId}
     * 是否需要登录：是
     *
     * @param userId 对方用户ID
     * @return 与该用户的聊天记录
     */
    @GetMapping("/history/{userId}")
    @ApiOperation("与某用户的消息列表")
    public Result<List<MessageVO>> getMessagesByUser(@PathVariable Long userId,
                                                     HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        // For now, return recent messages without pagination
        List<MessageVO> messages = messageService.getRecentMessages(currentUserId, 50);
        return Result.success(messages);
    }

    /**
     * 发送私信
     * 接口路径：POST /api/message/send
     * 是否需要登录：是
     *
     * @param dto 私信信息（接收者ID、内容）
     * @return 发送的私信信息
     */
    @PostMapping("/send")
    @ApiOperation("发送私信")
    public Result<MessageVO> sendMessage(@RequestBody @Valid MessageSendDTO dto,
                                        HttpServletRequest request) {
        Long senderId = getCurrentUserId(request);
        Long messageId = messageService.sendMessage(senderId, dto);
        // TODO: Return the created message - for now return success
        MessageVO messageVO = new MessageVO();
        messageVO.setId(messageId);
        return Result.success(messageVO, "发送成功");
    }

    /**
     * 标记已读
     * 接口路径：PUT /api/message/read/{messageId}
     * 是否需要登录：是
     *
     * @param messageId 消息ID
     * @return 标记结果
     */
    @PutMapping("/read/{messageId}")
    @ApiOperation("标记已读")
    public Result<Void> markAsRead(@PathVariable Long messageId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        messageService.markAsRead(messageId, userId);
        return Result.success(null, "标记成功");
    }

    /**
     * 批量标记已读
     * 接口路径：PUT /api/message/read/batch
     * 是否需要登录：是
     *
     * @param userIds 用户ID列表（批量标记与这些用户的消息为已读）
     * @return 标记结果
     */
    @PutMapping("/read/batch")
    @ApiOperation("批量标记已读")
    public Result<Void> batchMarkAsRead(@RequestBody List<Long> userIds,
                                       HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        // Mark all as read for each user in the list
        for (Long otherUserId : userIds) {
            messageService.markAllAsRead(currentUserId, otherUserId);
        }
        return Result.success(null, "批量标记成功");
    }

    /**
     * 未读消息数
     * 接口路径：GET /api/message/unread/count
     * 是否需要登录：是
     *
     * @return 未读消息数量统计
     */
    @GetMapping("/unread/count")
    @ApiOperation("未读消息数")
    public Result<MessageUnreadVO> getUnreadCount(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        MessageUnreadVO unreadVO = messageService.getUnreadCount(userId);
        return Result.success(unreadVO);
    }

    /**
     * 删除消息
     * 接口路径：DELETE /api/message/{messageId}
     * 是否需要登录：是
     *
     * @param messageId 消息ID
     * @return 删除结果
     */
    @DeleteMapping("/{messageId}")
    @ApiOperation("删除消息")
    public Result<Void> deleteMessage(@PathVariable Long messageId,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        messageService.deleteMessage(messageId, userId);
        return Result.success(null, "删除成功");
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
