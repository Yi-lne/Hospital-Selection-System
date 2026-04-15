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
     * 根据疾病编码查询
     * @param diseaseCode 疾病编码
     * @return 疾病对象
     */
    Disease selectByCode(@Param("diseaseCode") String diseaseCode);

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
}
