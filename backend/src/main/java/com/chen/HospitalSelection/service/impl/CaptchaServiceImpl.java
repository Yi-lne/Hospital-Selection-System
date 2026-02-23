package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.CaptchaVerifyDTO;
import com.chen.HospitalSelection.service.CaptchaService;
import com.chen.HospitalSelection.util.CaptchaUtil;
import com.chen.HospitalSelection.vo.CaptchaVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 *
 * @author chen
 * @since 2025-02-16
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    /**
     * 验证码存储（captchaId -> 验证码信息）
     */
    private final ConcurrentHashMap<String, CaptchaUtil.CaptchaInfo> captchaStore = new ConcurrentHashMap<>();

    /**
     * 定时清理过期验证码的线程池
     */
    private ScheduledExecutorService scheduler;

    /**
     * 验证码过期时间（毫秒）
     */
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟

    @PostConstruct
    public void init() {
        // 启动定时任务，每分钟清理过期验证码
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::cleanExpiredCaptchas, 1, 1, TimeUnit.MINUTES);
        log.info("验证码服务初始化完成，已启动定时清理任务");
    }

    @PreDestroy
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        log.info("验证码服务已关闭");
    }

    @Override
    public CaptchaVO generateCaptcha() {
        // 生成验证码
        CaptchaUtil.CaptchaInfo captchaInfo = CaptchaUtil.generateCaptcha();

        // 存储验证码信息
        captchaStore.put(captchaInfo.getCaptchaId(), captchaInfo);

        // 构建返回结果
        CaptchaVO captchaVO = CaptchaVO.builder()
                .captchaId(captchaInfo.getCaptchaId())
                .backgroundImage(captchaInfo.getBackgroundImage())
                .sliderImage(captchaInfo.getSliderImage())
                .sliderY(captchaInfo.getTargetY())
                .build();

        log.info("生成滑块验证码成功，ID：{}", captchaInfo.getCaptchaId());
        return captchaVO;
    }

    @Override
    public boolean verifyCaptcha(CaptchaVerifyDTO dto) {
        CaptchaUtil.CaptchaInfo captchaInfo = captchaStore.get(dto.getCaptchaId());

        if (captchaInfo == null) {
            log.warn("验证码不存在或已过期，ID：{}", dto.getCaptchaId());
            return false;
        }

        boolean valid = CaptchaUtil.verifySlider(
                captchaInfo.getTargetX(),
                dto.getMoveX()
        );

        int diff = Math.abs(captchaInfo.getTargetX() - dto.getMoveX());

        if (valid) {
            log.info("滑块验证码验证成功，ID：{}，用户移动：{}，目标位置：{}，差值：{}",
                    dto.getCaptchaId(), dto.getMoveX(), captchaInfo.getTargetX(), diff);
        } else {
            log.warn("滑块验证码验证失败，ID：{}，用户移动：{}，目标位置：{}，差值：{}，容差：15",
                    dto.getCaptchaId(), dto.getMoveX(), captchaInfo.getTargetX(), diff);
        }

        return valid;
    }

    @Override
    public boolean verifyAndConsumeCaptcha(CaptchaVerifyDTO dto) {
        boolean valid = verifyCaptcha(dto);

        if (valid) {
            // 验证成功后删除验证码，防止重复使用
            captchaStore.remove(dto.getCaptchaId());
            log.info("验证码已消耗，ID：{}", dto.getCaptchaId());
        }

        return valid;
    }

    @Override
    public void deleteCaptcha(String captchaId) {
        captchaStore.remove(captchaId);
        log.info("删除验证码，ID：{}", captchaId);
    }

    /**
     * 清理过期验证码
     */
    private void cleanExpiredCaptchas() {
        long now = System.currentTimeMillis();
        int beforeSize = captchaStore.size();

        captchaStore.entrySet().removeIf(entry -> {
            // 从captchaId中提取时间戳
            String captchaId = entry.getKey();
            try {
                String timestampStr = captchaId.split("-")[0];
                long timestamp = Long.parseLong(timestampStr);
                return now - timestamp > CAPTCHA_EXPIRE_TIME;
            } catch (Exception e) {
                // 如果解析失败，删除该验证码
                return true;
            }
        });

        int afterSize = captchaStore.size();
        if (beforeSize - afterSize > 0) {
            log.info("清理过期验证码，清理数量：{}", beforeSize - afterSize);
        }
    }
}
