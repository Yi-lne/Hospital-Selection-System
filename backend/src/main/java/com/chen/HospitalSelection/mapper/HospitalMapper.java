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
     * 模糊搜索医院（按名称、重点科室）
     * @param keyword 关键词
     * @return 医院列表
     */
    List<Hospital> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 模糊搜索医院（按名称、重点科室，可选择是否包含已删除）
     * @param keyword 关键词
     * @param includeDeleted 是否包含已删除的医院
     * @return 医院列表
     */
    List<Hospital> searchByKeywordIncludingDeleted(
            @Param("keyword") String keyword,
            @Param("includeDeleted") Boolean includeDeleted
    );

    /**
     * 查询所有医院（包含已删除）
     * @param includeDeleted 是否包含已删除
     * @return 医院列表
     */
    List<Hospital> selectAllIncludingDeleted(@Param("includeDeleted") Boolean includeDeleted);

    /**
     * 根据动态条件查询医院
     * @param hospitalLevel 医院等级
     * @param provinceCode 省份编码
     * @param cityCode 城市编码
     * @param areaCode 区域编码
     * @param isMedicalInsurance 是否医保定点
     * @param keyDepartments 重点科室
     * @param deptName 科室名称
     * @param sortBy 排序方式（level=级别优先，rating=评分优先）
     * @return 医院列表
     */
    List<Hospital> selectByCondition(
            @Param("hospitalLevel") String hospitalLevel,
            @Param("provinceCode") String provinceCode,
            @Param("cityCode") String cityCode,
            @Param("areaCode") String areaCode,
            @Param("isMedicalInsurance") Integer isMedicalInsurance,
            @Param("keyDepartments") String keyDepartments,
            @Param("deptName") String deptName,
            @Param("sortBy") String sortBy
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
     * 逻辑删除医院
     * @param id 医院ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
