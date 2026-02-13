package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.HandleReportDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.ReportDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.*;
import com.chen.HospitalSelection.model.*;
import com.chen.HospitalSelection.service.NotificationService;
import com.chen.HospitalSelection.service.ReportService;
import com.chen.HospitalSelection.service.RoleService;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.ReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 举报服务实现类
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public Long createReport(Long userId, ReportDTO dto) {
        log.info("创建举报，用户ID：{}，对象类型：{}，对象ID：{}", userId, dto.getTargetType(), dto.getTargetId());

        // 检查举报对象是否存在
        if (dto.getTargetType() == 1) {
            // 话题
            Topic topic = topicMapper.selectById(dto.getTargetId());
            if (topic == null) {
                throw new BusinessException("话题不存在");
            }
        } else if (dto.getTargetType() == 2) {
            // 评论
            Comment comment = commentMapper.selectById(dto.getTargetId());
            if (comment == null) {
                throw new BusinessException("评论不存在");
            }
        }

        Report report = new Report();
        report.setUserId(userId);
        report.setTargetType(dto.getTargetType());
        report.setTargetId(dto.getTargetId());
        report.setReasonType(dto.getReasonType());
        report.setReason(dto.getReason());
        report.setStatus(0); // 待处理
        report.setCreateTime(LocalDateTime.now());

        reportMapper.insert(report);

        log.info("举报创建成功，举报ID：{}", report.getId());
        return report.getId();
    }

    @Override
    public PageResult<ReportVO> getReportList(Integer status, PageQueryDTO dto) {
        log.info("查询举报列表，状态：{}，页码：{}，每页大小：{}", status, dto.getPage(), dto.getPageSize());

        int offset = (dto.getPage() - 1) * dto.getPageSize();
        List<ReportVO> reportVOs = reportMapper.selectByPage(status, offset, dto.getPageSize());
        int total = reportMapper.countByStatus(status);

        return new PageResult<>((long) total, dto.getPage(), dto.getPageSize(), reportVOs);
    }

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public void handleReport(Long reportId, HandleReportDTO dto, Long handlerId) {
        log.info("处理举报，举报ID：{}，处理状态：{}", reportId, dto.getStatus());

        // 验证管理员权限
        if (!roleService.isAdmin(handlerId)) {
            throw new BusinessException("无权限执行此操作");
        }

        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }

        // 更新处理状态
        reportMapper.updateStatus(reportId, dto.getStatus(), dto.getHandleResult(), handlerId);

        log.info("举报处理完成，举报ID：{}", reportId);

        // 发送通知给举报者
        notificationService.createReportHandleNotification(
            reportId,
            report.getUserId(),
            dto.getStatus(),
            dto.getHandleResult()
        );
    }

    @Override
    public void deleteReport(Long reportId) {
        log.info("删除举报记录，举报ID：{}", reportId);
        reportMapper.deleteById(reportId);
    }

    /**
     * 转换为VO
     */
    private ReportVO convertToVO(Report report) {
        ReportVO vo = new ReportVO();
        vo.setId(report.getId());
        vo.setUserId(report.getUserId());
        vo.setTargetType(report.getTargetType());
        vo.setTargetId(report.getTargetId());
        vo.setReasonType(report.getReasonType());
        vo.setReasonTypeDesc(getReasonTypeDesc(report.getReasonType()));
        vo.setReason(report.getReason());
        vo.setStatus(report.getStatus());
        vo.setStatusDesc(getStatusDesc(report.getStatus()));
        vo.setHandleResult(report.getHandleResult());
        vo.setCreateTime(report.getCreateTime());

        // 获取举报者信息
        User user = userMapper.selectById(report.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 获取举报对象信息
        if (report.getTargetType() == 1) {
            vo.setTargetTypeName("话题");
            Topic topic = topicMapper.selectById(report.getTargetId());
            if (topic != null) {
                vo.setTargetTitle(topic.getTitle());
                vo.setTopicId(topic.getId());  // 话题类型，topicId 就是 targetId
            }
        } else if (report.getTargetType() == 2) {
            vo.setTargetTypeName("评论");
            Comment comment = commentMapper.selectById(report.getTargetId());
            if (comment != null) {
                String content = comment.getContent();
                if (content != null && content.length() > 50) {
                    content = content.substring(0, 50) + "...";
                }
                vo.setTargetTitle(content);
                vo.setTopicId(comment.getTopicId());  // 评论类型，获取所属话题ID
            }
        }

        return vo;
    }

    /**
     * 获取举报原因描述
     */
    private String getReasonTypeDesc(String reasonType) {
        switch (reasonType) {
            case "spam":
                return "垃圾信息";
            case "offensive":
                return "攻击辱骂";
            case "porn":
                return "色情低俗";
            case "violence":
                return "暴力血腥";
            case "rumor":
                return "谣言虚假";
            case "other":
                return "其他";
            default:
                return reasonType;
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0:
                return "待处理";
            case 1:
                return "已处理";
            case 2:
                return "已驳回";
            default:
                return "未知";
        }
    }
}
