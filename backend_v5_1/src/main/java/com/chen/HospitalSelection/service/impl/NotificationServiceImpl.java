package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.NotificationMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.mapper.CommentMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.mapper.ReportMapper;
import com.chen.HospitalSelection.model.Notification;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.Comment;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.model.Report;
import com.chen.HospitalSelection.service.NotificationService;
import com.chen.HospitalSelection.vo.NotificationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public List<NotificationVO> getUserNotifications(Long userId) {
        log.info("获取用户通知列表，用户ID：{}", userId);
        List<Notification> notifications = notificationMapper.selectByUserId(userId);
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        log.info("获取未读通知数量，用户ID：{}", userId);
        Integer count = notificationMapper.countUnread(userId);
        log.info("用户 {} 的未读通知数量：{}", userId, count);
        return count;
    }

    @Override
    public void markAsRead(Long notificationId) {
        log.info("标记通知为已读，通知ID：{}", notificationId);
        notificationMapper.markAsRead(notificationId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        log.info("批量标记通知为已读，用户ID：{}", userId);
        notificationMapper.batchMarkAsRead(userId);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("删除通知，通知ID：{}，用户ID：{}", notificationId, userId);
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public void createCommentNotification(Long topicId, Long topicAuthorId, String commenterName) {
        // 不给自己发送通知
        // commenterName 是评论者昵称，需要从当前登录用户获取
        log.info("========== 创建话题评论通知 ==========");
        log.info("话题ID：{}，话题作者ID：{}，评论者：{}", topicId, topicAuthorId, commenterName);

        Notification notification = new Notification();
        notification.setUserId(topicAuthorId);
        notification.setType("comment");
        notification.setRelatedId(topicId);
        notification.setContent(commenterName + " 评论了");
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("话题评论通知创建成功，通知ID：{}，接收者ID：{}", notification.getId(), topicAuthorId);
        log.info("========================================");
    }

    @Override
    public void createReplyNotification(Long commentId, Long commentAuthorId, String replierName) {
        log.info("========== 创建评论回复通知 ==========");
        log.info("评论ID：{}，评论作者ID：{}，回复者：{}", commentId, commentAuthorId, replierName);

        // 获取评论信息，获取话题ID
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            log.error("评论不存在，无法创建回复通知，评论ID：{}", commentId);
            return;
        }

        // 存储话题ID而不是评论ID，方便前端跳转
        Long topicId = comment.getTopicId();
        log.info("被回复的评论所在话题ID：{}", topicId);
        log.info("被回复的评论内容：{}", comment.getContent());

        Notification notification = new Notification();
        notification.setUserId(commentAuthorId);
        notification.setType("reply");
        notification.setRelatedId(topicId);  // 存储话题ID
        notification.setContent(replierName + " 回复了");
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("评论回复通知创建成功，通知接收者ID：{}，跳转话题ID：{}", commentAuthorId, topicId);
        log.info("========================================");
    }

    @Override
    public void createTopicDeleteNotification(Long topicId, Long topicAuthorId, String reason) {
        log.info("创建话题删除通知，话题ID：{}，话题作者ID：{}", topicId, topicAuthorId);

        // 获取话题标题
        Topic topic = topicMapper.selectById(topicId);
        String topicTitle = topic != null ? topic.getTitle() : "该话题";
        String content = "你发布的话题「" + topicTitle + "」已被管理员删除";
        if (reason != null && !reason.isEmpty()) {
            content += "，原因：" + reason;
        }

        Notification notification = new Notification();
        notification.setUserId(topicAuthorId);
        notification.setType("delete_topic");
        notification.setRelatedId(topicId);
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("话题删除通知创建成功");
    }

    @Override
    public void createCommentDeleteNotification(Long commentId, Long commentAuthorId, String reason) {
        log.info("创建评论删除通知，评论ID：{}，评论作者ID：{}", commentId, commentAuthorId);

        // 获取评论信息
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            log.error("评论不存在，无法创建删除通知，评论ID：{}", commentId);
            return;
        }

        // 获取评论内容（截取前20个字）
        String commentPreview = comment.getContent();
        if (commentPreview.length() > 20) {
            commentPreview = commentPreview.substring(0, 20) + "...";
        }

        String content = "你的评论「" + commentPreview + "」已被管理员删除";
        if (reason != null && !reason.isEmpty()) {
            content += "，原因：" + reason;
        }

        // 存储话题ID而不是评论ID，方便前端跳转
        Long topicId = comment.getTopicId();

        Notification notification = new Notification();
        notification.setUserId(commentAuthorId);
        notification.setType("delete_comment");
        notification.setRelatedId(topicId);  // 存储话题ID
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("评论删除通知创建成功，话题ID：{}", topicId);
    }

    /**
     * 转换为VO
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setType(notification.getType());
        vo.setTypeDesc(getTypeDesc(notification.getType()));
        vo.setRelatedId(notification.getRelatedId());
        vo.setContent(notification.getContent());
        vo.setIsRead(notification.getIsRead() == 1);
        vo.setCreateTime(notification.getCreateTime());

        // 设置话题ID（relatedId 存储的就是话题ID）
        vo.setTopicId(notification.getRelatedId());

        // 获取话题标题（如果有关联的话题）
        if (notification.getRelatedId() != null) {
            Topic topic = topicMapper.selectById(notification.getRelatedId());
            if (topic != null) {
                vo.setTopicTitle(topic.getTitle());
            }
        }

        return vo;
    }

    /**
     * 获取通知类型描述
     */
    private String getTypeDesc(String type) {
        switch (type) {
            case "comment":
                return "话题评论";
            case "reply":
                return "评论回复";
            case "delete_topic":
                return "话题删除";
            case "delete_comment":
                return "评论删除";
            case "report_handle":
                return "举报处理";
            case "like":
                return "收到点赞";
            case "collect":
                return "收到收藏";
            default:
                return "系统通知";
        }
    }

    /**
     * 创建举报处理结果通知
     * @param reportId 举报ID
     * @param reporterId 举报者ID
     * @param result 处理结果（1-通过，2-驳回）
     * @param reason 处理说明
     */
    @Override
    public void createReportHandleNotification(Long reportId, Long reporterId, Integer result, String reason) {
        log.info("创建举报处理通知，举报ID：{}，举报者ID：{}，处理结果：{}", reportId, reporterId, result);

        // 获取举报信息
        Report report = reportMapper.selectById(reportId);
        if (report == null || reporterId == null) {
            log.error("举报记录不存在或举报者不存在，无法创建通知");
            return;
        }

        // 获取举报者信息
        User reporter = userMapper.selectById(reporterId);
        if (reporter == null) {
            log.error("举报者不存在，无法创建通知");
            return;
        }

        // 构建通知内容
        String content;
        if (result == 1) {
            // 通过
            content = String.format("你提交的举报已被管理员审核通过", getReportTargetDescription(report));
            if (reason != null && !reason.isEmpty()) {
                content += "，说明：" + reason;
            }
        } else if (result == 2) {
            // 驳回
            content = String.format("你提交的举报已被管理员驳回", getReportTargetDescription(report));
            if (reason != null && !reason.isEmpty()) {
                content += "，说明：" + reason;
            }
        } else {
            log.error("未知的处理结果：{}", result);
            return;
        }

        // 创建通知
        Notification notification = new Notification();
        notification.setUserId(reporterId);
        notification.setType("report_handle");
        // 设置related_id为话题ID，方便用户点击通知跳转
        if (report.getTargetType() == 1) {
            notification.setRelatedId(report.getTargetId());  // 话题举报，related_id = topicId
        } else if (report.getTargetType() == 2) {
            Comment comment = commentMapper.selectById(report.getTargetId());
            notification.setRelatedId(comment != null ? comment.getTopicId() : report.getTargetId());  // 评论举报，获取所属话题ID
        } else {
            notification.setRelatedId(report.getTargetId());
        }
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("举报处理通知创建成功，通知接收者ID：{}，举报ID：{}", reporterId, reportId);
    }

    /**
     * 创建点赞通知
     * @param targetId 目标ID（话题ID或评论ID）
     * @param targetAuthorId 目标作者ID
     * @param likerName 点赞者昵称
     * @param targetType 目标类型（1=话题，2=评论）
     */
    @Override
    public void createLikeNotification(Long targetId, Long targetAuthorId, String likerName, Integer targetType) {
        log.info("创建点赞通知，目标ID：{}，目标作者ID：{}，点赞者：{}，类型：{}", targetId, targetAuthorId, likerName, targetType);

        // 获取话题ID（用于前端跳转）
        Long topicId = targetId;
        if (targetType == 2) {
            // 评论点赞，获取所属话题ID
            Comment comment = commentMapper.selectById(targetId);
            if (comment != null) {
                topicId = comment.getTopicId();
            }
        }

        String content;
        if (targetType == 1) {
            content = likerName + " 赞了";
        } else {
            content = likerName + " 赞了你的评论";
        }

        Notification notification = new Notification();
        notification.setUserId(targetAuthorId);
        notification.setType("like");
        notification.setRelatedId(topicId);  // 存储话题ID用于跳转
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("点赞通知创建成功，通知接收者ID：{}，跳转话题ID：{}", targetAuthorId, topicId);
    }

    /**
     * 创建收藏通知
     * @param topicId 话题ID
     * @param topicAuthorId 话题作者ID
     * @param collectorName 收藏者昵称
     */
    @Override
    public void createCollectNotification(Long topicId, Long topicAuthorId, String collectorName) {
        log.info("创建收藏通知，话题ID：{}，话题作者ID：{}，收藏者：{}", topicId, topicAuthorId, collectorName);

        String content = collectorName + " 收藏了";

        Notification notification = new Notification();
        notification.setUserId(topicAuthorId);
        notification.setType("collect");
        notification.setRelatedId(topicId);  // 存储话题ID用于跳转
        notification.setContent(content);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());

        notificationMapper.insert(notification);
        log.info("收藏通知创建成功，通知接收者ID：{}，跳转话题ID：{}", topicAuthorId, topicId);
    }

    /**
     * 获取举报目标描述（用于通知内容）
     * @param report 举报信息
     * @return 目标描述
     */
    private String getReportTargetDescription(Report report) {
        if (report.getTargetType() == 1) {
            // 话题
            Topic topic = topicMapper.selectById(report.getTargetId());
            return "话题「" + (topic != null ? topic.getTitle() : "已删除") + "」";
        } else if (report.getTargetType() == 2) {
            // 评论
            Comment comment = commentMapper.selectById(report.getTargetId());
            String content = comment != null ? comment.getContent() : "已删除";
            // 截断长评论
            if (content != null && content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            return "评论「" + content + "」";
        } else {
            return "举报对象";
        }
    }
}
