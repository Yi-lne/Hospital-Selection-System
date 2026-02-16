import Request from '@/utils/request'

/**
 * 获取会话列表
 */
export function getConversations() {
  return Request.get('/message/conversations')
}

/**
 * 获取与某用户的聊天记录
 */
export function getMessageHistory(userId, page = 1, pageSize = 50) {
  return Request.get(`/message/history/${userId}`, {
    params: { page, pageSize }
  })
}

/**
 * 发送私信
 */
export function sendMessage(data) {
  return Request.post('/message/send', data)
}

/**
 * 获取未读消息数
 */
export function getUnreadCount() {
  return Request.get('/message/unread/count')
}

/**
 * 标记消息为已读
 * 后端接口: PUT /message/read/{messageId}
 */
export function markAsRead(messageId) {
  return Request.put(`/message/read/${messageId}`)
}

/**
 * 批量标记消息为已读
 * 后端接口: PUT /message/read/batch
 * 后端期望: userIds: number[]
 */
export function markAllAsRead(userIds) {
  return Request.put('/message/read/batch', userIds)
}

/**
 * 删除消息
 * 后端接口: DELETE /message/{messageId}
 */
export function deleteMessage(messageId) {
  return Request.delete(`/message/${messageId}`)
}
