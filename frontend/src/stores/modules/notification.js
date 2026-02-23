import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount } from '@/api/notification'

/**
 * 通知状态管理
 */
export const useNotificationStore = defineStore('notification', () => {
  // 通知列表
  const notifications = ref([])

  // 未读通知数
  const unreadCount = ref(0)

  // 定时器ID
  let pollTimer = null

  // 轮询间隔（10秒）
  const POLL_INTERVAL = 10000

  /**
   * 启动定时轮询
   */
  function startPolling() {
    // 先执行一次
    fetchUnreadCount()

    // 清除旧的定时器
    if (pollTimer) {
      clearInterval(pollTimer)
    }

    // 启动新的定时器
    pollTimer = setInterval(() => {
      fetchUnreadCount()
    }, POLL_INTERVAL)
  }

  /**
   * 停止定时轮询
   */
  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
  }

  /**
   * 获取未读通知数量
   */
  async function fetchUnreadCount() {
    try {
      const res = await getUnreadCount()
      const count = res.data || 0
      console.log('[通知] 未读通知数量:', count)
      unreadCount.value = count
    } catch (error) {
      console.error('[通知] 获取未读通知数量失败:', error)
    }
  }

  /**
   * 设置通知列表
   */
  function setNotifications(list) {
    notifications.value = list
  }

  /**
   * 设置未读通知数
   */
  function setUnreadCount(count) {
    unreadCount.value = count
  }

  /**
   * 标记通知为已读
   */
  function markAsRead(notificationId) {
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
  }

  /**
   * 批量标记为已读
   */
  function markAllAsRead() {
    notifications.value.forEach(n => {
      n.isRead = true
    })
    unreadCount.value = 0
  }

  /**
   * 删除通知
   */
  function removeNotification(notificationId) {
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index > -1) {
      const notification = notifications.value[index]
      if (!notification.isRead) {
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
      notifications.value.splice(index, 1)
    }
  }

  /**
   * 添加新通知
   */
  function addNotification(notification) {
    notifications.value.unshift(notification)
    if (!notification.isRead) {
      unreadCount.value++
    }
  }

  return {
    notifications,
    unreadCount,
    fetchUnreadCount,
    startPolling,
    stopPolling,
    setNotifications,
    setUnreadCount,
    markAsRead,
    markAllAsRead,
    removeNotification,
    addNotification
  }
})
