package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.MedicalHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 病史Mapper接口
 * 对应表：user_medical_history
 */
@Mapper
public interface MedicalHistoryMapper {

    /**
     * 根据ID查询病史
     * @param id 主键ID
     * @return 病史对象
     */
    MedicalHistory selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询所有病史
     * @param userId 用户ID
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserId(@Param("userId") Long userId);

    /**
     * 插入病史
     * @param medicalHistory 病史对象
     * @return 影响行数
     */
    int insert(MedicalHistory medicalHistory);

    /**
     * 更新病史信息
     * @param medicalHistory 病史对象
     * @return 影响行数
     */
    int updateById(MedicalHistory medicalHistory);

    /**
     * 逻辑删除病史
     * @param id 病史ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
