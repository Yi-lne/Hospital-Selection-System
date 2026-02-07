package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 私信Mapper接口
 * 对应表：user_message
 */
@Mapper
public interface MessageMapper {

    /**
     * 根据ID查询私信
     * @param id 主键ID
     * @return 私信对象
     */
    Message selectById(@Param("id") Long id);

    /**
     * 查询用户发送的所有私信
     * @param senderId 发送者ID
     * @return 私信列表
     */
    List<Message> selectBySenderId(@Param("senderId") Long senderId);

    /**
     * 查询用户发送的所有私信（包含用户信息）
     * @param senderId 发送者ID
     * @return 私信列表
     */
    List<Message> selectBySenderWithUser(@Param("senderId") Long senderId);

    /**
     * 查询用户接收的所有私信
     * @param receiverId 接收者ID
     * @return 私信列表
     */
    List<Message> selectByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 查询用户接收的所有私信（包含用户信息）
     * @param receiverId 接收者ID
     * @return 私信列表
     */
    List<Message> selectByReceiverWithUser(@Param("receiverId") Long receiverId);

    /**
     * 查询两个用户之间的聊天记录（包含用户信息）
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 私信列表
     */
    List<Message> selectBetweenUsersWithUser(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 查询两个用户之间的聊天记录
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 私信列表
     */
    List<Message> selectBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 查询用户接收的未读私信
     * @param receiverId 接收者ID
     * @return 私信列表
     */
    List<Message> selectUnreadByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 查询用户发送的未读私信
     * @param senderId 发送者ID
     * @return 私信列表
     */
    List<Message> selectUnreadBySenderId(@Param("senderId") Long senderId);

    /**
     * 查询所有私信
     * @return 私信列表
     */
    List<Message> selectAll();

    /**
     * 查询最近的私信记录（分页）
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param limit 限制数量
     * @param offset 偏移量
     * @return 私信列表
     */
    List<Message> selectRecentMessages(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    /**
     * 插入私信
     * @param message 私信对象
     * @return 影响行数
     */
    int insert(Message message);

    /**
     * 更新私信内容
     * @param message 私信对象
     * @return 影响行数
     */
    int updateById(Message message);

    /**
     * 标记私信为已读
     * @param id 私信ID
     * @return 影响行数
     */
    int markAsRead(@Param("id") Long id);

    /**
     * 批量标记私信为已读
     * @param ids 私信ID列表
     * @return 影响行数
     */
    int batchMarkAsRead(@Param("ids") List<Long> ids);

    /**
     * 标记用户接收的所有私信为已读
     * @param receiverId 接收者ID
     * @param senderId 发送者ID（可选，指定发送者）
     * @return 影响行数
     */
    int markAllAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * 逻辑删除私信
     * @param id 私信ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除私信
     * @param ids 私信ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 删除两个用户之间的所有私信
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 影响行数
     */
    int deleteBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 统计私信总数
     * @return 私信总数
     */
    int count();

    /**
     * 统计用户发送的私信数量
     * @param senderId 发送者ID
     * @return 私信数量
     */
    int countBySenderId(@Param("senderId") Long senderId);

    /**
     * 统计用户接收的私信数量
     * @param receiverId 接收者ID
     * @return 私信数量
     */
    int countByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 统计用户接收的未读私信数量
     * @param receiverId 接收者ID
     * @return 未读私信数量
     */
    int countUnreadByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 统计两个用户之间的私信数量
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 私信数量
     */
    int countBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
