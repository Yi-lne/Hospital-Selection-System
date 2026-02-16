package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 发送私信DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "发送私信请求参数")
public class MessageSendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接收者用户ID
     */
    @NotNull(message = "接收者ID不能为空")
    @Schema(description = "接收者用户ID", example = "5", required = true)
    private Long receiverId;

    /**
     * 私信内容
     */
    @NotBlank(message = "私信内容不能为空")
    @Size(max = 500, message = "私信内容长度不能超过500个字符")
    @Schema(description = "私信内容", example = "你好，请问你在哪家医院治疗高血压？", required = true)
    private String content;
}
