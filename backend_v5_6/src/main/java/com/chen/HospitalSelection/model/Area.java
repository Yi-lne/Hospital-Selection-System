package com.chen.HospitalSelection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省市区三级联动实体类
 * 对应表：area_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 地区编码（国标，如110000=北京）
     */
    private String code;

    /**
     * 地区名称（省/市/区）
     */
    private String name;

    /**
     * 父级编码（如北京市的父级是110000）
     */
    private String parentCode;

    /**
     * 层级（1=省，2=市，3=区/县）
     */
    private Integer level;
}
