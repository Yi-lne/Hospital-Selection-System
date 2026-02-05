package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.MessageSendDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.MessageMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Message;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.vo.MessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 私信服务测试类
 *
 * @author chen
 */
public class MessageServiceTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.MessageServiceImpl messageService;

    private Message testMessage;
    private User testUser;
    private User testUser2;
    private static final Long TEST_MESSAGE_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final Long TEST_USER_ID_2 = 3L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试用户
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setNickname("用户1");
        testUser.setAvatar("avatar1.png");

        testUser2 = new User();
        testUser2.setId(TEST_USER_ID_2);
        testUser2.setNickname("用户2");
        testUser2.setAvatar("avatar2.png");

        // 创建测试私信
        testMessage = new Message();
        testMessage.setId(TEST_MESSAGE_ID);
        testMessage.setSenderId(TEST_USER_ID);
        testMessage.setReceiverId(TEST_USER_ID_2);
        testMessage.setContent("你好，请问高血压怎么治疗？");
        testMessage.setIsRead(0);
        testMessage.setIsDeleted(0);
        testMessage.setCreateTime(LocalDateTime.now());
        // 添加用户信息字段
        testMessage.setSenderNickname("用户1");
        testMessage.setSenderAvatar("avatar1.png");
        testMessage.setReceiverNickname("用户2");
        testMessage.setReceiverAvatar("avatar2.png");
    }

    @Test
    @DisplayName("测试发送私信 - 成功")
    public void testSendMessage_Success() {
        // Arrange
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(TEST_USER_ID_2);
        dto.setContent("你好");

        when(userMapper.selectById(TEST_USER_ID_2)).thenReturn(testUser2);
        when(messageMapper.insert(any(Message.class))).thenAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            msg.setId(100L);
            return null;
        });

        // Act
        Long result = messageService.sendMessage(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);

        verify(messageMapper, times(1)).insert(any(Message.class));
    }

    @Test
    @DisplayName("测试发送私信 - 接收者不存在")
    public void testSendMessage_ReceiverNotExists() {
        // Arrange
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(999L);
        dto.setContent("你好");

        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            messageService.sendMessage(TEST_USER_ID, dto);
        });

        assertEquals("接收者不存在", exception.getMessage());
        verify(messageMapper, never()).insert(any(Message.class));
    }

    @Test
    @DisplayName("测试发送私信 - 不能给自己发")
    public void testSendMessage_CannotSendToSelf() {
        // Arrange
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(TEST_USER_ID);
        dto.setContent("你好");

        // 需要先mock接收者存在，才能检查是否给自己发消息
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            messageService.sendMessage(TEST_USER_ID, dto);
        });

        assertEquals("不能给自己发消息", exception.getMessage());
        verify(messageMapper, never()).insert(any(Message.class));
    }

    @Test
    @DisplayName("测试获取会话列表 - 成功")
    public void testGetConversations_Success() {
        // Arrange
        when(messageMapper.selectBySenderWithUser(TEST_USER_ID)).thenReturn(Arrays.asList(testMessage));
        when(messageMapper.selectByReceiverWithUser(TEST_USER_ID)).thenReturn(Collections.emptyList());
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(userMapper.selectById(TEST_USER_ID_2)).thenReturn(testUser2);
        when(messageMapper.countUnreadByReceiverId(TEST_USER_ID)).thenReturn(0);
        when(messageMapper.countUnreadByReceiverId(TEST_USER_ID_2)).thenReturn(2);

        // Act
        List<MessageVO> result = messageService.getConversations(TEST_USER_ID);

        // Assert
        assertNotNull(result);

        verify(messageMapper, times(1)).selectBySenderWithUser(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试标记消息已读 - 成功")
    public void testMarkAsRead_Success() {
        // Arrange
        when(messageMapper.selectById(TEST_MESSAGE_ID)).thenReturn(testMessage);
        when(messageMapper.markAsRead(TEST_MESSAGE_ID)).thenReturn(1);

        // Act
        messageService.markAsRead(TEST_MESSAGE_ID, TEST_USER_ID_2);

        // Assert
        verify(messageMapper, times(1)).markAsRead(TEST_MESSAGE_ID);
    }

    @Test
    @DisplayName("测试批量标记已读 - 成功")
    public void testMarkAllAsRead_Success() {
        // Arrange
        when(messageMapper.markAllAsRead(TEST_USER_ID, TEST_USER_ID_2)).thenReturn(5);

        // Act
        messageService.markAllAsRead(TEST_USER_ID, TEST_USER_ID_2);

        // Assert
        verify(messageMapper, times(1)).markAllAsRead(TEST_USER_ID, TEST_USER_ID_2);
    }

    @Test
    @DisplayName("测试获取未读消息数 - 成功")
    public void testGetUnreadCount_Success() {
        // Arrange
        when(messageMapper.countUnreadByReceiverId(TEST_USER_ID)).thenReturn(10);

        // Act
        Integer result = messageService.getUnreadCount(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(Integer.valueOf(10), result);

        verify(messageMapper, times(1)).countUnreadByReceiverId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试删除消息 - 成功")
    public void testDeleteMessage_Success() {
        // Arrange
        when(messageMapper.selectById(TEST_MESSAGE_ID)).thenReturn(testMessage);
        when(messageMapper.deleteById(TEST_MESSAGE_ID)).thenReturn(1);

        // Act
        messageService.deleteMessage(TEST_MESSAGE_ID, TEST_USER_ID);

        // Assert
        verify(messageMapper, times(1)).deleteById(TEST_MESSAGE_ID);
    }

    @Test
    @DisplayName("测试删除消息 - 无权删除")
    public void testDeleteMessage_NoPermission() {
        // Arrange
        when(messageMapper.selectById(TEST_MESSAGE_ID)).thenReturn(testMessage);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            messageService.deleteMessage(TEST_MESSAGE_ID, 999L);
        });

        assertEquals("无权删除此消息", exception.getMessage());
        verify(messageMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试清空聊天记录 - 成功")
    public void testClearChatHistory_Success() {
        // Arrange
        when(messageMapper.deleteBetweenUsers(TEST_USER_ID, TEST_USER_ID_2)).thenReturn(10);

        // Act
        messageService.clearChatHistory(TEST_USER_ID, TEST_USER_ID_2);

        // Assert
        verify(messageMapper, times(1)).deleteBetweenUsers(TEST_USER_ID, TEST_USER_ID_2);
    }

    @Test
    @DisplayName("测试获取最近消息 - 成功")
    public void testGetRecentMessages_Success() {
        // Arrange
        // 实际实现使用selectUnreadByReceiverId和selectByReceiverId
        when(messageMapper.selectUnreadByReceiverId(TEST_USER_ID)).thenReturn(Collections.emptyList());
        when(messageMapper.selectByReceiverId(TEST_USER_ID)).thenReturn(Arrays.asList(testMessage));

        // Act
        List<MessageVO> result = messageService.getRecentMessages(TEST_USER_ID, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(messageMapper, times(1)).selectUnreadByReceiverId(TEST_USER_ID);
        verify(messageMapper, times(1)).selectByReceiverId(TEST_USER_ID);
    }
}