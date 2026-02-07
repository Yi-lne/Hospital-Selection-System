package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Disease;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 疾病分类Mapper接口
 * 对应表：disease_type
 */
@Mapper
public interface DiseaseMapper {

    /**
     * 根据ID查询疾病分类
     * @param id 疾病ID
     * @return 疾病对象
     */
    Disease selectById(@Param("id") Long id);

    /**
     * 根据疾病编码查询
     * @param diseaseCode 疾病编码
     * @return 疾病对象
     */
    Disease selectByCode(@Param("diseaseCode") String diseaseCode);

    /**
     * 根据疾病名称查询
     * @param diseaseName 疾病名称
     * @return 疾病对象
     */
    Disease selectByName(@Param("diseaseName") String diseaseName);

    /**
     * 根据父分类ID查询子分类列表
     * @param parentId 父分类ID（0=查询一级分类）
     * @return 疾病分类列表
     */
    List<Disease> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有一级分类
     * @return 一级分类列表
     */
    List<Disease> selectLevel1();

    /**
     * 查询所有疾病分类
     * @return 疾病分类列表
     */
    List<Disease> selectAll();

    /**
     * 模糊搜索疾病（按名称）
     * @param keyword 关键词
     * @return 疾病列表
     */
    List<Disease> searchByName(@Param("keyword") String keyword);

    /**
     * 插入疾病分类
     * @param disease 疾病对象
     * @return 影响行数
     */
    int insert(Disease disease);

    /**
     * 批量插入疾病分类
     * @param diseases 疾病列表
     * @return 影响行数
     */
    int batchInsert(@Param("diseases") List<Disease> diseases);

    /**
     * 更新疾病分类信息
     * @param disease 疾病对象
     * @return 影响行数
     */
    int updateById(Disease disease);

    /**
     * 逻辑删除疾病分类
     * @param id 疾病ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除疾病分类
     * @param ids 疾病ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据父分类ID删除所有子分类
     * @param parentId 父分类ID
     * @return 影响行数
     */
    int deleteByParentId(@Param("parentId") Long parentId);

    /**
     * 检查疾病编码是否存在
     * @param diseaseCode 疾病编码
     * @return 存在数量
     */
    int countByCode(@Param("diseaseCode") String diseaseCode);

    /**
     * 统计一级分类数量
     * @return 一级分类数量
     */
    int countLevel1();

    /**
     * 统计子分类数量
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    int countByParentId(@Param("parentId") Long parentId);
}
