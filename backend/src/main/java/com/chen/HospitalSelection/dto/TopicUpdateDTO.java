package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 话题修改 DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "话题修改请求参数")
public class TopicUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病编码（可选）
     */
    @Schema(description = "疾病编码", example = "hypertension")
    private String diseaseCode;

    /**
     * 话题板块子类
     */
    @Size(max = 50, message = "话题板块子类长度不能超过 50 个字符")
    @Schema(description = "话题板块子类", example = "心血管")
    private String boardSub;

    /**
     * 话题板块大类（1 = 疾病板块，2 = 医院评价区，3 = 就医经验区，4 = 康复护理区）
     */
    @Schema(description = "话题板块大类", example = "1")
    private Integer boardType;

    /**
     * 话题标题
     */
    @NotBlank(message = "话题标题不能为空")
    @Size(max = 200, message = "话题标题长度不能超过 200 个字符")
    @Schema(description = "话题标题", example = "高血压患者如何科学饮食？", required = true)
    private String title;

    /**
     * 话题内容
     */
    @NotBlank(message = "话题内容不能为空")
    @Schema(description = "话题内容", example = "我最近被诊断为高血压...", required = true)
    private String content;
}
