import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 通知状态管理
 */
export const useNotificationStore = defineStore('notification', () => {
  // 通知列表
  const notifications = ref([])

  // 未读通知数
  const unreadCount = ref(0)

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
    setNotifications,
    setUnreadCount,
    markAsRead,
    markAllAsRead,
    removeNotification,
    addNotification
  }
})
