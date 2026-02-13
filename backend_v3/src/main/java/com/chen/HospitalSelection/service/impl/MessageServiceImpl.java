package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.MessageSendDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.exception.ParameterException;
import com.chen.HospitalSelection.exception.ResourceNotFoundException;
import com.chen.HospitalSelection.mapper.MessageMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Message;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.service.MessageService;
import com.chen.HospitalSelection.vo.MessageVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 私信服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<MessageVO> getConversations(Long userId) {
        log.info("获取会话列表，用户ID：{}", userId);

        // 查询我发送和接收的所有消息（包含用户信息，避免N+1查询）
        List<Message> sentMessages = messageMapper.selectBySenderWithUser(userId);
        List<Message> receivedMessages = messageMapper.selectByReceiverWithUser(userId);

        // 合并消息
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);

        // 按对方用户ID分组，获取每个会话的最新消息
        // 这里简化处理，按对方用户分组，取每组最新的一条

        return allMessages.stream()
                .collect(Collectors.groupingBy(message -> {
                    // 确定对方用户ID
                    return message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId();
                }))
                .entrySet()
                .stream()
                .map(entry -> {
                    // 获取该会话的最新消息
                    List<Message> messages = entry.getValue();
                    Message message = messages.stream()
                            .max((m1, m2) -> m1.getCreateTime().compareTo(m2.getCreateTime()))
                            .orElse(null);

                    if (message == null) return null;

                    MessageVO vo = new MessageVO();
                    BeanUtils.copyProperties(message, vo);

                    // 设置对方用户信息（优先使用已加载的用户信息）
                    Long otherUserId = entry.getKey();

                    // 判断对方是发送者还是接收者
                    if (message.getSenderId().equals(otherUserId)) {
                        // 对方是发送者
                        if (message.getSenderNickname() != null) {
                            vo.setSenderNickname(message.getSenderNickname());
                            vo.setSenderAvatar(message.getSenderAvatar());
                        } else {
                            // 向后兼容：如果没有加载用户信息，则查询数据库
                            User otherUser = userMapper.selectById(otherUserId);
                            if (otherUser != null) {
                                vo.setSenderNickname(otherUser.getNickname());
                                vo.setSenderAvatar(otherUser.getAvatar());
                            }
                        }
                    } else {
                        // 对方是接收者
                        if (message.getReceiverNickname() != null) {
                            vo.setReceiverNickname(message.getReceiverNickname());
                            vo.setReceiverAvatar(message.getReceiverAvatar());
                        } else {
                            // 向后兼容：如果没有加载用户信息，则查询数据库
                            User otherUser = userMapper.selectById(otherUserId);
                            if (otherUser != null) {
                                vo.setReceiverNickname(otherUser.getNickname());
                                vo.setReceiverAvatar(otherUser.getAvatar());
                            }
                        }
                    }

                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<MessageVO> getMessageHistory(Long currentUserId, Long otherUserId, PageQueryDTO dto) {
        log.info("获取聊天记录，当前用户ID：{}，对方用户ID：{}", currentUserId, otherUserId);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Message> messageList = messageMapper.selectBetweenUsersWithUser(currentUserId, otherUserId);
        PageInfo<Message> pageInfo = new PageInfo<>(messageList);

        List<MessageVO> voList = messageList.stream()
                .map(message -> {
                    MessageVO vo = new MessageVO();
                    BeanUtils.copyProperties(message, vo);
                    vo.setIsSelf(message.getSenderId().equals(currentUserId));
                    // 用户信息已经通过JOIN查询加载，无需再次查询
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    @Transactional
    public Long sendMessage(Long senderId, MessageSendDTO dto) {
        log.info("发送私信，发送者ID：{}，接收者ID：{}", senderId, dto.getReceiverId());

        // 检查接收者是否存在
        User receiver = userMapper.selectById(dto.getReceiverId());
        if (receiver == null) {
            throw new ResourceNotFoundException("接收者不存在");
        }

        // 不能给自己发消息
        if (senderId.equals(dto.getReceiverId())) {
            throw new ParameterException("不能给自己发消息");
        }

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setIsRead(0);
        message.setIsDeleted(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);

        log.info("私信发送成功，消息ID：{}", message.getId());
        return message.getId();
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        log.info("标记消息为已读，消息ID：{}，用户ID：{}", messageId, userId);

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw ResourceNotFoundException.messageNotFound();
        }

        // 只有接收者可以标记已读
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权操作此消息");
        }

        messageMapper.markAsRead(messageId);

        log.info("消息标记为已读，消息ID：{}", messageId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId, Long otherUserId) {
        log.info("批量标记消息为已读，用户ID：{}，对方用户ID：{}", userId, otherUserId);

        // 标记对方发给我的所有消息为已读
        messageMapper.markAllAsRead(userId, otherUserId);

        log.info("批量标记消息为已读成功");
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        log.info("获取未读消息数量，用户ID：{}", userId);

        return messageMapper.countUnreadByReceiverId(userId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        log.info("删除消息，消息ID：{}，用户ID：{}", messageId, userId);

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw ResourceNotFoundException.messageNotFound();
        }

        // 只有发送者或接收者可以删除消息
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权删除此消息");
        }

        messageMapper.deleteById(messageId);

        log.info("消息删除成功，消息ID：{}", messageId);
    }

    @Override
    @Transactional
    public void clearChatHistory(Long currentUserId, Long otherUserId) {
        log.info("清空聊天记录，当前用户ID：{}，对方用户ID：{}", currentUserId, otherUserId);

        messageMapper.deleteBetweenUsers(currentUserId, otherUserId);

        log.info("聊天记录清空成功");
    }

    @Override
    public List<MessageVO> getRecentMessages(Long userId, Integer limit) {
        log.info("获取最近聊天记录，用户ID：{}，限制数量：{}", userId, limit);

        // 获取最近的接收消息
        List<Message> receivedMessages = messageMapper.selectUnreadByReceiverId(userId);

        // 如果未读消息不足limit条，再获取一些已读消息
        if (receivedMessages.size() < limit) {
            List<Message> allReceived = messageMapper.selectByReceiverId(userId);
            // 简化实现：直接返回前limit条
            receivedMessages = allReceived.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return receivedMessages.stream()
                .map(message -> {
                    MessageVO vo = new MessageVO();
                    BeanUtils.copyProperties(message, vo);
                    vo.setIsSelf(message.getSenderId().equals(userId));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
