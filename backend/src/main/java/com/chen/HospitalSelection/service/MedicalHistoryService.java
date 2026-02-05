package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.MedicalHistoryDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.MedicalHistoryVO;
import com.chen.HospitalSelection.vo.PageResult;

import java.util.List;

/**
 * 病史服务接口
 * 提供用户病史管理功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface MedicalHistoryService {

    /**
     * 获取用户的所有病史记录
     *
     * @param userId 用户ID
     * @return 病史列表
     */
    List<MedicalHistoryVO> getMedicalHistoryList(Long userId);

    /**
     * 分页查询病史记录
     *
     * @param userId 用户ID
     * @param dto    分页查询参数
     * @return 病史分页列表
     */
    PageResult<MedicalHistoryVO> getMedicalHistoryPage(Long userId, PageQueryDTO dto);

    /**
     * 获取病史详情
     *
     * @param historyId 病史ID
     * @param userId    用户ID
     * @return 病史详细信息
     * @throws RuntimeException 当用户无权查看时抛出异常
     */
    MedicalHistoryVO getMedicalHistoryDetail(Long historyId, Long userId);

    /**
     * 添加病史记录
     *
     * @param userId 用户ID
     * @param dto    病史信息
     * @return 病史ID
     */
    Long addMedicalHistory(Long userId, MedicalHistoryDTO dto);

    /**
     * 修改病史记录
     *
     * @param historyId 病史ID
     * @param userId    用户ID
     * @param dto       病史信息
     * @throws RuntimeException 当用户无权修改时抛出异常
     */
    void updateMedicalHistory(Long historyId, Long userId, MedicalHistoryDTO dto);

    /**
     * 删除病史记录
     *
     * @param historyId 病史ID
     * @param userId    用户ID
     * @throws RuntimeException 当用户无权删除时抛出异常
     */
    void deleteMedicalHistory(Long historyId, Long userId);

    /**
     * 根据疾病名称查询病史记录
     *
     * @param userId      用户ID
     * @param diseaseName 疾病名称
     * @return 病史列表
     */
    List<MedicalHistoryVO> searchByDiseaseName(Long userId, String diseaseName);

    /**
     * 根据治疗状态查询病史记录
     *
     * @param userId 用户ID
     * @param status 状态（1=治疗中，2=已康复）
     * @return 病史列表
     */
    List<MedicalHistoryVO> searchByStatus(Long userId, Integer status);

    /**
     * 获取正在治疗的疾病列表
     *
     * @param userId 用户ID
     * @return 正在治疗的疾病列表
     */
    List<MedicalHistoryVO> getActiveDiseases(Long userId);
}
