package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.MedicalHistoryDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.MedicalHistoryMapper;
import com.chen.HospitalSelection.model.MedicalHistory;
import com.chen.HospitalSelection.service.MedicalHistoryService;
import com.chen.HospitalSelection.vo.MedicalHistoryVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 病史服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    @Autowired
    private MedicalHistoryMapper medicalHistoryMapper;

    @Override
    public List<MedicalHistoryVO> getMedicalHistoryList(Long userId) {
        log.info("查询病史列表，用户ID：{}", userId);

        List<MedicalHistory> historyList = medicalHistoryMapper.selectByUserId(userId);

        return historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalHistoryVO getMedicalHistoryDetail(Long historyId, Long userId) {
        log.info("查询病史详情，病史ID：{}，用户ID：{}", historyId, userId);

        MedicalHistory history = medicalHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException("病史记录不存在");
        }

        // 检查权限：只能查看自己的病史
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此病史记录");
        }

        return convertToVO(history);
    }

    @Override
    @Transactional
    public Long addMedicalHistory(Long userId, MedicalHistoryDTO dto) {
        log.info("添加病史记录，用户ID：{}", userId);

        MedicalHistory history = new MedicalHistory();
        BeanUtils.copyProperties(dto, history);
        history.setUserId(userId);
        history.setIsDeleted(0);
        history.setCreateTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());

        medicalHistoryMapper.insert(history);

        log.info("病史记录添加成功，病史ID：{}", history.getId());
        return history.getId();
    }

    @Override
    @Transactional
    public void updateMedicalHistory(Long historyId, Long userId, MedicalHistoryDTO dto) {
        log.info("修改病史记录，病史ID：{}，用户ID：{}", historyId, userId);

        MedicalHistory history = medicalHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException("病史记录不存在");
        }

        // 检查权限：只能修改自己的病史
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此病史记录");
        }

        BeanUtils.copyProperties(dto, history);
        history.setUpdateTime(LocalDateTime.now());

        medicalHistoryMapper.updateById(history);

        log.info("病史记录修改成功，病史ID：{}", historyId);
    }

    @Override
    @Transactional
    public void deleteMedicalHistory(Long historyId, Long userId) {
        log.info("删除病史记录，病史ID：{}，用户ID：{}", historyId, userId);

        MedicalHistory history = medicalHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException("病史记录不存在");
        }

        // 检查权限：只能删除自己的病史
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此病史记录");
        }

        medicalHistoryMapper.deleteById(historyId);

        log.info("病史记录删除成功，病史ID：{}", historyId);
    }

    /**
     * 转换为病史VO
     */
    private MedicalHistoryVO convertToVO(MedicalHistory history) {
        MedicalHistoryVO vo = new MedicalHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }
}
