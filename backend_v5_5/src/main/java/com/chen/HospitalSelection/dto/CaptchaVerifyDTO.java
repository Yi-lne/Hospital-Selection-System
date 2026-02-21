package com.chen.HospitalSelection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 滑块验证码验证DTO
 *
 * @author chen
 * @since 2025-02-16
 */
@Data
@ApiModel(description = "滑块验证码验证请求参数")
public class CaptchaVerifyDTO implements Serializable {

    @NotNull(message = "验证码ID不能为空")
    @ApiModelProperty(value = "验证码ID", required = true, example = "1234567890-1234")
    private String captchaId;

    @NotNull(message = "滑块位置X坐标不能为空")
    @ApiModelProperty(value = "滑块位置X坐标", required = true, example = "150")
    private Integer moveX;
}
