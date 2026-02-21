package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.UserBanDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.UserVO;

import java.util.List;

/**
 * 管理员用户服务接口
 *
 * @author chen
 * @since 2025-02-16
 */
public interface AdminUserService {

    /**
     * 获取所有用户（分页）
     *
     * @param dto 分页查询参数
     * @return 用户分页列表
     */
    PageResult<UserVO> getAllUsers(PageQueryDTO dto);

    /**
     * 搜索用户
     *
     * @param keyword 搜索关键词（手机号或昵称）
     * @return 搜索结果
     */
    List<UserVO> searchUsers(String keyword);

    /**
     * 封禁用户
     *
     * @param userId 用户ID
     * @param dto 封禁参数（时长、原因）
     */
    void banUser(Long userId, UserBanDTO dto);

    /**
     * 解封用户
     *
     * @param userId 用户ID
     */
    void unbanUser(Long userId);
}
