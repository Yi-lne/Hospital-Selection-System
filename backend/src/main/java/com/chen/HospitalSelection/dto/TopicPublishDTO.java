package com.chen.HospitalSelection.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 话题发布DTO
 *
 * @author chen
 * @since 2025-01-30
 */
@Data
@Schema(description = "话题发布请求参数")
public class TopicPublishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病编码（可选）
     */
    @Schema(description = "疾病编码", example = "hypertension")
    private String diseaseCode;

    /**
     * 一级板块
     */
    @NotBlank(message = "一级板块不能为空")
    @Size(max = 50, message = "一级板块长度不能超过50个字符")
    @Schema(description = "一级板块（心血管区、内分泌区、肿瘤区、儿科区）", example = "心血管区", required = true)
    private String boardLevel1;

    /**
     * 二级板块（可选）
     */
    @Size(max = 50, message = "二级板块长度不能超过50个字符")
    @Schema(description = "二级板块（具体疾病，如高血压、冠心病）", example = "高血压")
    private String boardLevel2;

    /**
     * 板块类型（1 = 疾病板块，2 = 医院评价区，3 = 就医经验区，4 = 康复护理区）
     */
    @NotNull(message = "板块类型不能为空")
    @Schema(description = "板块类型（1=疾病板块，2=医院评价区，3=就医经验区，4=康复护理区）", example = "1", required = true)
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
