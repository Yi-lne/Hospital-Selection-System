package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CaptchaVerifyDTO;
import com.chen.HospitalSelection.vo.CaptchaVO;

/**
 * 验证码服务接口
 *
 * @author chen
 * @since 2025-02-16
 */
public interface CaptchaService {

    /**
     * 生成验证码
     *
     * @return 验证码信息（包含Base64编码的图片）
     */
    CaptchaVO generateCaptcha();

    /**
     * 验证验证码
     *
     * @param dto 验证信息
     * @return 是否验证成功
     */
    boolean verifyCaptcha(CaptchaVerifyDTO dto);

    /**
     * 验证并消耗验证码（验证成功后删除，防止重复使用）
     *
     * @param dto 验证信息
     * @return 是否验证成功
     */
    boolean verifyAndConsumeCaptcha(CaptchaVerifyDTO dto);

    /**
     * 删除验证码
     *
     * @param captchaId 验证码ID
     */
    void deleteCaptcha(String captchaId);
}
