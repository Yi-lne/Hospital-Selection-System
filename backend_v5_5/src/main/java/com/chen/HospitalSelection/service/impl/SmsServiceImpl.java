package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务实现类（开发测试版本）
 *
 * 注意：这是一个模拟实现，仅用于开发测试
 * 生产环境需要接入真实的短信服务商（如阿里云、腾讯云等）
 *
 * @author chen
 * @since 2025-01-31
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    /**
     * 验证码缓存（生产环境应使用Redis）
     * Key: 手机号, Value: 验证码
     */
    private static final ConcurrentHashMap<String, String> CODE_CACHE = new ConcurrentHashMap<>();

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 定时任务线程池，用于清理过期验证码
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SmsServiceImpl() {
        // 每分钟清理一次过期验证码
        scheduler.scheduleAtFixedRate(this::cleanExpiredCodes, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public String sendVerificationCode(String phone) {
        log.info("发送验证码到手机：{}", phone);

        // 1. 验证手机号格式
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 2. 生成6位随机验证码
        String code = generateCode();

        // 3. 存储验证码（生产环境应该存储到Redis）
        CODE_CACHE.put(phone, code);

        // 4. 模拟发送短信（生产环境调用真实短信API）
        log.warn("【开发测试模式】验证码：{}，手机号：{}，有效期{}分钟",
                code, phone, CODE_EXPIRE_MINUTES);

        // TODO: 生产环境接入真实短信服务
        // 示例：阿里云短信
        // SendMessageRequest request = new SendMessageRequest();
        // request.setPhoneNumbers(phone);
        // request.setSignName("xxx");
        // request.setTemplateCode("SMS_xxxxx");
        // request.setTemplateParam("{\"code\":\"" + code + "\"}");
        // smsClient.sendMessage(request);

        return code; // 开发环境返回验证码方便测试，生产环境不应返回
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        log.info("验证验证码，手机号：{}", phone);

        // 从缓存获取验证码
        String cachedCode = CODE_CACHE.get(phone);

        if (cachedCode == null) {
            log.warn("验证码不存在或已过期，手机号：{}", phone);
            return false;
        }

        // 验证验证码
        boolean isValid = cachedCode.equals(code);

        if (isValid) {
            // 验证成功后删除验证码，防止重复使用
            CODE_CACHE.remove(phone);
            log.info("验证码验证成功，手机号：{}", phone);
        } else {
            log.warn("验证码错误，手机号：{}，期望：{}，实际：{}", phone, cachedCode, code);
        }

        return isValid;
    }

    /**
     * 生成随机验证码
     *
     * @return 验证码
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    /**
     * 清理过期验证码（这里简化处理，实际应该记录每个验证码的创建时间）
     * 生产环境使用Redis时，设置过期时间自动清理
     */
    private void cleanExpiredCodes() {
        // 简化处理：每次清空所有验证码
        // 实际应该只清理过期的
        if (!CODE_CACHE.isEmpty()) {
            log.debug("清理过期验证码，当前缓存数量：{}", CODE_CACHE.size());
            // CODE_CACHE.clear(); // 取消注释这行会在每分钟清空所有验证码
        }
    }
}
