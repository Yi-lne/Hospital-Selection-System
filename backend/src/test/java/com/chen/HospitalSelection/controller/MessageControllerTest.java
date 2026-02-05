package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.MessageSendDTO;
import com.chen.HospitalSelection.mapper.MessageMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Message;
import com.chen.HospitalSelection.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 私信控制器集成测试类
 *
 * @author chen
 */
@Transactional
public class MessageControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("测试发送私信 - 成功")
    public void testSendMessage_Success() throws Exception {
        // Arrange
        User receiver = userMapper.selectByPhone("13900000011");

        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(receiver.getId());
        dto.setContent("你好，这是一条测试消息");

        // 生成发送者的token
        User sender = userMapper.selectByPhone("13900000010");
        String token = generateTestToken(sender.getId(), sender.getPhone());

        // Act & Assert
        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("发送成功"));

        // 验证数据库中真的有消息了
        // Message message = messageMapper.selectByLatest();
        // assertNotNull(message);
        // assertEquals("你好，这是一条测试消息", message.getContent());
    }

    @Test
    @DisplayName("测试获取会话列表 - 成功")
    public void testGetConversations_Success() throws Exception {
        // Arrange - 先发送一条消息
        User receiver = userMapper.selectByPhone("13900000011");
        User sender = userMapper.selectByPhone("13900000010");

        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(receiver.getId());
        dto.setContent("测试消息");

        String token = generateTestToken(sender.getId(), sender.getPhone());

        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act & Assert
        mockMvc.perform(get("/message/conversations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试获取消息历史 - 成功")
    public void testGetMessageHistory_Success() throws Exception {
        // Arrange
        User receiver = userMapper.selectByPhone("13900000011");
        User sender = userMapper.selectByPhone("13900000010");

        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(receiver.getId());
        dto.setContent("测试消息");

        String token = generateTestToken(sender.getId(), sender.getPhone());

        // 先验证消息能发送成功
        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // Act & Assert
        mockMvc.perform(get("/message/history/" + receiver.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("测试标记已读 - 成功")
    public void testMarkAsRead_Success() throws Exception {
        // Arrange
        User receiver = userMapper.selectByPhone("13900000011");
        User sender = userMapper.selectByPhone("13900000010");

        // 发送一条消息（receiver发给sender的）
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(sender.getId());  // 发给sender
        dto.setContent("给发送者的消息");

        // 使用receiver的token发送消息
        String receiverToken = generateTestToken(receiver.getId(), receiver.getPhone());

        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + receiverToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act & Assert - 标记已读（sender标记接收到的消息为已读）
        String senderToken = generateTestToken(sender.getId(), sender.getPhone());
        mockMvc.perform(put("/message/read/1") // 假设消息ID为1
                        .header("Authorization", "Bearer " + senderToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试获取未读消息数 - 成功")
    public void testGetUnreadCount_Success() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000010");
        String token = generateTestToken(user.getId(), user.getPhone());

        // Act & Assert
        mockMvc.perform(get("/message/unread/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("测试发送私信 - 接收者不存在")
    public void testSendMessage_ReceiverNotFound() throws Exception {
        // Arrange
        User sender = userMapper.selectByPhone("13900000010");
        String token = generateTestToken(sender.getId(), sender.getPhone());

        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(99999L); // 不存在的用户
        dto.setContent("测试消息");

        // Act & Assert
        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404)) // 或适当的业务错误码
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("测试发送私信 - 不能发给自己")
    public void testSendMessage_CannotSendToSelf() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000010");
        String token = generateTestToken(user.getId(), user.getPhone());

        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(user.getId()); // 发给自己
        dto.setContent("测试消息");

        // Act & Assert
        mockMvc.perform(post("/message/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400)); // 业务错误
    }
}
