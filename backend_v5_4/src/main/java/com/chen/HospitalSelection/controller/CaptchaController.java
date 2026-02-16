package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.CaptchaVerifyDTO;
import com.chen.HospitalSelection.service.CaptchaService;
import com.chen.HospitalSelection.vo.CaptchaVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 验证码控制器
 *
 * @author chen
 * @since 2025-02-16
 */
@RestController
@RequestMapping("/captcha")
@Api(tags = "验证码管理")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成验证码
     * 接口路径：POST /api/captcha/generate
     * 是否需要登录：否
     *
     * @return 验证码信息
     */
    @PostMapping("/generate")
    @ApiOperation("生成验证码")
    public Result<CaptchaVO> generateCaptcha() {
        CaptchaVO captchaVO = captchaService.generateCaptcha();
        return Result.success(captchaVO, "验证码生成成功");
    }

    /**
     * 验证验证码（不删除，用于滑块验证）
     * 接口路径：POST /api/captcha/verify
     * 是否需要登录：否
     *
     * @param dto 验证信息
     * @return 验证结果
     */
    @PostMapping("/verify")
    @ApiOperation("验证验证码（不删除）")
    public Result<Boolean> verifyCaptcha(@RequestBody @Valid CaptchaVerifyDTO dto) {
        boolean valid = captchaService.verifyCaptcha(dto);
        return Result.success(valid, valid ? "验证成功" : "验证失败");
    }

    /**
     * 验证并消耗验证码（用于登录/注册）
     * 接口路径：POST /api/captcha/verify-consume
     * 是否需要登录：否
     *
     * @param dto 验证信息
     * @return 验证结果
     */
    @PostMapping("/verify-consume")
    @ApiOperation("验证并消耗验证码")
    public Result<Boolean> verifyAndConsumeCaptcha(@RequestBody @Valid CaptchaVerifyDTO dto) {
        boolean valid = captchaService.verifyAndConsumeCaptcha(dto);
        return Result.success(valid, valid ? "验证成功" : "验证失败");
    }
}
