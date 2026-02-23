package com.chen.HospitalSelection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI查询请求DTO
 *
 * @author chen
 * @since 2025-02-22
 */
@Data
@ApiModel("AI查询请求")
public class AIQueryRequestDTO {

    @ApiModelProperty(value = "用户的自然语言查询", required = true, example = "我在北京，经常头痛")
    @NotBlank(message = "查询内容不能为空")
    @Size(min = 2, max = 500, message = "查询内容长度必须在2-500字之间")
    private String query;

    @ApiModelProperty(value = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量", example = "10")
    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;
}
