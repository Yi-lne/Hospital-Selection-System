import Request from '@/utils/request'

/**
 * 获取话题列表（分页）
 */
export function getTopicList(page = 1, pageSize = 10) {
  return Request.get('/community/topics', {
    params: { page, pageSize }
  })
}

/**
 * 获取话题详情
 */
export function getTopicDetail(id) {
  return Request.get(`/community/topic/${id}`)
}

/**
 * 发布话题
 */
export function publishTopic(data) {
  return Request.post('/community/topic', data)
}

/**
 * 更新话题
 */
export function updateTopic(id, data) {
  return Request.put(`/community/topic/${id}`, data)
}

/**
 * 删除话题
 */
export function deleteTopic(id) {
  return Request.delete(`/community/topic/${id}`)
}

/**
 * 获取话题评论列表
 */
export function getCommentList(topicId, page = 1, pageSize = 10) {
  return Request.get(`/community/topic/${topicId}/comments`, {
    params: { page, pageSize }
  })
}

/**
 * 添加评论
 */
export function addComment(data) {
  return Request.post('/community/comment', data)
}

/**
 * 回复评论
 */
export function replyComment(data) {
  return Request.post('/community/comment/reply', data)
}

/**
 * 删除评论
 */
export function deleteComment(id) {
  return Request.delete(`/community/comment/${id}`)
}

/**
 * 点赞话题
 */
export function likeTopic(topicId) {
  return Request.post('/community/like/topic', null, {
    params: { topicId }
  })
}

/**
 * 取消点赞话题
 */
export function unlikeTopic(topicId) {
  return Request.delete(`/community/like/topic/${topicId}`)
}

/**
 * 点赞评论
 */
export function likeComment(commentId) {
  return Request.post('/community/like/comment', null, {
    params: { commentId }
  })
}

/**
 * 取消点赞评论
 */
export function unlikeComment(commentId) {
  return Request.delete(`/community/like/comment/${commentId}`)
}

/**
 * 我的话题
 */
export function getMyTopics(page = 1, pageSize = 10) {
  return Request.get('/community/my/topics', {
    params: { page, pageSize }
  })
}

/**
 * 我的评论
 */
export function getMyComments(params) {
  return Request.get('/community/my/comments', { params })
}

/**
 * 获取板块列表
 */
export function getBoardList() {
  return Request.get('/community/boards')
}

/**
 * 根据板块获取话题列表
 */
export function getTopicsByBoard(params) {
  return Request.get('/community/topics', { params })
}
