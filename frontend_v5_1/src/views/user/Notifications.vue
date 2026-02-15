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
        </el-radio-group>
        <el-button v-if="hasUnread" type="primary" @click="markAllRead">
          全部已读
        </el-button>
      </div>

      <!-- 通知列表 -->
      <div v-if="notificationList.length > 0" class="notification-list">
        <div
          v-for="notification in notificationList"
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.isRead }"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon">
            <el-icon :size="24" :color="getNotificationColor(notification.type)">
              <component :is="getNotificationIcon(notification.type)" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-text">
              <span class="content">{{ notification.content }}</span>
              <span v-if="notification.topicTitle" class="topic-title">《{{ notification.topicTitle }}》</span>
              <span class="time">{{ formatTime(notification.createTime) }}</span>
            </div>
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
import { Check, Star, ChatDotRound, ChatLineSquare } from '@element-plus/icons-vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
import { formatRelativeTime } from '@/utils/date'
import { useNotificationStore } from '@/stores'
import Empty from '@/components/common/Empty.vue'

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
    reply: ChatLineSquare
  }
  return icons[type] || Check
}

// 获取通知颜色
const getNotificationColor = (type) => {
  const colors = {
    like: '#67c23a',
    collect: '#E6A23C',
    comment: '#409eff',
    reply: '#909399'
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

  // 跳转到相关内容（使用 relatedId 作为话题ID）
  if (notification.relatedId) {
    router.push(`/community/topic/${notification.relatedId}`)
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
