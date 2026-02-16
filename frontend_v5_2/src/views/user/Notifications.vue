<template>
  <div class="notifications-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">互动通知</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="notifications-card">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="activeTab" @change="loadNotifications">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="like">点赞</el-radio-button>
          <el-radio-button value="collect">收藏</el-radio-button>
          <el-radio-button value="comment">评论</el-radio-button>
          <el-radio-button value="reply">回复</el-radio-button>
          <el-radio-button value="report_handle">举报</el-radio-button>
        </el-radio-group>
        <div class="filter-actions">
          <el-button v-if="hasRead" @click="deleteAllRead">
            删除所有已读
          </el-button>
          <el-button v-if="hasUnread" type="primary" @click="markAllRead">
            全部已读
          </el-button>
        </div>
      </div>

      <!-- 通知列表 -->
      <div v-if="notificationList.length > 0" class="notification-list">
        <div
          v-for="notification in notificationList"
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead }"
        >
          <div class="notification-icon">
            <el-icon :size="24" :color="getNotificationColor(notification.type)">
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          <div class="notification-content" @click="handleNotificationClick(notification)">
            <div class="notification-text">
              <span class="content">{{ notification.content }}</span>
              <span v-if="notification.topicTitle" class="topic-title">《{{ notification.topicTitle }}》</span>
              <span class="time">{{ formatTime(notification.createTime) }}</span>
            </div>
          </div>
          <div class="notification-actions">
            <el-button
              text
              size="small"
              type="danger"
              @click.stop="handleDeleteNotification(notification)"
            >
              删除
            </el-button>
          </div>
          <div v-if="!notification.isRead" class="unread-dot"></div>
        </div>
      </div>
      <Empty v-else description="暂无通知" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadNotifications"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Star, ChatDotRound, ChatLineSquare, Warning } from '@element-plus/icons-vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead, deleteNotification, deleteAllReadNotifications } from '@/api/notification'
import { formatRelativeTime } from '@/utils/date'
import { useNotificationStore } from '@/stores'
import Empty from '@/components/common/Empty.vue'
import { ElMessageBox } from 'element-plus'
import { getTopicDetail } from '@/api/community'

const router = useRouter()
const notificationStore = useNotificationStore()

const loading = ref(false)
const notificationList = ref([])
const total = ref(0)
const activeTab = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const hasUnread = computed(() => {
  return notificationList.value.some(n => !n.isRead)
})

const hasRead = computed(() => {
  return notificationList.value.some(n => n.isRead)
})

// 格式化时间
const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const icons = {
    like: Check,
    collect: Star,
    comment: ChatDotRound,
    reply: ChatLineSquare,
    report_handle: Warning
  }
  return icons[type] || Check
}

// 获取通知颜色
const getNotificationColor = (type) => {
  const colors = {
    like: '#67c23a',
    collect: '#E6A23C',
    comment: '#409eff',
    reply: '#909399',
    report_handle: '#F56C6C'
  }
  return colors[type] || '#909399'
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true
    const res = await getNotifications()
    const allNotifications = res.data || []

    // 前端过滤：根据类型筛选
    let filteredNotifications = allNotifications
    if (activeTab.value) {
      filteredNotifications = allNotifications.filter(n => n.type === activeTab.value)
    }

    // 前端分页
    total.value = filteredNotifications.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    notificationList.value = filteredNotifications.slice(start, end)
  } catch (error) {
    console.error('加载通知失败:', error)
    notificationList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 点击通知
const handleNotificationClick = async (notification) => {
  // 检查话题是否存在
  if (notification.relatedId) {
    try {
      await getTopicDetail(notification.relatedId)

      // 话题存在，标记为已读并跳转
      if (!notification.isRead) {
        try {
          await markNotificationRead(notification.id)
          notification.isRead = true
          // 更新全局未读通知数量
          notificationStore.fetchUnreadCount()
        } catch (error) {
          console.error('标记已读失败:', error)
        }
      }

      router.push(`/community/topic/${notification.relatedId}`)
    } catch (error) {
      // 话题不存在，提示并删除通知
      console.error('话题不存在:', error)
      ElMessage.warning('该话题已被删除')

      try {
        await deleteNotification(notification.id)
        // 从列表中移除
        const index = notificationList.value.findIndex(n => n.id === notification.id)
        if (index > -1) {
          notificationList.value.splice(index, 1)
          total.value--
        }
        // 更新全局未读通知数量
        notificationStore.fetchUnreadCount()
      } catch (deleteError) {
        console.error('删除通知失败:', deleteError)
      }
    }
  }
}

// 全部标记已读
const markAllRead = async () => {
  try {
    await markAllNotificationsRead()
    ElMessage.success('已全部标记为已读')
    loadNotifications()
    // 更新全局未读通知数量
    notificationStore.fetchUnreadCount()
  } catch (error) {
    console.error('全部标记已读失败:', error)
  }
}

// 删除通知
const handleDeleteNotification = async (notification) => {
  try {
    await ElMessageBox.confirm(
      '确定删除这条通知吗？删除后无法恢复。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteNotification(notification.id)
    ElMessage.success('删除成功')
    // 从列表中移除
    const index = notificationList.value.findIndex(n => n.id === notification.id)
    if (index > -1) {
      notificationList.value.splice(index, 1)
      total.value--
    }
    // 更新全局未读通知数量
    notificationStore.fetchUnreadCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除通知失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '删除失败'
      ElMessage.error(errorMsg)
    }
  }
}

// 删除所有已读通知
const deleteAllRead = async () => {
  try {
    const readCount = notificationList.value.filter(n => n.isRead).length
    if (readCount === 0) {
      ElMessage.info('没有已读通知可删除')
      return
    }

    await ElMessageBox.confirm(
      `确定删除所有已读通知吗？将删除 ${readCount} 条通知。`,
      '批量删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await deleteAllReadNotifications()
    ElMessage.success(res.data || '删除成功')
    loadNotifications()
    // 更新全局未读通知数量
    notificationStore.fetchUnreadCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除所有已读失败:', error)
      const errorMsg = error.response?.data?.message || error.message || '删除失败'
      ElMessage.error(errorMsg)
    }
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.page = 1
  pagination.pageSize = size
  loadNotifications()
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped lang="scss">
.notifications-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .notifications-card {
    margin-top: 20px;
    min-height: 500px;
  }

  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .notification-list {
    .notification-item {
      position: relative;
      display: flex;
      gap: 12px;
      padding: 12px 16px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 12px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e6f7ff;
      }

      &.unread {
        background: #fff;
        border: 1px solid #409eff;

        .unread-dot {
          position: absolute;
          top: 12px;
          right: 12px;
          width: 8px;
          height: 8px;
          background: #f56c6c;
          border-radius: 50%;
        }
      }

      .notification-icon {
        flex-shrink: 0;
        padding-top: 2px;
      }

      .notification-content {
        flex: 1;
        min-width: 0;

        .notification-text {
          display: flex;
          align-items: center;
          gap: 8px;
          flex-wrap: wrap;
          margin: 0;
          font-size: 14px;
          line-height: 1.5;
          color: #606266;

          .content {
            font-weight: 500;
            color: #303133;
          }

          .topic-title {
            color: #409eff;
            font-size: 14px;
          }

          .time {
            margin-left: auto;
            font-size: 12px;
            color: #909399;
            flex-shrink: 0;
          }
        }
      }
    }
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
