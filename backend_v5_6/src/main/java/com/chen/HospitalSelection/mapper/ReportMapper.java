package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Report;
import com.chen.HospitalSelection.vo.ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 举报Mapper接口
 */
@Mapper
public interface ReportMapper {

    /**
     * 插入举报记录
     */
    int insert(Report report);

    /**
     * 根据ID查询
     */
    Report selectById(Long id);

    /**
     * 查询所有举报（分页）
     */
    List<ReportVO> selectByPage(@Param("status") Integer status, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计举报数量
     */
    int countByStatus(@Param("status") Integer status);

    /**
     * 更新处理状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("handleResult") String handleResult, @Param("handlerId") Long handlerId);

    /**
     * 删除举报记录
     */
    int deleteById(Long id);
}
