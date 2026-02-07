package com.chen.HospitalSelection.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果封装类
 * 用于返回分页列表数据
 *
 * @param <T> 列表数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * 构造方法（自动计算总页数）
     *
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param list     列表数据
     */
    public PageResult(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        // 计算总页数
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 空结果构造方法
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L, 1, 10, null);
    }

    /**
     * 空结果构造方法（自定义分页参数）
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return new PageResult<>(0L, pageNum, pageSize, null);
    }
}
