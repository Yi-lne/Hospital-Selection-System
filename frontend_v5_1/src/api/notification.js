import Request from '@/utils/request'

/**
 * 获取通知列表
 */
export function getNotifications(params) {
  return Request.get('/notification/list', { params })
}

/**
 * 标记通知已读
 */
export function markNotificationRead(id) {
  return Request.put(`/notification/read/${id}`)
}

/**
 * 全部标记已读
 */
export function markAllNotificationsRead() {
  return Request.put('/notification/read/all')
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return Request.get('/notification/unread/count')
}

/**
 * 删除通知
 */
export function deleteNotification(id) {
  return Request.delete(`/notification/${id}`)
}
