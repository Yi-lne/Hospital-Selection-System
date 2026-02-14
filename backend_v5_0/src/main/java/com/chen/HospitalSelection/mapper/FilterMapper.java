package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.model.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 筛选Mapper接口
 * 提供多条件筛选查询功能（医院、医生）
 */
@Mapper
public interface FilterMapper {

    // ==================== 医院筛选 ====================

    /**
     * 多条件筛选医院
     * @param hospitalLevel 医院等级（可选）
     * @param provinceCode 省份编码（可选）
     * @param cityCode 城市编码（可选）
     * @param areaCode 区县编码（可选）
     * @param isMedicalInsurance 是否医保定点（可选）
     * @param keyword 关键词（可选，搜索名称、重点科室）
     * @param minRating 最低评分（可选）
     * @param maxRating 最高评分（可选）
     * @return 医院列表
     */
    List<Hospital> filterHospitals(@Param("hospitalLevel") String hospitalLevel,
                                   @Param("provinceCode") String provinceCode,
                                   @Param("cityCode") String cityCode,
                                   @Param("areaCode") String areaCode,
                                   @Param("isMedicalInsurance") Integer isMedicalInsurance,
                                   @Param("keyword") String keyword,
                                   @Param("minRating") java.math.BigDecimal minRating,
                                   @Param("maxRating") java.math.BigDecimal maxRating);

    /**
     * 根据等级和地区筛选医院
     * @param hospitalLevel 医院等级
     * @param provinceCode 省份编码
     * @param cityCode 城市编码
     * @return 医院列表
     */
    List<Hospital> filterHospitalsByLevelAndArea(@Param("hospitalLevel") String hospitalLevel,
                                                 @Param("provinceCode") String provinceCode,
                                                 @Param("cityCode") String cityCode);

    /**
     * 根据评分范围筛选医院
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @return 医院列表
     */
    List<Hospital> filterHospitalsByRatingRange(@Param("minRating") java.math.BigDecimal minRating,
                                                @Param("maxRating") java.math.BigDecimal maxRating);

    /**
     * 根据医保定点和地区筛选医院
     * @param isMedicalInsurance 是否医保定点
     * @param cityCode 城市编码
     * @return 医院列表
     */
    List<Hospital> filterHospitalsByInsuranceAndCity(@Param("isMedicalInsurance") Integer isMedicalInsurance,
                                                     @Param("cityCode") String cityCode);

    /**
     * 高级筛选医院（支持排序）
     * @param hospitalLevel 医院等级（可选）
     * @param cityCode 城市编码（可选）
     * @param isMedicalInsurance 是否医保定点（可选）
     * @param keyword 关键词（可选）
     * @param minRating 最低评分（可选）
     * @param sortBy 排序字段（rating=评分，reviewCount=评价数）
     * @param sortOrder 排序方向（asc=升序，desc=降序）
     * @return 医院列表
     */
    List<Hospital> advancedFilterHospitals(@Param("hospitalLevel") String hospitalLevel,
                                           @Param("cityCode") String cityCode,
                                           @Param("isMedicalInsurance") Integer isMedicalInsurance,
                                           @Param("keyword") String keyword,
                                           @Param("minRating") java.math.BigDecimal minRating,
                                           @Param("sortBy") String sortBy,
                                           @Param("sortOrder") String sortOrder);

    // ==================== 医生筛选 ====================

    /**
     * 多条件筛选医生
     * @param hospitalId 医院ID（可选）
     * @param deptId 科室ID（可选）
     * @param title 职称（可选）
     * @param keyword 关键词（可选，搜索姓名、专业特长）
     * @param minRating 最低评分（可选）
     * @param maxRating 最高评分（可选）
     * @return 医生列表
     */
    List<Doctor> filterDoctors(@Param("hospitalId") Long hospitalId,
                               @Param("deptId") Long deptId,
                               @Param("title") String title,
                               @Param("keyword") String keyword,
                               @Param("minRating") java.math.BigDecimal minRating,
                               @Param("maxRating") java.math.BigDecimal maxRating);

    /**
     * 根据医院和科室筛选医生
     * @param hospitalId 医院ID
     * @param deptId 科室ID
     * @return 医生列表
     */
    List<Doctor> filterDoctorsByHospitalAndDept(@Param("hospitalId") Long hospitalId,
                                                @Param("deptId") Long deptId);

    /**
     * 根据职称筛选医生
     * @param title 职称
     * @param hospitalId 医院ID（可选）
     * @return 医生列表
     */
    List<Doctor> filterDoctorsByTitle(@Param("title") String title,
                                      @Param("hospitalId") Long hospitalId);

    /**
     * 根据评分范围筛选医生
     * @param minRating 最低评分
     * @param maxRating 最高评分
     * @param hospitalId 医院ID（可选）
     * @return 医生列表
     */
    List<Doctor> filterDoctorsByRatingRange(@Param("minRating") java.math.BigDecimal minRating,
                                            @Param("maxRating") java.math.BigDecimal maxRating,
                                            @Param("hospitalId") Long hospitalId);

    /**
     * 高级筛选医生（支持排序）
     * @param hospitalId 医院ID（可选）
     * @param deptId 科室ID（可选）
     * @param title 职称（可选）
     * @param keyword 关键词（可选）
     * @param minRating 最低评分（可选）
     * @param sortBy 排序字段（rating=评分，reviewCount=评价数）
     * @param sortOrder 排序方向（asc=升序，desc=降序）
     * @return 医生列表
     */
    List<Doctor> advancedFilterDoctors(@Param("hospitalId") Long hospitalId,
                                       @Param("deptId") Long deptId,
                                       @Param("title") String title,
                                       @Param("keyword") String keyword,
                                       @Param("minRating") java.math.BigDecimal minRating,
                                       @Param("sortBy") String sortBy,
                                       @Param("sortOrder") String sortOrder);

    // ==================== 统计查询 ====================

    /**
     * 统计筛选后的医院数量
     * @param hospitalLevel 医院等级（可选）
     * @param cityCode 城市编码（可选）
     * @param isMedicalInsurance 是否医保定点（可选）
     * @param keyword 关键词（可选）
     * @return 医院数量
     */
    int countFilteredHospitals(@Param("hospitalLevel") String hospitalLevel,
                               @Param("cityCode") String cityCode,
                               @Param("isMedicalInsurance") Integer isMedicalInsurance,
                               @Param("keyword") String keyword);

    /**
     * 统计筛选后的医生数量
     * @param hospitalId 医院ID（可选）
     * @param deptId 科室ID（可选）
     * @param title 职称（可选）
     * @param keyword 关键词（可选）
     * @return 医生数量
     */
    int countFilteredDoctors(@Param("hospitalId") Long hospitalId,
                             @Param("deptId") Long deptId,
                             @Param("title") String title,
                             @Param("keyword") String keyword);
}
