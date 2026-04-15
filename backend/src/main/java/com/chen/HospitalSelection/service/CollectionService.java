package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.CollectionVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.Map;

/**
 * 收藏服务接口
 * 提供医院、医生、话题的收藏功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface CollectionService {

    /**
     * 添加收藏
     *
     * @param userId 用户ID
     * @param dto    收藏信息（目标类型、目标ID）
     * @return 收藏ID
     * @throws RuntimeException 当已收藏时抛出异常
     */
    Long addCollection(Long userId, CollectionDTO dto);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param dto    收藏信息（目标类型、目标ID）
     */
    void cancelCollection(Long userId, CollectionDTO dto);

    /**
     * 获取我的收藏列表
     *
     * @param userId     用户ID
     * @param targetType 收藏类型（1=医院，2=医生，3=话题，可选）
     * @param dto        分页查询参数
     * @return 收藏分页列表
     */
    PageResult<CollectionVO> getCollectionList(Long userId, Integer targetType, PageQueryDTO dto);

    /**
     * 检查是否已收藏
     *
     * @param userId     用户ID
     * @param targetType 收藏类型
     * @param targetId   目标ID
     * @return true=已收藏，false=未收藏
     */
    boolean checkCollection(Long userId, Integer targetType, Long targetId);

}
