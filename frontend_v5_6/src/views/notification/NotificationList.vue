<template>
  <div class="notification-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">通知列表</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="list-card">
      <template #header>
        <div class="header-actions">
          <span>通知 ({{ notifications.length }})</span>
          <el-button
            v-if="notifications.length > 0"
            link
            type="primary"
            @click="markAllRead"
          >
            全部已读
          </el-button>
        </div>
      </template>

      <template v-if="notifications.length > 0">
        <div
          v-for="notif in notifications"
          :key="notif.id"
          :class="['notification-item', { 'is-read': notif.isRead }]"
          @click="handleClick(notif)"
        >
          <div class="notif-main">
            <div class="notif-left">
              <el-tag :type="getTagType(notif.type)" size="small">
                {{ notif.typeDesc }}
              </el-tag>
              <div class="notif-content">
                <span class="notif-text">{{ notif.content }}</span>
                <span class="notif-time">{{ formatTime(notif.createTime) }}</span>
              </div>
            </div>
            <div class="notif-actions">
              <el-button
                v-if="!notif.isRead"
                link
                type="primary"
                size="small"
                @click.stop="markRead(notif.id)"
              >
                标为已读
              </el-button>
            </div>
          </div>
          <el-button
            link
            type="danger"
            size="small"
            class="delete-btn"
            @click.stop="deleteNotif(notif.id)"
          >
            删除
          </el-button>
        </div>
      </template>
      <Empty v-else description="暂无通知" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useNotificationStore } from '@/stores'
import { getNotifications, markNotificationRead, markAllNotificationsRead, deleteNotification } from '@/api/notification'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()
const notificationStore = useNotificationStore()

const loading = ref(false)
const notifications = ref([])

const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 获取通知类型标签颜色
const getTagType = (type) => {
  const typeMap = {
    'comment': 'primary',
    'reply': 'success',
    'delete_topic': 'danger',
    'delete_comment': 'warning',
    'report_handle': 'info'
  }
  return typeMap[type] || 'info'
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true
    const res = await getNotifications()
    notifications.value = res.data
    notificationStore.setNotifications(res.data)

    // 计算未读数
    const unreadCount = res.data.filter((n) => !n.isRead).length
    notificationStore.setUnreadCount(unreadCount)
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

// 标记单个通知为已读
const markRead = async (id, showMessage = true) => {
  try {
    await markNotificationRead(id)
    notificationStore.markAsRead(id)
    if (showMessage) {
      ElMessage.success('标记成功')
    }
  } catch (error) {
    console.error('标记失败:', error)
    if (showMessage) {
      ElMessage.error('标记失败')
    }
  }
}

// 批量标记为已读
const markAllRead = async () => {
  try {
    await markAllNotificationsRead()
    notificationStore.markAllAsRead()
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    console.error('批量标记失败:', error)
    ElMessage.error('操作失败')
  }
}

// 删除通知
const deleteNotif = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteNotification(id)
    notificationStore.removeNotification(id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 点击通知
const handleClick = (notif) => {
  // 如果是未读通知，先标记为已读（不显示提示）
  if (!notif.isRead) {
    markRead(notif.id, false)
  }

  // 根据通知类型跳转到相应页面
  if (notif.type === 'comment' || notif.type === 'delete_topic' || notif.type === 'report_handle') {
    // 话题相关通知，跳转到话题详情
    router.push(`/community/topic/${notif.relatedId}`)
  } else if (notif.type === 'reply' || notif.type === 'delete_comment') {
    // 评论相关通知，暂时跳转到话题详情（TODO: 可以跳转到评论位置）
    router.push(`/community/topic/${notif.relatedId}`)
  }
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped lang="scss">
.notification-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .list-card {
    margin-top: 20px;
    min-height: 400px;

    .header-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .notification-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background 0.3s;
    background: #fff;
    gap: 12px;

    &:hover {
      background: #f5f7fa;
    }

    &.is-read {
      opacity: 0.6;
    }

    &:last-child {
      border-bottom: none;
    }

    .notif-main {
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex: 1;
      gap: 16px;

      .notif-left {
        display: flex;
        align-items: center;
        gap: 12px;
        flex: 1;
      }

      .notif-content {
        display: flex;
        align-items: center;
        gap: 16px;
        flex: 1;

        .notif-text {
          flex: 1;
          font-size: 14px;
          color: #303133;
          line-height: 1.6;
        }

        .notif-time {
          font-size: 12px;
          color: #909399;
          white-space: nowrap;
        }
      }

      .notif-actions {
        display: flex;
        gap: 12px;
        flex-shrink: 0;
      }
    }

    .delete-btn {
      flex-shrink: 0;
    }
  }
}
</style>
