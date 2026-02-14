package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医生信息返回对象（详细信息）
 * 用于返回医生详情页面的完整信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVO {

    /**
     * 医生ID
     */
    private Long id;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 所属医院ID
     */
    private Long hospitalId;

    /**
     * 所属医院名称
     */
    private String hospitalName;

    /**
     * 所属医院等级
     */
    private String hospitalLevel;

    /**
     * 科室ID
     */
    private Long deptId;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 职称（如：主任医师、副主任医师）
     */
    private String title;

    /**
     * 专业特长
     */
    private String specialty;

    /**
     * 学术背景（学历、研究方向等）
     */
    private String academicBackground;

    /**
     * 坐诊时间（如：周一上午、周三下午）
     */
    private String scheduleTime;

    /**
     * 挂号/咨询费
     */
    private BigDecimal consultationFee;

    /**
     * 患者评分（0.00-5.00）
     */
    private BigDecimal rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 是否已收藏（需要登录时返回）
     */
    private Boolean isCollected;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
