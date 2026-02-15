package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.vo.NotificationVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 获取用户的通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<NotificationVO> getUserNotifications(Long userId);

    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     */
    void markAsRead(Long notificationId);

    /**
     * 批量标记为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     * @param notificationId 通知ID
     * @param userId 用户ID
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 创建话题评论通知
     * @param topicId 话题ID
     * @param topicAuthorId 话题作者ID
     * @param commenterName 评论者昵称
     */
    void createCommentNotification(Long topicId, Long topicAuthorId, String commenterName);

    /**
     * 创建评论回复通知
     * @param commentId 评论ID
     * @param commentAuthorId 评论作者ID
     * @param replierName 回复者昵称
     */
    void createReplyNotification(Long commentId, Long commentAuthorId, String replierName);

    /**
     * 创建话题删除通知
     * @param topicId 话题ID
     * @param topicAuthorId 话题作者ID
     * @param reason 删除原因
     */
    void createTopicDeleteNotification(Long topicId, Long topicAuthorId, String reason);

    /**
     * 创建评论删除通知
     * @param commentId 评论ID
     * @param commentAuthorId 评论作者ID
     * @param reason 删除原因
     */
    void createCommentDeleteNotification(Long commentId, Long commentAuthorId, String reason);

    /**
     * 创建点赞通知
     * @param targetId 目标ID（话题ID或评论ID）
     * @param targetAuthorId 目标作者ID
     * @param likerName 点赞者昵称
     * @param targetType 目标类型（1=话题，2=评论）
     */
    void createLikeNotification(Long targetId, Long targetAuthorId, String likerName, Integer targetType);

    /**
     * 创建收藏通知
     * @param topicId 话题ID
     * @param topicAuthorId 话题作者ID
     * @param collectorName 收藏者昵称
     */
    void createCollectNotification(Long topicId, Long topicAuthorId, String collectorName);

    /**
     * 创建举报处理结果通知
     * @param reportId 举报ID
     * @param reporterId 举报者ID
     * @param result 处理结果（1-通过，2-驳回）
     * @param reason 处理说明
     */
    void createReportHandleNotification(Long reportId, Long reporterId, Integer result, String reason);
}
