package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.PasswordResetDTO;
import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.UserVO;
import com.chen.HospitalSelection.vo.UserProfileVO;

/**
 * 用户服务接口
 * 提供用户注册、登录、信息管理等功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册信息（手机号、密码、昵称）
     * @return 注册成功的用户信息
     * @throws RuntimeException 当手机号已存在时抛出异常
     */
    UserVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息（手机号、密码）
     * @return 登录成功的用户信息（包含Token）
     * @throws RuntimeException 当手机号或密码错误时抛出异常
     */
    UserVO login(UserLoginDTO dto);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户基本信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 获取用户完整资料
     *
     * @param userId 用户ID
     * @return 用户完整资料信息
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新信息（昵称、头像、性别等）
     * @return 更新后的用户信息
     */
    UserVO updateUserInfo(Long userId, UserUpdateDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    密码信息（原密码、新密码）
     * @throws RuntimeException 当原密码错误时抛出异常
     */
    void updatePassword(Long userId, PasswordUpdateDTO dto);

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 验证码（开发环境返回，生产环境返回null或空字符串）
     */
    String sendVerificationCode(String phone);

    /**
     * 找回密码（通过手机验证码重置密码）
     *
     * @param dto 找回密码信息（手机号、验证码、新密码）
     * @throws RuntimeException 当手机号不存在或验证码错误时抛出异常
     */
    void resetPassword(PasswordResetDTO dto);

    /**
     * 分页查询用户列表（管理员功能）
     *
     * @param dto 分页查询参数
     * @return 用户分页列表
     */
    PageResult<UserVO> getUserList(PageQueryDTO dto);

    /**
     * 启用/禁用用户（管理员功能）
     *
     * @param userId 用户ID
     * @param status 状态（1=启用，0=禁用）
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 上传用户头像
     *
     * @param userId  用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户信息
     */
    UserVO uploadAvatar(Long userId, String avatarUrl);
}
