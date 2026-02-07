<template>
  <div class="user-center-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">个人中心</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="content-row">
      <!-- 左侧：用户信息卡片 -->
      <el-col :xs="24" :sm="8" :md="6">
        <el-card class="user-card">
          <div class="user-info">
            <el-avatar :size="100" :src="userStore.userAvatar || defaultAvatar">
              {{ userStore.userName?.charAt(0) || '用' }}
            </el-avatar>
            <h3>{{ userStore.userName }}</h3>
            <p class="phone">{{ userStore.userInfo?.phone || '' }}</p>
            <div class="stats">
              <div class="stat-item">
                <span class="value">{{ stats.topicCount || 0 }}</span>
                <span class="label">话题</span>
              </div>
              <div class="stat-item">
                <span class="value">{{ stats.collectionCount || 0 }}</span>
                <span class="label">收藏</span>
              </div>
              <div class="stat-item">
                <span class="value">{{ stats.commentCount || 0 }}</span>
                <span class="label">评论</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 快捷菜单 -->
        <el-menu :default-active="activeMenu" class="side-menu" @select="handleMenuSelect">
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="collection">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="topics">
            <el-icon><ChatDotRound /></el-icon>
            <span>我的话题</span>
          </el-menu-item>
          <el-menu-item index="medical-history">
            <el-icon><Document /></el-icon>
            <span>病史管理</span>
          </el-menu-item>
          <el-menu-item index="query-history">
            <el-icon><Clock /></el-icon>
            <span>查询历史</span>
          </el-menu-item>
          <el-menu-item index="messages">
            <el-icon><ChatLineSquare /></el-icon>
            <span>私信消息</span>
            <el-badge v-if="unreadCount > 0" :value="unreadCount" class="badge" />
          </el-menu-item>
        </el-menu>
      </el-col>

      <!-- 右侧：内容区域 -->
      <el-col :xs="24" :sm="16" :md="18">
        <el-card class="content-card">
          <!-- 统计数据 -->
          <div class="stats-section">
            <h3>数据统计</h3>
            <el-row :gutter="16">
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <el-icon :size="32" color="#409eff"><Document /></el-icon>
                  <div class="stat-info">
                    <div class="value">{{ stats.topicCount || 0 }}</div>
                    <div class="label">发布话题</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <el-icon :size="32" color="#67c23a"><Star /></el-icon>
                  <div class="stat-info">
                    <div class="value">{{ stats.collectionCount || 0 }}</div>
                    <div class="label">收藏内容</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <el-icon :size="32" color="#e6a23c"><ChatDotRound /></el-icon>
                  <div class="stat-info">
                    <div class="value">{{ stats.commentCount || 0 }}</div>
                    <div class="label">发表评论</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="12" :sm="6">
                <div class="stat-card">
                  <el-icon :size="32" color="#f56c6c"><View /></el-icon>
                  <div class="stat-info">
                    <div class="value">{{ stats.viewCount || 0 }}</div>
                    <div class="label">浏览次数</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 最近活动 -->
          <div class="activity-section">
            <h3>最近活动</h3>
            <el-timeline v-if="activities.length > 0">
              <el-timeline-item
                v-for="activity in activities"
                :key="activity.id"
                :timestamp="formatTime(activity.createTime)"
                placement="top"
              >
                <div class="activity-item">
                  <el-tag :type="getActivityType(activity.type)" size="small">
                    {{ getActivityLabel(activity.type) }}
                  </el-tag>
                  <span class="activity-content">{{ activity.content }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <Empty v-else description="暂无活动记录" />
          </div>

          <!-- 快捷操作 -->
          <div class="actions-section">
            <h3>快捷操作</h3>
            <div class="action-buttons">
              <el-button type="primary" :icon="Edit" @click="$router.push('/community/publish')">
                发布话题
              </el-button>
              <el-button :icon="Search" @click="$router.push('/hospital')">
                查找医院
              </el-button>
              <el-button :icon="User" @click="$router.push('/doctor')">
                查找医生
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Star, ChatDotRound, Document, Clock, ChatLineSquare,
  View, Edit, Search
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import { getMyTopics } from '@/api/community'
import { getCollectionList } from '@/api/collection'
import { getMessageList } from '@/api/message'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const stats = reactive({
  topicCount: 0,
  collectionCount: 0,
  commentCount: 0,
  viewCount: 0
})
const activities = ref([])
const unreadCount = ref(0)

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('/user/profile')) return 'profile'
  if (path.includes('/user/collection')) return 'collection'
  if (path.includes('/user/topics')) return 'topics'
  if (path.includes('/user/medical-history')) return 'medical-history'
  if (path.includes('/user/query-history')) return 'query-history'
  if (path.includes('/message')) return 'messages'
  return ''
})

// 格式化时间
const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 获取活动类型
const getActivityType = (type) => {
  const types = {
    topic: 'success',
    comment: 'warning',
    collection: 'danger',
    view: 'info'
  }
  return types[type] || 'info'
}

// 获取活动标签
const getActivityLabel = (type) => {
  const labels = {
    topic: '话题',
    comment: '评论',
    collection: '收藏',
    view: '浏览'
  }
  return labels[type] || '其他'
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取话题数量
    const topicRes = await getMyTopics(1, 1)
    stats.topicCount = topicRes.data.total || 0

    // 获取收藏数量
    const collectionRes = await getCollectionList()
    stats.collectionCount = collectionRes.data.total || 0

    // 获取未读消息数
    const messageRes = await getMessageList()
    unreadCount.value = messageRes.data?.unreadCount || 0

    // 评论数量（模拟数据）
    stats.commentCount = 0
    stats.viewCount = 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 菜单选择处理
const handleMenuSelect = (index) => {
  const routes = {
    profile: '/user/profile',
    collection: '/user/collection',
    topics: '/user/topics',
    'medical-history': '/user/medical-history',
    'query-history': '/user/query-history',
    messages: '/message/conversations'
  }
  if (routes[index]) {
    router.push(routes[index])
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.user-center-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-row {
    margin-top: 20px;
  }

  .user-card {
    margin-bottom: 20px;

    .user-info {
      text-align: center;

      h3 {
        margin: 16px 0 8px;
        font-size: 18px;
        color: #303133;
      }

      .phone {
        margin: 0 0 20px;
        color: #909399;
        font-size: 14px;
      }

      .stats {
        display: flex;
        justify-content: space-around;
        padding: 20px 0;
        border-top: 1px solid #f0f0f0;

        .stat-item {
          text-align: center;

          .value {
            display: block;
            font-size: 20px;
            font-weight: 600;
            color: #303133;
          }

          .label {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
  }

  .side-menu {
    border-right: none;

    .badge {
      margin-left: 8px;
    }
  }

  .content-card {
    min-height: 600px;

    h3 {
      margin: 0 0 20px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .stats-section {
      margin-bottom: 40px;

      .stat-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
        background: #f5f7fa;
        border-radius: 8px;
        margin-bottom: 16px;

        .stat-info {
          flex: 1;

          .value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
          }

          .label {
            font-size: 13px;
            color: #909399;
          }
        }
      }
    }

    .activity-section {
      margin-bottom: 40px;

      .activity-item {
        display: flex;
        align-items: center;
        gap: 12px;

        .activity-content {
          flex: 1;
          color: #606266;
        }
      }
    }

    .actions-section {
      .action-buttons {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
      }
    }
  }
}
</style>
