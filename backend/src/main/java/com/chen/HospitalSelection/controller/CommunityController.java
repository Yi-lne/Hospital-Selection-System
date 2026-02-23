package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.TopicPublishDTO;
import com.chen.HospitalSelection.dto.TopicUpdateDTO;
import com.chen.HospitalSelection.service.CommunityService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.CommentVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import com.chen.HospitalSelection.vo.TopicDetailVO;
import com.chen.HospitalSelection.vo.TopicVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 社区功能接口
 * 基础路径：/api/community
 *
 * @author chen
 */
@RestController
@RequestMapping("/community")
@Api(tags = "社区管理")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    /**
     * 话题列表（分页）
     * 接口路径：GET /api/community/topics
     * 是否需要登录：否
     *
     * @param dto       分页查询参数
     * @param boardLevel1 一级板块（可选）
     * @param boardLevel2 二级板块（可选）
     * @return 话题列表
     */
    @GetMapping("/topics")
    @ApiOperation("话题列表（分页）")
    public Result<PageResult<TopicVO>> getTopicList(@Valid PageQueryDTO dto,
                                                     @RequestParam(required = false) String boardLevel1,
                                                     @RequestParam(required = false) String boardLevel2,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String keyword) {
        PageResult<TopicVO> pageResult;
        // If board parameters are provided, use getTopicsByBoard, otherwise use getTopicList
        if (boardLevel1 != null || boardLevel2 != null) {
            pageResult = communityService.getTopicsByBoard(boardLevel1, boardLevel2, dto, sortBy, keyword);
        } else {
            pageResult = communityService.getTopicList(dto, sortBy, keyword);
        }
        return Result.success(pageResult);
    }

    /**
     * 话题详情
     * 接口路径：GET /api/community/topic/{id}
     * 是否需要登录：否
     *
     * @param id 话题ID
     * @return 话题详情及评论列表
     */
    @GetMapping("/topic/{id}")
    @ApiOperation("话题详情")
    public Result<TopicDetailVO> getTopicDetail(@PathVariable Long id, HttpServletRequest request) {
        // Try to get userId if user is logged in, otherwise pass null
        Long userId = null;
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // User not logged in, userId remains null
        }
        TopicDetailVO topicDetailVO = communityService.getTopicDetail(id, userId);
        return Result.success(topicDetailVO);
    }

    /**
     * 发布话题
     * 接口路径：POST /api/community/topic
     * 是否需要登录：是
     *
     * @param dto 话题信息（标题、内容、板块等）
     * @return 发布的话题信息
     */
    @PostMapping("/topic")
    @ApiOperation("发布话题")
    public Result<TopicVO> publishTopic(@RequestBody @Valid TopicPublishDTO dto,
                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long topicId = communityService.publishTopic(userId, dto);
        // Get the created topic to return
        TopicDetailVO topicDetail = communityService.getTopicDetail(topicId, userId);
        TopicVO topicVO = new TopicVO();
        // Copy basic fields from detail to VO
        topicVO.setId(topicDetail.getId());
        topicVO.setUserId(topicDetail.getUserId());
        topicVO.setNickname(topicDetail.getNickname());
        topicVO.setAvatar(topicDetail.getAvatar());
        topicVO.setDiseaseCode(topicDetail.getDiseaseCode());
        topicVO.setBoardLevel1(topicDetail.getBoardLevel1());
        topicVO.setBoardLevel2(topicDetail.getBoardLevel2());
        topicVO.setBoardType(topicDetail.getBoardType());
        topicVO.setTitle(topicDetail.getTitle());
        // Generate content summary from full content (limit to 100 chars)
        String content = topicDetail.getContent();
        if (content != null && content.length() > 100) {
            topicVO.setContentSummary(content.substring(0, 100) + "...");
        } else {
            topicVO.setContentSummary(content);
        }
        topicVO.setLikeCount(topicDetail.getLikeCount());
        topicVO.setCommentCount(topicDetail.getCommentCount());
        topicVO.setCollectCount(topicDetail.getCollectCount());
        topicVO.setViewCount(topicDetail.getViewCount());
        topicVO.setIsLiked(topicDetail.getIsLiked());
        topicVO.setIsCollected(topicDetail.getIsCollected());
        topicVO.setCreateTime(topicDetail.getCreateTime());
        return Result.success(topicVO, "发布成功");
    }

    /**
     * 修改话题
     * 接口路径：PUT /api/community/topic/{id}
     * 是否需要登录：是
     *
     * @param id  话题ID
     * @param dto 修改的话题信息
     * @return 修改后的话题信息
     */
    @PutMapping("/topic/{id}")
    @ApiOperation("修改话题")
    public Result<Void> updateTopic(@PathVariable Long id,
                                   @RequestBody @Valid TopicUpdateDTO dto,
                                   HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.updateTopic(userId, id, dto);
        return Result.success(null, "修改成功");
    }

    /**
     * 删除话题
     * 接口路径：DELETE /api/community/topic/{id}
     * 是否需要登录：是
     *
     * @param id 话题ID
     * @return 删除结果
     */
    @DeleteMapping("/topic/{id}")
    @ApiOperation("删除话题")
    public Result<Void> deleteTopic(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.deleteTopic(userId, id);
        return Result.success(null, "删除成功");
    }

    /**
     * 评论列表
     * 接口路径：GET /api/community/topic/{id}/comments
     * 是否需要登录：否
     *
     * @param id 话题ID
     * @param dto 分页查询参数（可选）
     * @return 该话题的评论列表
     */
    @GetMapping("/topic/{id}/comments")
    @ApiOperation("评论列表")
    public Result<PageResult<CommentVO>> getTopicComments(@PathVariable Long id,
                                                          @Valid PageQueryDTO dto) {
        PageResult<CommentVO> comments = communityService.getCommentList(id, dto);
        return Result.success(comments);
    }

    /**
     * 发表评论
     * 接口路径：POST /api/community/comment
     * 是否需要登录：是
     *
     * @param dto 评论信息（话题ID、内容等）
     * @return 发表的评论信息
     */
    @PostMapping("/comment")
    @ApiOperation("发表评论")
    public Result<CommentVO> addComment(@RequestBody @Valid CommentDTO dto,
                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long commentId = communityService.addComment(userId, dto);
        // TODO: Return the created comment - for now return success with id
        CommentVO commentVO = new CommentVO();
        commentVO.setId(commentId);
        return Result.success(commentVO, "评论成功");
    }

    /**
     * 回复评论
     * 接口路径：POST /api/community/comment/reply
     * 是否需要登录：是
     *
     * @param dto 评论信息（话题ID、父评论ID、内容等）
     * @return 发表的回复评论信息
     */
    @PostMapping("/comment/reply")
    @ApiOperation("回复评论")
    public Result<CommentVO> replyComment(@RequestBody @Valid CommentDTO dto,
                                         HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        // parentId必须大于0才能回复
        if (dto.getParentId() == null || dto.getParentId() <= 0) {
            return Result.error(400, "回复评论必须指定父评论ID");
        }
        Long commentId = communityService.addComment(userId, dto);
        CommentVO commentVO = new CommentVO();
        commentVO.setId(commentId);
        return Result.success(commentVO, "回复成功");
    }

    /**
     * 删除评论
     * 接口路径：DELETE /api/community/comment/{id}
     * 是否需要登录：是
     *
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/comment/{id}")
    @ApiOperation("删除评论")
    public Result<Void> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.deleteComment(userId, id);
        return Result.success(null, "删除成功");
    }

    /**
     * 点赞话题
     * 接口路径：POST /api/community/like/topic
     * 是否需要登录：是
     *
     * @param topicId 话题ID
     * @return 点赞结果
     */
    @PostMapping("/like/topic")
    @ApiOperation("点赞话题")
    public Result<Void> likeTopic(@RequestParam Long topicId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.likeTopic(userId, topicId);
        return Result.success(null, "点赞成功");
    }

    /**
     * 取消点赞话题
     * 接口路径：DELETE /api/community/like/topic/{id}
     * 是否需要登录：是
     *
     * @param id 话题ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/like/topic/{id}")
    @ApiOperation("取消点赞话题")
    public Result<Void> unlikeTopic(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.unlikeTopic(userId, id);
        return Result.success(null, "取消点赞成功");
    }

    /**
     * 点赞评论
     * 接口路径：POST /api/community/like/comment
     * 是否需要登录：是
     *
     * @param commentId 评论ID
     * @return 点赞结果
     */
    @PostMapping("/like/comment")
    @ApiOperation("点赞评论")
    public Result<Void> likeComment(@RequestParam Long commentId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.likeComment(userId, commentId);
        return Result.success(null, "点赞成功");
    }

    /**
     * 取消点赞评论
     * 接口路径：DELETE /api/community/like/comment/{id}
     * 是否需要登录：是
     *
     * @param id 评论ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/like/comment/{id}")
    @ApiOperation("取消点赞评论")
    public Result<Void> unlikeComment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.unlikeComment(userId, id);
        return Result.success(null, "取消点赞成功");
    }

    /**
     * 我的话题
     * 接口路径：GET /api/community/my/topics
     * 是否需要登录：是
     *
     * @param dto 分页查询参数
     * @return 我发布的话题列表
     */
    @GetMapping("/my/topics")
    @ApiOperation("我的话题")
    public Result<PageResult<TopicVO>> getMyTopics(@Valid PageQueryDTO dto,
                                                   HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<TopicVO> pageResult = communityService.getMyTopics(userId, dto);
        return Result.success(pageResult);
    }

    /**
     * 我的评论
     * 接口路径：GET /api/community/my/comments
     * 是否需要登录：是
     *
     * @param dto 分页查询参数
     * @return 我的评论列表
     */
    @GetMapping("/my/comments")
    @ApiOperation("我的评论")
    public Result<PageResult<CommentVO>> getMyComments(@Valid PageQueryDTO dto,
                                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<CommentVO> pageResult = communityService.getMyComments(userId, dto);
        return Result.success(pageResult);
    }

    /**
     * 板块列表
     * 接口路径：GET /api/community/boards
     * 是否需要登录：否
     *
     * @return 所有板块分类
     */
    @GetMapping("/boards")
    @ApiOperation("板块列表")
    public Result<List<String>> getBoards() {
        // TODO: Implement service method for dynamic board retrieval
        // For now, return static board list
        List<String> boards = java.util.Arrays.asList(
            "心血管疾病",
            "内分泌疾病",
            "呼吸系统疾病",
            "消化系统疾病",
            "神经系统疾病",
            "肿瘤科",
            "儿科疾病"
        );
        return Result.success(boards);
    }

    /**
     * 获取当前登录用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer "
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员审核话题
     * 接口路径：PUT /api/community/admin/topic/{id}/moderate
     * 是否需要登录：是（管理员）
     *
     * @param id     话题ID
     * @param status 状态（1=正常，0=禁用，2=审核中）
     * @param reason 审核理由（可选）
     * @return 审核结果
     */
    @PutMapping("/admin/topic/{id}/moderate")
    @ApiOperation("管理员审核话题")
    public Result<Void> moderateTopic(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.moderateTopic(userId, id, status, reason);
        return Result.success(null, "审核成功");
    }

    /**
     * 管理员删除话题
     * 接口路径：DELETE /api/community/admin/topic/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id     话题ID
     * @param reason 删除理由（可选）
     * @return 删除结果
     */
    @DeleteMapping("/admin/topic/{id}")
    @ApiOperation("管理员删除话题")
    public Result<Void> deleteTopicByAdmin(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.deleteTopicByAdmin(userId, id, reason);
        return Result.success(null, "删除成功");
    }

    /**
     * 管理员删除评论
     * 接口路径：DELETE /api/community/admin/comment/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id     评论ID
     * @param reason 删除理由（可选）
     * @return 删除结果
     */
    @DeleteMapping("/admin/comment/{id}")
    @ApiOperation("管理员删除评论")
    public Result<Void> deleteCommentByAdmin(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        communityService.deleteCommentByAdmin(userId, id, reason);
        return Result.success(null, "删除成功");
    }

    /**
     * 获取待审核话题列表
     * 接口路径：GET /api/community/admin/topics/pending
     * 是否需要登录：是（管理员）
     *
     * @param dto 分页查询参数
     * @return 待审核话题列表
     */
    @GetMapping("/admin/topics/pending")
    @ApiOperation("获取待审核话题列表")
    public Result<PageResult<TopicVO>> getPendingTopics(@Valid PageQueryDTO dto) {
        PageResult<TopicVO> pageResult = communityService.getPendingTopics(dto);
        return Result.success(pageResult);
    }

    /**
     * 获取所有话题（管理视角）
     * 接口路径：GET /api/community/admin/topics/all
     * 是否需要登录：是（管理员）
     *
     * @param dto 分页查询参数
     * @return 所有话题列表
     */
    @GetMapping("/admin/topics/all")
    @ApiOperation("获取所有话题（管理视角）")
    public Result<PageResult<TopicVO>> getAllTopics(@Valid PageQueryDTO dto) {
        PageResult<TopicVO> pageResult = communityService.getAllTopics(dto);
        return Result.success(pageResult);
    }
}
