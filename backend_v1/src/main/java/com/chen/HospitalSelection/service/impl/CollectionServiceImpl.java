package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.CollectionMapper;
import com.chen.HospitalSelection.model.UserCollectionItem;
import com.chen.HospitalSelection.service.CollectionService;
import com.chen.HospitalSelection.vo.CollectionVO;
import com.chen.HospitalSelection.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    @Transactional
    public Long addCollection(Long userId, CollectionDTO dto) {
        log.info("添加收藏，用户ID：{}，目标类型：{}，目标ID：{}", userId, dto.getTargetType(), dto.getTargetId());

        // 检查是否已收藏
        int count = collectionMapper.countByUserAndTarget(userId, dto.getTargetType(), dto.getTargetId());
        if (count > 0) {
            throw new BusinessException("已经收藏过了");
        }

        UserCollectionItem collection = new UserCollectionItem();
        collection.setUserId(userId);
        collection.setTargetType(dto.getTargetType());
        collection.setTargetId(dto.getTargetId());
        collection.setIsDeleted(0);
        collection.setCreateTime(LocalDateTime.now());

        collectionMapper.insert(collection);

        log.info("收藏添加成功，收藏ID：{}", collection.getId());
        return collection.getId();
    }

    @Override
    @Transactional
    public void cancelCollection(Long userId, CollectionDTO dto) {
        log.info("取消收藏，用户ID：{}，目标类型：{}，目标ID：{}", userId, dto.getTargetType(), dto.getTargetId());

        int count = collectionMapper.countByUserAndTarget(userId, dto.getTargetType(), dto.getTargetId());
        if (count == 0) {
            throw new BusinessException("未收藏该项目");
        }

        collectionMapper.cancelCollection(userId, dto.getTargetType(), dto.getTargetId());

        log.info("收藏取消成功");
    }

    @Override
    public PageResult<CollectionVO> getCollectionList(Long userId, Integer targetType, PageQueryDTO dto) {
        log.info("查询收藏列表，用户ID：{}，目标类型：{}", userId, targetType);

        List<UserCollectionItem> collectionList;
        long total;

        if (targetType != null) {
            // 根据类型查询（使用已有的方法）
            if (targetType == 1) {
                collectionList = collectionMapper.selectHospitalsByUserId(userId);
            } else if (targetType == 2) {
                collectionList = collectionMapper.selectDoctorsByUserId(userId);
            } else if (targetType == 3) {
                collectionList = collectionMapper.selectTopicsByUserId(userId);
            } else {
                collectionList = collectionMapper.selectByUserId(userId);
            }
        } else {
            // 查询所有收藏
            collectionList = collectionMapper.selectByUserId(userId);
        }

        total = collectionList.size();

        // 手动分页（简化实现）
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), collectionList.size());
        List<UserCollectionItem> pagedList = offset < collectionList.size()
            ? collectionList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<CollectionVO> voList = pagedList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public boolean checkCollection(Long userId, Integer targetType, Long targetId) {
        int count = collectionMapper.countByUserAndTarget(userId, targetType, targetId);
        return count > 0;
    }

    @Override
    public Map<Integer, Long> getCollectionCount(Long userId) {
        log.info("获取收藏数量统计，用户ID：{}", userId);

        Map<Integer, Long> countMap = new HashMap<>();

        // 统计各类型收藏数量（简化实现：查询所有后在内存中统计）
        List<UserCollectionItem> allCollections = collectionMapper.selectByUserId(userId);

        long hospitalCount = allCollections.stream().filter(c -> c.getTargetType() == 1).count();
        countMap.put(1, hospitalCount);

        long doctorCount = allCollections.stream().filter(c -> c.getTargetType() == 2).count();
        countMap.put(2, doctorCount);

        long topicCount = allCollections.stream().filter(c -> c.getTargetType() == 3).count();
        countMap.put(3, topicCount);

        return countMap;
    }

    @Override
    @Transactional
    public boolean toggleCollection(Long userId, CollectionDTO dto) {
        log.info("切换收藏状态，用户ID：{}，目标类型：{}，目标ID：{}", userId, dto.getTargetType(), dto.getTargetId());

        int count = collectionMapper.countByUserAndTarget(userId, dto.getTargetType(), dto.getTargetId());
        if (count > 0) {
            // 已收藏，取消收藏
            cancelCollection(userId, dto);
            return false;
        } else {
            // 未收藏，添加收藏
            addCollection(userId, dto);
            return true;
        }
    }

    @Override
    public PageResult<CollectionVO> getHospitalCollections(Long userId, PageQueryDTO dto) {
        return getCollectionList(userId, 1, dto);
    }

    @Override
    public PageResult<CollectionVO> getDoctorCollections(Long userId, PageQueryDTO dto) {
        return getCollectionList(userId, 2, dto);
    }

    @Override
    public PageResult<CollectionVO> getTopicCollections(Long userId, PageQueryDTO dto) {
        return getCollectionList(userId, 3, dto);
    }

    /**
     * 转换为收藏VO
     */
    private CollectionVO convertToVO(UserCollectionItem collection) {
        CollectionVO vo = new CollectionVO();
        BeanUtils.copyProperties(collection, vo);
        // 这里需要根据targetType和targetId查询具体的目标对象信息
        // 简化实现，实际应该调用对应的Mapper查询医院/医生/话题信息
        return vo;
    }
}
