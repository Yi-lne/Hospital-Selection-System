package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.QueryHistoryVO;

import java.util.List;

/**
 * 查询历史服务接口
 * 提供用户查询浏览历史记录功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface QueryHistoryService {

    /**
     * 获取查询历史列表
     *
     * @param userId 用户ID
     * @param dto    分页查询参数
     * @return 查询历史分页列表
     */
    PageResult<QueryHistoryVO> getQueryHistoryList(Long userId, PageQueryDTO dto);

    /**
     * 记录用户查询行为
     *
     * @param userId     用户ID
     * @param queryType  查询类型（1=医院，2=医生，3=话题）
     * @param targetId   查询目标ID
     * @param queryParams 查询条件（JSON格式，可选）
     * @return 历史记录ID
     */
    Long recordQuery(Long userId, Integer queryType, Long targetId, String queryParams);

    /**
     * 删除查询历史记录
     *
     * @param historyId 历史记录ID
     * @param userId    用户ID
     * @throws RuntimeException 当用户无权删除时抛出异常
     */
    void deleteQueryHistory(Long historyId, Long userId);

    /**
     * 清空查询历史
     *
     * @param userId 用户ID
     */
    void clearQueryHistory(Long userId);

    /**
     * 获取医院查询历史
     *
     * @param userId 用户ID
     * @param dto    分页查询参数
     * @return 医院查询历史
     */
    PageResult<QueryHistoryVO> getHospitalQueryHistory(Long userId, PageQueryDTO dto);

    /**
     * 获取医生查询历史
     *
     * @param userId 用户ID
     * @param dto    分页查询参数
     * @return 医生查询历史
     */
    PageResult<QueryHistoryVO> getDoctorQueryHistory(Long userId, PageQueryDTO dto);

    /**
     * 获取话题浏览历史
     *
     * @param userId 用户ID
     * @param dto    分页查询参数
     * @return 话题浏览历史
     */
    PageResult<QueryHistoryVO> getTopicQueryHistory(Long userId, PageQueryDTO dto);

    /**
     * 获取最近查询记录（用于首页展示）
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 最近的查询记录
     */
    List<QueryHistoryVO> getRecentQueries(Long userId, Integer limit);

    /**
     * 获取热门查询记录
     *
     * @param queryType 查询类型
     * @param limit     限制数量
     * @return 热门查询记录
     */
    List<QueryHistoryVO> getHotQueries(Integer queryType, Integer limit);
}
