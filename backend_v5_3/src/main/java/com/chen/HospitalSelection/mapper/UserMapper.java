package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口
 * 对应表：sys_user
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User selectById(@Param("id") Long id);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 根据状态查询用户列表
     * @param status 状态（1=正常，0=禁用）
     * @return 用户列表
     */
    List<User> selectByStatus(@Param("status") Integer status);

    /**
     * 插入用户
     * @param user 用户对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响行数
     */
    int updateById(User user);

    /**
     * 更新用户头像
     * @param id 用户ID
     * @param avatar 头像URL
     * @return 影响行数
     */
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    /**
     * 更新用户密码
     * @param id 用户ID
     * @param password 新密码（加密后）
     * @return 影响行数
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 逻辑删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除用户
     * @param ids 用户ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 存在数量
     */
    int countByPhone(@Param("phone") String phone);

    /**
     * 根据昵称查询用户（排除指定用户ID）
     * @param nickname 昵称
     * @param excludeUserId 排除的用户ID
     * @return 用户对象
     */
    User selectByNicknameExcludeId(@Param("nickname") String nickname, @Param("excludeUserId") Long excludeUserId);
}
