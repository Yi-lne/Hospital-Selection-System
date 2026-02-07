package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 医院Mapper接口
 * 对应表：hospital_info
 */
@Mapper
public interface HospitalMapper {

    /**
     * 根据ID查询医院
     * @param id 医院ID
     * @return 医院对象
     */
    Hospital selectById(@Param("id") Long id);

    /**
     * 根据医院名称查询
     * @param hospitalName 医院名称
     * @return 医院对象
     */
    Hospital selectByName(@Param("hospitalName") String hospitalName);

    /**
     * 查询所有医院
     * @return 医院列表
     */
    List<Hospital> selectAll();

    /**
     * 根据医院等级查询
     * @param hospitalLevel 医院等级
     * @return 医院列表
     */
    List<Hospital> selectByLevel(@Param("hospitalLevel") String hospitalLevel);

    /**
     * 根据省份查询医院
     * @param provinceCode 省份编码
     * @return 医院列表
     */
    List<Hospital> selectByProvince(@Param("provinceCode") String provinceCode);

    /**
     * 根据城市查询医院
     * @param cityCode 城市编码
     * @return 医院列表
     */
    List<Hospital> selectByCity(@Param("cityCode") String cityCode);

    /**
     * 根据区县查询医院
     * @param areaCode 区县编码
     * @return 医院列表
     */
    List<Hospital> selectByArea(@Param("areaCode") String areaCode);

    /**
     * 根据是否医保定点查询
     * @param isMedicalInsurance 是否医保定点（0=否，1=是）
     * @return 医院列表
     */
    List<Hospital> selectByMedicalInsurance(@Param("isMedicalInsurance") Integer isMedicalInsurance);

    /**
     * 模糊搜索医院（按名称、重点科室）
     * @param keyword 关键词
     * @return 医院列表
     */
    List<Hospital> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据动态条件查询医院
     * @param hospitalLevel 医院等级
     * @param provinceCode 省份编码
     * @param cityCode 城市编码
     * @param areaCode 区域编码
     * @param isMedicalInsurance 是否医保定点
     * @param department 科室名称（通过 hospital_department 表精确匹配）
     * @param departmentNames 科室名称列表（用于匹配科室的多个名称，如全称和简称）
     * @return 医院列表
     */
    List<Hospital> selectByCondition(
            @Param("hospitalLevel") String hospitalLevel,
            @Param("provinceCode") String provinceCode,
            @Param("cityCode") String cityCode,
            @Param("areaCode") String areaCode,
            @Param("isMedicalInsurance") Integer isMedicalInsurance,
            @Param("department") String department,
            @Param("departmentNames") List<String> departmentNames
    );

    /**
     * 插入医院
     * @param hospital 医院对象
     * @return 影响行数
     */
    int insert(Hospital hospital);

    /**
     * 更新医院信息
     * @param hospital 医院对象
     * @return 影响行数
     */
    int updateById(Hospital hospital);

    /**
     * 更新医院评分
     * @param id 医院ID
     * @param rating 评分
     * @return 影响行数
     */
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating);

    /**
     * 增加评价数量
     * @param id 医院ID
     * @return 影响行数
     */
    int incrementReviewCount(@Param("id") Long id);

    /**
     * 逻辑删除医院
     * @param id 医院ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除医院
     * @param ids 医院ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 统计医院数量
     * @return 医院总数
     */
    int count();

    // ==================== 管理员专用方法 ====================

    /**
     * 根据名称搜索医院（模糊搜索）
     * @param hospitalName 医院名称（支持模糊）
     * @return 医院列表
     */
    List<Hospital> searchByName(@Param("hospitalName") String hospitalName);

    /**
     * 获取所有医院（包括已删除）
     * @param includeDeleted 是否包含已删除的医院
     * @return 医院列表
     */
    List<Hospital> selectAllIncludingDeleted(@Param("includeDeleted") Boolean includeDeleted);
}
