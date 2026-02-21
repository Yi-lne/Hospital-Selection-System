package com.chen.HospitalSelection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 滑块验证码响应VO
 *
 * @author chen
 * @since 2025-02-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "滑块验证码响应信息")
public class CaptchaVO implements Serializable {

    @ApiModelProperty(value = "验证码ID", example = "1234567890-1234")
    private String captchaId;

    @ApiModelProperty(value = "背景图片（Base64编码）", example = "data:image/png;base64,iVBORw0KG...")
    private String backgroundImage;

    @ApiModelProperty(value = "滑块图片（Base64编码）", example = "data:image/png;base64,iVBORw0KG...")
    private String sliderImage;

    @ApiModelProperty(value = "滑块Y坐标", example = "60")
    private Integer sliderY;
}
