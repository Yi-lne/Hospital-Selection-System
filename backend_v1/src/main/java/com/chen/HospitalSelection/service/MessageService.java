package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.MessageSendDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.MessageUnreadVO;
import com.chen.HospitalSelection.vo.MessageVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;

/**
 * 私信服务接口
 * 提供用户私信功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface MessageService {

    /**
     * 获取会话列表（所有与我聊天的人及最新消息）
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<MessageVO> getConversations(Long userId);

    /**
     * 获取与指定用户的聊天记录
     *
     * @param currentUserId 当前用户ID
     * @param otherUserId   对方用户ID
     * @param dto           分页查询参数
     * @return 消息分页列表
     */
    PageResult<MessageVO> getMessageHistory(Long currentUserId, Long otherUserId, PageQueryDTO dto);

    /**
     * 发送私信
     *
     * @param senderId 发送者用户ID
     * @param dto      私信信息（接收者ID、内容）
     * @return 消息ID
     * @throws RuntimeException 当接收者不存在时抛出异常
     */
    Long sendMessage(Long senderId, MessageSendDTO dto);

    /**
     * 标记消息为已读
     *
     * @param messageId 消息ID
     * @param userId    当前用户ID（接收者）
     * @throws RuntimeException 当用户无权操作时抛出异常
     */
    void markAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     *
     * @param userId       当前用户ID
     * @param otherUserId  对方用户ID
     */
    void markAllAsRead(Long userId, Long otherUserId);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息统计信息
     */
    MessageUnreadVO getUnreadCount(Long userId);

    /**
     * 删除消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @throws RuntimeException 当用户无权操作时抛出异常
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * 清空与某用户的聊天记录
     *
     * @param currentUserId 当前用户ID
     * @param otherUserId   对方用户ID
     */
    void clearChatHistory(Long currentUserId, Long otherUserId);

    /**
     * 获取最近的聊天记录（用于首页展示）
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近的消息列表
     */
    List<MessageVO> getRecentMessages(Long userId, Integer limit);
}
