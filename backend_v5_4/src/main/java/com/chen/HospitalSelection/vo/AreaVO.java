package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 地区信息返回对象（树形结构）
 * 用于返回省市区三级联动数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaVO {

    /**
     * 地区ID
     */
    private Long id;

    /**
     * 地区编码（国标，如 110000 = 北京）
     */
    private String code;

    /**
     * 地区名称（省/市/区）
     */
    private String name;

    /**
     * 父级编码（如北京市的父级是 110000）
     */
    private String parentCode;

    /**
     * 层级（1 = 省，2 = 市，3 = 区/县）
     */
    private Integer level;

    /**
     * 子地区列表（树形结构）
     */
    private List<AreaVO> children;
}
