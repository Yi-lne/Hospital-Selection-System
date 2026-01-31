package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.QueryHistoryMapper;
import com.chen.HospitalSelection.model.QueryHistory;
import com.chen.HospitalSelection.service.QueryHistoryService;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.QueryHistoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询历史服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class QueryHistoryServiceImpl implements QueryHistoryService {

    @Autowired
    private QueryHistoryMapper queryHistoryMapper;

    @Override
    public PageResult<QueryHistoryVO> getQueryHistoryList(Long userId, PageQueryDTO dto) {
        log.info("查询历史列表，用户ID：{}", userId);

        // 获取所有数据
        List<QueryHistory> allList = queryHistoryMapper.selectByUserId(userId);
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<QueryHistory> historyList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<QueryHistoryVO> voList = historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    @Transactional
    public Long recordQuery(Long userId, Integer queryType, Long targetId, String queryParams) {
        log.info("记录查询行为，用户ID：{}，查询类型：{}，目标ID：{}", userId, queryType, targetId);

        QueryHistory history = new QueryHistory();
        history.setUserId(userId);
        history.setQueryType(queryType);
        history.setTargetId(targetId);
        history.setQueryParams(queryParams);
        history.setIsDeleted(0);
        history.setCreateTime(LocalDateTime.now());

        queryHistoryMapper.insert(history);

        return history.getId();
    }

    @Override
    @Transactional
    public void deleteQueryHistory(Long historyId, Long userId) {
        log.info("删除查询历史，历史ID：{}，用户ID：{}", historyId, userId);

        QueryHistory history = queryHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException("历史记录不存在");
        }

        // 检查权限：只能删除自己的历史
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此历史记录");
        }

        queryHistoryMapper.deleteById(historyId);

        log.info("查询历史删除成功，历史ID：{}", historyId);
    }

    @Override
    @Transactional
    public void clearQueryHistory(Long userId) {
        log.info("清空查询历史，用户ID：{}", userId);

        queryHistoryMapper.deleteByUserId(userId);

        log.info("查询历史清空成功，用户ID：{}", userId);
    }

    @Override
    public PageResult<QueryHistoryVO> getHospitalQueryHistory(Long userId, PageQueryDTO dto) {
        log.info("查询医院历史，用户ID：{}", userId);

        // 获取所有医院查询历史
        List<QueryHistory> allList = queryHistoryMapper.selectByUserIdAndType(userId, 1);
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<QueryHistory> historyList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<QueryHistoryVO> voList = historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<QueryHistoryVO> getDoctorQueryHistory(Long userId, PageQueryDTO dto) {
        log.info("查询医生历史，用户ID：{}", userId);

        // 获取所有医生查询历史
        List<QueryHistory> allList = queryHistoryMapper.selectByUserIdAndType(userId, 2);
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<QueryHistory> historyList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<QueryHistoryVO> voList = historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<QueryHistoryVO> getTopicQueryHistory(Long userId, PageQueryDTO dto) {
        log.info("查询话题历史，用户ID：{}", userId);

        // 获取所有话题查询历史
        List<QueryHistory> allList = queryHistoryMapper.selectByUserIdAndType(userId, 3);
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<QueryHistory> historyList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<QueryHistoryVO> voList = historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public List<QueryHistoryVO> getRecentQueries(Long userId, Integer limit) {
        log.info("获取最近查询记录，用户ID：{}，限制数量：{}", userId, limit);

        List<QueryHistory> historyList = queryHistoryMapper.selectRecentByUserId(userId, limit);

        return historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueryHistoryVO> getHotQueries(Integer queryType, Integer limit) {
        log.info("获取热门查询记录，查询类型：{}，限制数量：{}", queryType, limit);

        // 获取所有查询历史，在内存中统计热门查询
        List<QueryHistory> allList = queryHistoryMapper.selectAll();

        // 过滤查询类型
        List<QueryHistory> filteredList = allList.stream()
                .filter(h -> queryType == null || h.getQueryType().equals(queryType))
                .collect(Collectors.toList());

        // 按targetId分组统计
        java.util.Map<Long, Long> targetCountMap = new java.util.HashMap<>();
        for (QueryHistory history : filteredList) {
            Long targetId = history.getTargetId();
            targetCountMap.put(targetId, targetCountMap.getOrDefault(targetId, 0L) + 1);
        }

        // 按查询次数排序，取前limit个
        List<QueryHistory> hotList = targetCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> filteredList.stream()
                        .filter(h -> h.getTargetId().equals(entry.getKey()))
                        .findFirst()
                        .orElse(null))
                .filter(h -> h != null)
                .collect(Collectors.toList());

        return hotList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为查询历史VO
     */
    private QueryHistoryVO convertToVO(QueryHistory history) {
        QueryHistoryVO vo = new QueryHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }
}
