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
            <div class="notification-header">
              <span class="type">{{ getNotificationLabel(notification.type) }}</span>
              <span class="time">{{ formatTime(notification.createTime) }}</span>
            </div>
            <p class="notification-text">{{ notification.content }}</p>
            <div v-if="notification.topicTitle" class="topic-ref">
              话题：{{ notification.topicTitle }}
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
import { Star, ChatDotRound, ChatLineSquare } from '@element-plus/icons-vue'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()

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
    like: Star,
    comment: ChatDotRound,
    reply: ChatLineSquare
  }
  return icons[type] || Star
}

// 获取通知颜色
const getNotificationColor = (type) => {
  const colors = {
    like: '#f56c6c',
    comment: '#409eff',
    reply: '#67c23a'
  }
  return colors[type] || '#909399'
}

// 获取通知标签
const getNotificationLabel = (type) => {
  const labels = {
    like: '点赞了你的内容',
    comment: '评论了你的话题',
    reply: '回复了你的评论'
  }
  return labels[type] || '通知'
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (activeTab.value) {
      params.type = activeTab.value
    }
    const res = await getNotifications(params)
    notificationList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载通知失败:', error)
    // Mock data for development
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
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 跳转到相关内容
  if (notification.topicId) {
    router.push(`/community/topic/${notification.topicId}`)
  }
}

// 全部标记已读
const markAllRead = async () => {
  try {
    await markAllNotificationsRead()
    ElMessage.success('已全部标记为已读')
    loadNotifications()
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
      gap: 16px;
      padding: 16px;
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
          top: 16px;
          right: 16px;
          width: 8px;
          height: 8px;
          background: #f56c6c;
          border-radius: 50%;
        }
      }

      .notification-icon {
        flex-shrink: 0;
      }

      .notification-content {
        flex: 1;

        .notification-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .type {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .time {
            font-size: 12px;
            color: #909399;
          }
        }

        .notification-text {
          margin: 0 0 8px;
          font-size: 14px;
          line-height: 1.6;
          color: #606266;
        }

        .topic-ref {
          font-size: 13px;
          color: #409eff;
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
