package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.UserBanDTO;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.service.AdminUserService;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户服务实现类
 *
 * @author chen
 * @since 2025-02-16
 */
@Slf4j
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<UserVO> getAllUsers(PageQueryDTO dto) {
        log.info("获取用户列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<User> users = userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<>(users);

        // 转换为VO
        List<UserVO> userVOList = new ArrayList<>();
        for (User user : users) {
            userVOList.add(convertToVO(user));
        }

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), userVOList);
    }

    @Override
    public List<UserVO> searchUsers(String keyword) {
        log.info("搜索用户，关键词：{}", keyword);

        List<User> users = userMapper.searchUsers(keyword);
        List<UserVO> userVOList = new ArrayList<>();

        for (User user : users) {
            userVOList.add(convertToVO(user));
        }

        return userVOList;
    }

    @Override
    @Transactional
    public void banUser(Long userId, UserBanDTO dto) {
        log.info("封禁用户，用户ID：{}，封禁类型：{}，原因：{}", userId, dto.getDurationType(), dto.getReason());

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被封禁");
        }

        // 计算封禁结束时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime banEndTime = calculateBanEndTime(dto.getDurationType());

        // 更新用户封禁信息
        user.setStatus(0);
        user.setBanStartTime(now);
        user.setBanEndTime(banEndTime);
        user.setBanReason(dto.getReason());
        user.setUpdateTime(now);
        userMapper.updateById(user);

        String durationText = getDurationText(dto.getDurationType());
        log.info("用户封禁成功，用户ID：{}，封禁至：{}，原因：{}", userId, banEndTime == null ? "永久" : banEndTime, dto.getReason());
    }

    /**
     * 计算封禁结束时间
     */
    private LocalDateTime calculateBanEndTime(String durationType) {
        LocalDateTime now = LocalDateTime.now();
        switch (durationType) {
            case "1_MINUTE":
                return now.plusMinutes(1);
            case "2_HOURS":
                return now.plusHours(2);
            case "1_DAY":
                return now.plusDays(1);
            case "7_DAYS":
                return now.plusDays(7);
            case "1_MONTH":
                return now.plusMonths(1);
            case "3_MONTHS":
                return now.plusMonths(3);
            case "PERMANENT":
                return null; // 永久封禁
            default:
                throw new RuntimeException("无效的封禁时长类型");
        }
    }

    /**
     * 获取封禁时长文本
     */
    private String getDurationText(String durationType) {
        switch (durationType) {
            case "1_MINUTE": return "1分钟";
            case "2_HOURS": return "2小时";
            case "1_DAY": return "1天";
            case "7_DAYS": return "7天";
            case "1_MONTH": return "1个月";
            case "3_MONTHS": return "3个月";
            case "PERMANENT": return "永久";
            default: return "";
        }
    }

    @Override
    @Transactional
    public void unbanUser(Long userId) {
        log.info("解封用户，用户ID：{}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 1) {
            throw new RuntimeException("用户状态正常");
        }

        // 更新用户状态并清除封禁信息
        user.setStatus(1);
        user.setBanStartTime(null);
        user.setBanEndTime(null);
        user.setBanReason(null);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户解封成功，用户ID：{}", userId);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置性别文本
        if (user.getGender() != null) {
            switch (user.getGender()) {
                case 1:
                    vo.setGenderText("男");
                    break;
                case 2:
                    vo.setGenderText("女");
                    break;
                default:
                    vo.setGenderText("未知");
                    break;
            }
        } else {
            vo.setGenderText("未知");
        }

        // 设置封禁时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (user.getBanStartTime() != null) {
            vo.setBanStartTime(user.getBanStartTime().format(formatter));
        }
        if (user.getBanEndTime() != null) {
            vo.setBanEndTime(user.getBanEndTime().format(formatter));
        }

        // 封禁原因直接复制
        vo.setBanReason(user.getBanReason());

        return vo;
    }
}
