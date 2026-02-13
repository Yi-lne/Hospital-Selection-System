package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper接口
 * 对应表：user_notification
 */
@Mapper
public interface NotificationMapper {

    /**
     * 根据用户ID查询通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<Notification> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询未读通知列表
     * @param userId 用户ID
     * @return 未读通知列表
     */
    List<Notification> selectUnreadByUserId(@Param("userId") Long userId);

    /**
     * 统计未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnread(@Param("userId") Long userId);

    /**
     * 插入通知
     * @param notification 通知对象
     * @return 影响行数
     */
    int insert(Notification notification);

    /**
     * 标记为已读
     * @param id 通知ID
     * @return 影响行数
     */
    int markAsRead(@Param("id") Long id);

    /**
     * 批量标记为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    int batchMarkAsRead(@Param("userId") Long userId);

    /**
     * 删除通知
     * @param id 通知ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询通知
     * @param id 通知ID
     * @return 通知对象
     */
    Notification selectById(@Param("id") Long id);
}
