package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.TopicPublishDTO;
import com.chen.HospitalSelection.dto.TopicUpdateDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.CommentMapper;
import com.chen.HospitalSelection.mapper.LikeMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Comment;
import com.chen.HospitalSelection.model.Like;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.service.CommunityService;
import com.chen.HospitalSelection.vo.CommentVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.TopicDetailVO;
import com.chen.HospitalSelection.vo.TopicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社区服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<TopicVO> getTopicList(PageQueryDTO dto) {
        log.info("分页查询话题列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 查询所有话题列表
        List<Topic> allTopics = topicMapper.selectAll();
        Long total = (long) allTopics.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allTopics.size());
        List<Topic> topicList = allTopics.subList(fromIndex, toIndex);

        List<TopicVO> voList = topicList.stream()
                .map(this::convertToTopicVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<TopicVO> getTopicsByBoard(String boardLevel1, String boardLevel2, PageQueryDTO dto) {
        log.info("根据板块查询话题，一级板块：{}，二级板块：{}", boardLevel1, boardLevel2);

        // 查询所有话题
        List<Topic> allTopics = topicMapper.selectAll();

        // 按板块筛选
        List<Topic> filteredTopics = allTopics.stream()
                .filter(topic -> {
                    boolean matchLevel1 = boardLevel1 == null || boardLevel1.equals(topic.getBoardLevel1());
                    boolean matchLevel2 = boardLevel2 == null || boardLevel2.equals(topic.getBoardLevel2());
                    return matchLevel1 && matchLevel2;
                })
                .collect(Collectors.toList());

        Long total = (long) filteredTopics.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), filteredTopics.size());
        List<Topic> topicList = filteredTopics.subList(fromIndex, toIndex);

        List<TopicVO> voList = topicList.stream()
                .map(this::convertToTopicVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<TopicVO> getTopicsByDisease(String diseaseCode, PageQueryDTO dto) {
        log.info("根据疾病查询话题，疾病编码：{}", diseaseCode);

        // 查询指定疾病的所有话题
        List<Topic> allTopics = topicMapper.selectByDiseaseCode(diseaseCode);
        Long total = (long) allTopics.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allTopics.size());
        List<Topic> topicList = allTopics.subList(fromIndex, toIndex);

        List<TopicVO> voList = topicList.stream()
                .map(this::convertToTopicVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public TopicDetailVO getTopicDetail(Long topicId, Long userId) {
        log.info("查询话题详情，话题ID：{}", topicId);

        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new BusinessException("话题不存在");
        }

        TopicDetailVO detailVO = new TopicDetailVO();
        BeanUtils.copyProperties(topic, detailVO);

        // 查询作者信息 - 使用正确的字段名
        User author = userMapper.selectById(topic.getUserId());
        if (author != null) {
            // TopicDetailVO 使用 userId, nickname, avatar 字段
            detailVO.setNickname(author.getNickname());
            detailVO.setAvatar(author.getAvatar());
        }

        // 查询评论列表
        List<Comment> commentList = commentMapper.selectByTopicId(topicId);
        List<CommentVO> commentVOList = commentList.stream()
                .map(this::convertToCommentVO)
                .collect(Collectors.toList());
        detailVO.setComments(commentVOList);

        // 如果传入了用户ID，检查是否点赞/收藏
        if (userId != null) {
            boolean isLiked = likeMapper.countByUserAndTarget(userId, 1, topicId) > 0;
            detailVO.setIsLiked(isLiked);
            // 收藏状态需要查询Collection表，这里暂时省略
        }

        return detailVO;
    }

    @Override
    @Transactional
    public Long publishTopic(Long userId, TopicPublishDTO dto) {
        log.info("发布话题，用户ID：{}", userId);

        Topic topic = new Topic();
        BeanUtils.copyProperties(dto, topic);
        topic.setUserId(userId);
        topic.setLikeCount(0);
        topic.setCommentCount(0);
        topic.setCollectCount(0);
        topic.setViewCount(0);
        topic.setStatus(1); // 正常状态
        topic.setIsDeleted(0);
        topic.setCreateTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());

        topicMapper.insert(topic);

        log.info("话题发布成功，话题ID：{}", topic.getId());
        return topic.getId();
    }

    @Override
    @Transactional
    public void updateTopic(Long userId, Long topicId, TopicUpdateDTO dto) {
        log.info("修改话题，用户ID：{}，话题ID：{}", userId, topicId);

        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new BusinessException("话题不存在");
        }

        // 检查权限：只有作者可以修改
        if (!topic.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此话题");
        }

        topic.setTitle(dto.getTitle());
        topic.setContent(dto.getContent());
        if (dto.getDiseaseCode() != null) {
            topic.setDiseaseCode(dto.getDiseaseCode());
        }
        topic.setUpdateTime(LocalDateTime.now());

        topicMapper.updateById(topic);

        log.info("话题修改成功，话题ID：{}", topicId);
    }

    @Override
    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        log.info("删除话题，用户ID：{}，话题ID：{}", userId, topicId);

        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new BusinessException("话题不存在");
        }

        // 检查权限：只有作者可以删除
        if (!topic.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此话题");
        }

        topicMapper.deleteById(topicId);

        log.info("话题删除成功，话题ID：{}", topicId);
    }

    @Override
    public PageResult<CommentVO> getCommentList(Long topicId, PageQueryDTO dto) {
        log.info("查询评论列表，话题ID：{}", topicId);

        List<Comment> allComments = commentMapper.selectByTopicId(topicId);
        Long total = (long) allComments.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allComments.size());
        List<Comment> commentList = allComments.subList(fromIndex, toIndex);

        List<CommentVO> voList = commentList.stream()
                .map(this::convertToCommentVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    @Transactional
    public Long addComment(Long userId, CommentDTO dto) {
        log.info("发表评论，用户ID：{}，话题ID：{}", userId, dto.getTopicId());

        Comment comment = new Comment();
        comment.setTopicId(dto.getTopicId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0);
        comment.setLikeCount(0);
        comment.setIsDeleted(0);
        comment.setCreateTime(LocalDateTime.now());

        commentMapper.insert(comment);

        // 更新话题的评论数
        topicMapper.incrementCommentCount(dto.getTopicId());

        log.info("评论发表成功，评论ID：{}", comment.getId());
        return comment.getId();
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("删除评论，用户ID：{}，评论ID：{}", userId, commentId);

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 检查权限：只有作者可以删除
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }

        commentMapper.deleteById(commentId);

        // 更新话题的评论数
        topicMapper.decrementCommentCount(comment.getTopicId());

        log.info("评论删除成功，评论ID：{}", commentId);
    }

    @Override
    @Transactional
    public void likeTopic(Long userId, Long topicId) {
        log.info("点赞话题，用户ID：{}，话题ID：{}", userId, topicId);

        // 检查是否已点赞
        int count = likeMapper.countByUserAndTarget(userId, 1, topicId);
        if (count > 0) {
            throw new BusinessException("已经点赞过了");
        }

        // 添加点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(1); // 1=话题
        like.setTargetId(topicId);
        like.setIsDeleted(0);
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);

        // 更新话题的点赞数
        topicMapper.incrementLikeCount(topicId);

        log.info("点赞成功，话题ID：{}", topicId);
    }

    @Override
    @Transactional
    public void unlikeTopic(Long userId, Long topicId) {
        log.info("取消点赞话题，用户ID：{}，话题ID：{}", userId, topicId);

        likeMapper.cancelLike(userId, 1, topicId);

        // 更新话题的点赞数
        topicMapper.decrementLikeCount(topicId);

        log.info("取消点赞成功，话题ID：{}", topicId);
    }

    @Override
    @Transactional
    public void likeComment(Long userId, Long commentId) {
        log.info("点赞评论，用户ID：{}，评论ID：{}", userId, commentId);

        int count = likeMapper.countByUserAndTarget(userId, 2, commentId);
        if (count > 0) {
            throw new BusinessException("已经点赞过了");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(2); // 2=评论
        like.setTargetId(commentId);
        like.setIsDeleted(0);
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);

        commentMapper.incrementLikeCount(commentId);

        log.info("点赞成功，评论ID：{}", commentId);
    }

    @Override
    @Transactional
    public void unlikeComment(Long userId, Long commentId) {
        log.info("取消点赞评论，用户ID：{}，评论ID：{}", userId, commentId);

        likeMapper.cancelLike(userId, 2, commentId);

        commentMapper.decrementLikeCount(commentId);

        log.info("取消点赞成功，评论ID：{}", commentId);
    }

    @Override
    public PageResult<TopicVO> getMyTopics(Long userId, PageQueryDTO dto) {
        log.info("查询我的话题，用户ID：{}", userId);

        // 查询用户的所有话题
        List<Topic> allTopics = topicMapper.selectByUserId(userId);
        Long total = (long) allTopics.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allTopics.size());
        List<Topic> topicList = allTopics.subList(fromIndex, toIndex);

        List<TopicVO> voList = topicList.stream()
                .map(this::convertToTopicVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<CommentVO> getMyComments(Long userId, PageQueryDTO dto) {
        log.info("查询我的评论，用户ID：{}", userId);

        // 查询用户的所有评论
        List<Comment> allComments = commentMapper.selectByUserId(userId);
        Long total = (long) allComments.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allComments.size());
        List<Comment> commentList = allComments.subList(fromIndex, toIndex);

        List<CommentVO> voList = commentList.stream()
                .map(this::convertToCommentVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long topicId) {
        topicMapper.incrementViewCount(topicId);
    }

    @Override
    public PageResult<TopicVO> getHotTopics(PageQueryDTO dto) {
        log.info("查询热门话题");

        // 查询前100条热门话题
        List<Topic> allHotTopics = topicMapper.selectHotTopics(100);
        Long total = (long) allHotTopics.size();

        // 内存分页
        int fromIndex = (dto.getPage() - 1) * dto.getPageSize();
        int toIndex = Math.min(fromIndex + dto.getPageSize(), allHotTopics.size());
        List<Topic> topicList = allHotTopics.subList(fromIndex, toIndex);

        List<TopicVO> voList = topicList.stream()
                .map(this::convertToTopicVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    /**
     * 转换为话题VO
     */
    private TopicVO convertToTopicVO(Topic topic) {
        TopicVO vo = new TopicVO();
        BeanUtils.copyProperties(topic, vo);
        return vo;
    }

    /**
     * 转换为评论VO
     */
    private CommentVO convertToCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 查询评论作者信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        return vo;
    }
}
