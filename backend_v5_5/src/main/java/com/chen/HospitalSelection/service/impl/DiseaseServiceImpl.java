package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DiseaseMapper;
import com.chen.HospitalSelection.model.Disease;
import com.chen.HospitalSelection.service.DiseaseService;
import com.chen.HospitalSelection.vo.DiseaseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 疾病分类服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class DiseaseServiceImpl implements DiseaseService {

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Override
    public List<DiseaseVO> getDiseaseTree() {
        log.info("获取疾病分类树");

        // 查询所有一级分类
        List<Disease> level1List = diseaseMapper.selectLevel1();

        // 构建树形结构
        List<DiseaseVO> tree = new ArrayList<>();
        for (Disease level1 : level1List) {
            DiseaseVO level1VO = convertToVO(level1);

            // 查询该一级分类下的所有二级分类
            List<Disease> level2List = diseaseMapper.selectByParentId(level1.getId());
            List<DiseaseVO> level2VOList = level2List.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            level1VO.setChildren(level2VOList);
            tree.add(level1VO);
        }

        return tree;
    }

    @Override
    public List<DiseaseVO> getLevel1Diseases() {
        log.info("获取所有一级分类");

        List<Disease> level1List = diseaseMapper.selectLevel1();

        return level1List.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiseaseVO> getLevel2Diseases(Long parentId) {
        log.info("获取二级分类，父ID：{}", parentId);

        List<Disease> level2List = diseaseMapper.selectByParentId(parentId);

        return level2List.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DiseaseVO getDiseaseByCode(String diseaseCode) {
        log.info("根据编码查询疾病，编码：{}", diseaseCode);

        Disease disease = diseaseMapper.selectByCode(diseaseCode);
        if (disease == null) {
            throw new BusinessException("疾病分类不存在");
        }

        return convertToVO(disease);
    }

    @Override
    public List<DiseaseVO> searchDiseasesByName(String diseaseName) {
        log.info("搜索疾病，疾病名称：{}", diseaseName);

        List<Disease> diseaseList = diseaseMapper.searchByName(diseaseName);

        return diseaseList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getDiseasePath(Long diseaseId) {
        log.info("获取疾病路径，疾病ID：{}", diseaseId);

        Disease disease = diseaseMapper.selectById(diseaseId);
        if (disease == null) {
            throw new BusinessException("疾病分类不存在");
        }

        StringBuilder path = new StringBuilder(disease.getDiseaseName());

        // 如果是一级分类，直接返回
        if (disease.getParentId() == 0) {
            return path.toString();
        }

        // 递归查询父级分类
        Disease parent = diseaseMapper.selectById(disease.getParentId());
        if (parent != null) {
            path.insert(0, parent.getDiseaseName() + " > ");
        }

        return path.toString();
    }

    /**
     * 转换为疾病VO
     */
    private DiseaseVO convertToVO(Disease disease) {
        DiseaseVO vo = new DiseaseVO();
        BeanUtils.copyProperties(disease, vo);
        return vo;
    }
}
