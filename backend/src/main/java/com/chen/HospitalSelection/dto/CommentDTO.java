package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 评论提交DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "评论提交请求参数")
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 话题ID
     */
    @NotNull(message = "话题ID不能为空")
    @Schema(description = "话题ID", example = "123", required = true)
    private Long topicId;

    /**
     * 父评论ID（0 = 一级评论，>0 = 回复评论）
     */
    @Schema(description = "父评论ID（0=一级评论，>0=回复评论）", example = "0")
    private Long parentId;

    /**
     * 评论内容
     */
    @NotNull(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容长度不能超过500个字符")
    @Schema(description = "评论内容", example = "这条分享很有用", required = true)
    private String content;
}
