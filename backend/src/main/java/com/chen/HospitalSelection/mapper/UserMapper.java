package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.dto.PageQueryDTO;
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
     * 根据昵称查询用户（排除指定用户ID）
     * @param nickname 昵称
     * @param excludeUserId 排除的用户ID
     * @return 用户对象
     */
    User selectByNicknameExcludeId(@Param("nickname") String nickname, @Param("excludeUserId") Long excludeUserId);

    /**
     * 搜索用户（根据手机号或昵称）
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    List<User> searchUsers(@Param("keyword") String keyword);

    List<User> selectByStatus(int status);
}
