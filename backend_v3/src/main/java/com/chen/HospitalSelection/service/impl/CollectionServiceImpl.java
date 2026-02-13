package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.CollectionMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.UserCollectionItem;
import com.chen.HospitalSelection.service.CollectionService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Override
    @Transactional
    public Long addCollection(Long userId, CollectionDTO dto) {
        log.info("添加收藏，用户ID：{}，目标类型：{}，目标ID：{}", userId, dto.getTargetType(), dto.getTargetId());

        // 检查是否已收藏（未删除状态）
        int count = collectionMapper.countByUserAndTarget(userId, dto.getTargetType(), dto.getTargetId());
        if (count > 0) {
            throw new BusinessException("400", "已经收藏过了");
        }

        // 尝试恢复已删除的收藏记录（如果存在）
        int recollected = collectionMapper.recollect(userId, dto.getTargetType(), dto.getTargetId());
        if (recollected > 0) {
            // 成功恢复旧记录，查询ID返回
            UserCollectionItem existing = collectionMapper.selectByUserAndTarget(userId, dto.getTargetType(), dto.getTargetId());
            log.info("收藏恢复成功，收藏ID：{}", existing.getId());
            return existing.getId();
        }

        // 不存在任何记录，插入新记录
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
            throw new BusinessException("400", "未收藏该项目");
        }

        collectionMapper.cancelCollection(userId, dto.getTargetType(), dto.getTargetId());

        log.info("收藏取消成功");
    }

    @Override
    public PageResult<CollectionVO> getCollectionList(Long userId, Integer targetType, PageQueryDTO dto) {
        log.info("查询收藏列表，用户ID：{}，目标类型：{}", userId, targetType);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        List<UserCollectionItem> collectionList;
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

        PageInfo<UserCollectionItem> pageInfo = new PageInfo<>(collectionList);

        // 获取当前页数据
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), collectionList.size());
        List<UserCollectionItem> pagedList = offset < collectionList.size()
            ? collectionList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<CollectionVO> voList = pagedList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
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
        vo.setId(collection.getId());
        vo.setTargetType(collection.getTargetType());
        vo.setTargetId(collection.getTargetId());
        vo.setCreateTime(collection.getCreateTime());

        // 根据收藏类型查询对应的目标对象信息
        if (collection.getTargetType() == 1) {
            // 医院
            Hospital hospital = hospitalMapper.selectById(collection.getTargetId());
            if (hospital != null) {
                HospitalSimpleVO hospitalVO = new HospitalSimpleVO();
                hospitalVO.setId(hospital.getId());
                hospitalVO.setHospitalName(hospital.getHospitalName());
                hospitalVO.setHospitalLevel(hospital.getHospitalLevel());
                hospitalVO.setAddress(hospital.getAddress());
                hospitalVO.setPhone(hospital.getPhone());
                hospitalVO.setRating(hospital.getRating());
                hospitalVO.setReviewCount(hospital.getReviewCount());
                hospitalVO.setIsMedicalInsurance(hospital.getIsMedicalInsurance());
                hospitalVO.setKeyDepartments(hospital.getKeyDepartments());
                vo.setHospital(hospitalVO);
            }
        } else if (collection.getTargetType() == 2) {
            // 医生
            Doctor doctor = doctorMapper.selectById(collection.getTargetId());
            if (doctor != null) {
                DoctorSimpleVO doctorVO = new DoctorSimpleVO();
                doctorVO.setId(doctor.getId());
                doctorVO.setDoctorName(doctor.getDoctorName());
                doctorVO.setTitle(doctor.getTitle());
                doctorVO.setHospitalId(doctor.getHospitalId());
                doctorVO.setDeptId(doctor.getDeptId());
                doctorVO.setSpecialty(doctor.getSpecialty());
                doctorVO.setScheduleTime(doctor.getScheduleTime());
                doctorVO.setConsultationFee(doctor.getConsultationFee());
                doctorVO.setRating(doctor.getRating());
                doctorVO.setReviewCount(doctor.getReviewCount());
                vo.setDoctor(doctorVO);
            }
        } else if (collection.getTargetType() == 3) {
            // 话题
            Topic topic = topicMapper.selectById(collection.getTargetId());
            if (topic != null) {
                TopicVO topicVO = new TopicVO();
                topicVO.setId(topic.getId());
                topicVO.setUserId(topic.getUserId());
                topicVO.setTitle(topic.getTitle());
                topicVO.setDiseaseCode(topic.getDiseaseCode());
                topicVO.setBoardLevel1(topic.getBoardLevel1());
                topicVO.setBoardLevel2(topic.getBoardLevel2());
                topicVO.setBoardType(topic.getBoardType());
                topicVO.setLikeCount(topic.getLikeCount());
                topicVO.setCommentCount(topic.getCommentCount());
                topicVO.setCollectCount(topic.getCollectCount());
                topicVO.setViewCount(topic.getViewCount());
                topicVO.setCreateTime(topic.getCreateTime());
                vo.setTopic(topicVO);
            }
        }

        return vo;
    }
}
