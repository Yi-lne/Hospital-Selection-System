package com.chen.HospitalSelection.task;

import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动解封定时任务
 * 定期检查并自动解封已过封禁期的用户
 *
 * @author chen
 * @since 2025-02-16
 */
@Slf4j
@Component
public class AutoUnbanTask {

    @Autowired
    private UserMapper userMapper;

    /**
     * 定时自动解封过期用户
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void autoUnbanExpiredUsers() {
        log.info("开始执行自动解封任务，时间：{}", LocalDateTime.now());

        try {
            // 查询所有封禁用户
            List<User> bannedUsers = userMapper.selectByStatus(0);

            if (bannedUsers == null || bannedUsers.isEmpty()) {
                log.info("当前没有封禁用户");
                return;
            }

            int unbanCount = 0;
            LocalDateTime now = LocalDateTime.now();

            for (User user : bannedUsers) {
                // 检查是否有过期用户（有封禁结束时间且已过期）
                if (user.getBanEndTime() != null && user.getBanEndTime().isBefore(now)) {
                    // 自动解封并清除封禁信息
                    user.setStatus(1);
                    user.setBanStartTime(null);
                    user.setBanEndTime(null);
                    user.setBanReason(null);
                    user.setUpdateTime(now);
                    userMapper.updateById(user);

                    unbanCount++;

                    log.info("自动解封用户，用户ID：{}，手机号：{}，封禁结束时间：{}",
                            user.getId(), user.getPhone(), user.getBanEndTime());
                }
            }

            log.info("自动解封任务执行完成，共解封用户：{} 个", unbanCount);

        } catch (Exception e) {
            log.error("自动解封任务执行失败", e);
        }
    }

    /**
     * 定时清理永久封禁用户的封禁信息（可选）
     * 每天凌晨2点执行
     * 此方法为示例，实际使用时可根据业务需求决定是否清理永久封禁信息
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanPermanentBanInfo() {
        log.info("开始清理永久封禁用户信息，时间：{}", LocalDateTime.now());

        try {
            // 查询所有封禁用户
            List<User> bannedUsers = userMapper.selectByStatus(0);

            if (bannedUsers == null || bannedUsers.isEmpty()) {
                log.info("当前没有封禁用户");
                return;
            }

            // 此方法可根据实际业务需求实现
            // 例如：清理很久以前被封禁且没有解封可能性的用户信息
            log.info("清理永久封禁用户信息任务完成");

        } catch (Exception e) {
            log.error("清理永久封禁用户信息任务失败", e);
        }
    }
}
