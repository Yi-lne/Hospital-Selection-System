package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 话题修改DTO
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
     * 一级板块
     */
    @Size(max = 50, message = "一级板块长度不能超过50个字符")
    @Schema(description = "一级板块", example = "心血管区")
    private String boardLevel1;

    /**
     * 二级板块（可选）
     */
    @Size(max = 50, message = "二级板块长度不能超过50个字符")
    @Schema(description = "二级板块", example = "高血压")
    private String boardLevel2;

    /**
     * 板块类型（1 = 疾病板块，2 = 医院评价区，3 = 就医经验区，4 = 康复护理区）
     */
    @Schema(description = "板块类型", example = "1")
    private Integer boardType;

    /**
     * 话题标题
     */
    @NotBlank(message = "话题标题不能为空")
    @Size(max = 200, message = "话题标题长度不能超过200个字符")
    @Schema(description = "话题标题", example = "高血压患者如何科学饮食？", required = true)
    private String title;

    /**
     * 话题内容
     */
    @NotBlank(message = "话题内容不能为空")
    @Schema(description = "话题内容", example = "我最近被诊断为高血压...", required = true)
    private String content;
}
