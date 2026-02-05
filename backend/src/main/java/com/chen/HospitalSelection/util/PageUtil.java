package com.chen.HospitalSelection.util;

/**
 * 分页工具类
 * 提供分页相关的常用方法
 */
public class PageUtil {

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页大小
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页大小
     */
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 获取页码
     *
     * @param pageNum 页码
     * @return 有效页码
     */
    public static int getPageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    /**
     * 获取每页大小
     *
     * @param pageSize 每页大小
     * @return 有效每页大小
     */
    public static int getPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    /**
     * 获取偏移量（用于SQL查询的LIMIT offset, size）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 偏移量
     */
    public static int getOffset(Integer pageNum, Integer pageSize) {
        int validPageNum = getPageNum(pageNum);
        int validPageSize = getPageSize(pageSize);
        return (validPageNum - 1) * validPageSize;
    }

    /**
     * 计算总页数
     *
     * @param totalCount 总记录数
     * @param pageSize   每页大小
     * @return 总页数
     */
    public static int getTotalPages(long totalCount, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 验证页码是否有效
     *
     * @param pageNum    页码
     * @param totalCount 总记录数
     * @param pageSize   每页大小
     * @return true-有效，false-无效
     */
    public static boolean isValidPageNum(int pageNum, long totalCount, int pageSize) {
        if (pageNum < 1) {
            return false;
        }
        int totalPages = getTotalPages(totalCount, pageSize);
        return totalPages == 0 || pageNum <= totalPages;
    }
}
