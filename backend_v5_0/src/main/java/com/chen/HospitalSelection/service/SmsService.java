package com.chen.HospitalSelection.service;

/**
 * 短信验证码服务接口
 *
 * @author chen
 * @since 2025-01-31
 */
public interface SmsService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 验证码（用于开发测试，生产环境不返回）
     */
    String sendVerificationCode(String phone);

    /**
     * 验证验证码
     *
     * @param phone   手机号
     * @param code    验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code);
}
