<template>
  <div class="community-home-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">社区交流</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="content-row">
      <!-- 左侧：板块导航 -->
      <el-col :xs="24" :sm="6" :md="5" :lg="4">
        <el-card class="board-card">
          <template #header>
            <div class="card-header">
              <span>热门板块</span>
            </div>
          </template>
          <div v-loading="hotBoardsLoading">
            <div
              v-for="board in hotBoards"
              :key="board"
              class="board-item"
              :class="{ active: selectedHotBoard === board }"
              @click="selectHotBoard(board)"
            >
              {{ board }}
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：话题列表 -->
      <el-col :xs="24" :sm="12" :md="14" :lg="16">
        <el-card class="topic-list-card">
          <template #header>
            <div class="card-header">
              <span>{{ currentTab === 'hot' ? '热门话题' : '最新话题' }}</span>
              <el-radio-group v-model="currentTab" @change="handleTabChange">
                <el-radio-button value="hot">热门</el-radio-button>
                <el-radio-button value="latest">最新</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <div v-loading="topicsLoading">
            <template v-if="topicList.length > 0">
              <div
                v-for="topic in topicList"
                :key="topic.id"
                class="topic-item"
                @click="goToTopicDetail(topic.id)"
              >
                <div class="topic-header">
                  <h3 class="topic-title">{{ topic.title }}</h3>
                  <el-tag v-if="topic.boardLevel1" size="small" type="info">
                    {{ topic.boardLevel1 }}
                  </el-tag>
                </div>
                <div class="topic-content">
                  {{ topic.content }}
                </div>
                <div class="topic-meta">
                  <span class="author">
                    <el-icon><User /></el-icon>
                    {{ topic.nickname || '匿名用户' }}
                  </span>
                  <span class="board">{{ topic.boardLevel2 || topic.boardLevel1 }}</span>
                  <span class="stats">
                    <el-icon><View /></el-icon>
                    {{ topic.viewCount || 0 }}
                  </span>
                  <span class="stats">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ topic.commentCount || 0 }}
                  </span>
                  <span class="stats">
                    <el-icon><Star /></el-icon>
                    {{ topic.likeCount || 0 }}
                  </span>
                  <span class="time">{{ formatTime(topic.createTime) }}</span>
                </div>
              </div>
            </template>
            <Empty v-else description="暂无话题" />
          </div>

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :total="total"
              :page-sizes="[10, 20, 30]"
              layout="total, sizes, prev, pager, next"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：发布话题 -->
      <el-col :xs="24" :sm="6" :md="5" :lg="4">
        <el-card class="action-card">
          <el-button
            type="primary"
            size="large"
            :icon="Edit"
            @click="goToPublish"
            style="width: 100%"
          >
            发布话题
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, View, ChatDotRound, Star, Edit
} from '@element-plus/icons-vue'
import { getBoardList, getTopicsByBoard } from '@/api/community'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()

const hotBoards = ref([])
const hotBoardsLoading = ref(false)
const selectedHotBoard = ref('')

const topicList = ref([])
const topicsLoading = ref(false)
const total = ref(0)
const currentTab = ref('hot')

const pagination = reactive({
  page: 1,
  pageSize: 10
})

// 加载板块列表
const loadHotBoards = async () => {
  try {
    hotBoardsLoading.value = true
    const res = await getBoardList()
    hotBoards.value = res.data || []
  } catch (error) {
    console.error('加载板块失败:', error)
    ElMessage.error('加载板块失败')
  } finally {
    hotBoardsLoading.value = false
  }
}

// 加载话题列表
const loadTopics = async () => {
  try {
    topicsLoading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }

    // 添加筛选条件
    if (selectedHotBoard.value) {
      params.boardLevel1 = selectedHotBoard.value
    }

    // 排序方式
    if (currentTab.value === 'hot') {
      params.sortBy = 'hot'
    } else {
      params.sortBy = 'latest'
    }

    const res = await getTopicsByBoard(params)
    topicList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载话题失败:', error)
    ElMessage.error('加载话题失败')
  } finally {
    topicsLoading.value = false
  }
}

// 选择板块
const selectHotBoard = (board) => {
  if (selectedHotBoard.value === board) {
    selectedHotBoard.value = ''
  } else {
    selectedHotBoard.value = board
  }
  pagination.page = 1
  loadTopics()
}

// 切换标签
const handleTabChange = () => {
  pagination.page = 1
  loadTopics()
}

// 分页改变
const handlePageChange = (page) => {
  pagination.page = page
  loadTopics()
}

const handleSizeChange = (size) => {
  pagination.page = 1
  pagination.pageSize = size
  loadTopics()
}

// 跳转到话题详情
const goToTopicDetail = (id) => {
  router.push(`/community/topic/${id}`)
}

// 跳转到发布话题
const goToPublish = () => {
  router.push('/community/publish')
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`

  return date.toLocaleDateString()
}

onMounted(() => {
  loadHotBoards()
  loadTopics()
})
</script>

<style scoped lang="scss">
.community-home-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-row {
    margin-top: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }

  // 板块卡片
  .board-card {
    margin-bottom: 20px;

    .board-item {
      padding: 12px 16px;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.3s;
      margin-bottom: 8px;

      &:hover {
        background-color: #f5f7fa;
      }

      &.active {
        background-color: #409eff;
        color: #fff;
      }
    }
  }

  // 话题列表卡片
  .topic-list-card {
    min-height: 600px;

    .topic-item {
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      &:last-child {
        border-bottom: none;
      }

      .topic-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;

        .topic-title {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          flex: 1;
          margin-right: 12px;
        }
      }

      .topic-content {
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .topic-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 13px;
        color: #909399;

        .author,
        .board,
        .stats,
        .time {
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .board {
          color: #409eff;
        }
      }
    }

    .pagination-wrapper {
      margin-top: 24px;
      display: flex;
      justify-content: center;
    }
  }

  // 操作卡片
  .action-card {
    margin-bottom: 20px;
  }
}
</style>
