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
              <span>常见疾病</span>
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
        <!-- 话题搜索框 -->
        <div class="topic-search">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索话题标题或疾病类型"
            :prefix-icon="Search"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>

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
                v-for="topic in topicList.filter(t => t && t.id)"
                :key="topic.id"
                class="topic-item"
                @click="goToTopicDetail(topic.id)"
              >
                <div class="topic-header">
                  <div class="title-section">
                    <h3 class="topic-title">{{ topic.title || '无标题' }}</h3>
                  </div>
                  <!-- 疾病板块时显示疾病类型，其他情况显示一级板块 -->
                  <div v-if="topic.boardType === 1 && topic.diseaseName" class="right-tag">
                    {{ topic.diseaseName }}
                  </div>
                  <div v-else-if="topic.boardLevel1" class="right-tag">
                    {{ topic.boardLevel1 }}
                  </div>
                </div>
                <div class="topic-content">
                  {{ topic.content || '' }}
                </div>
                <div class="topic-meta">
                  <span class="author">
                    <el-icon><User /></el-icon>
                    {{ topic.nickname || '匿名用户' }}
                  </span>
                  <span v-if="topic.boardType" class="board">{{ getBoardTypeName(topic.boardType) }}</span>
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
                    {{ topic.collectCount || 0 }}
                  </span>
                  <span class="time">{{ formatTime(topic.createTime) }}</span>
                  <el-button
                    v-if="userStore.isLogin"
                    text
                    size="small"
                    type="warning"
                    @click="showReportDialog(topic.id, $event)"
                  >
                    举报
                  </el-button>
                  <el-button
                    v-if="userStore.isAdmin"
                    text
                    size="small"
                    type="danger"
                    @click="handleDeleteTopic(topic.id, $event)"
                  >
                    删除
                  </el-button>
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

    <!-- 举报对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="举报"
      width="500px"
    >
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="举报对象">
          <span>话题</span>
        </el-form-item>
        <el-form-item label="举报原因" required>
          <el-select v-model="reportForm.reasonType" placeholder="请选择举报原因">
            <el-option
              v-for="reason in reportReasonTypes"
              :key="reason"
              :label="reason"
              :value="reason"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            v-model="reportForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入举报详细说明（选填，最多500字）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, View, ChatDotRound, Star, Edit, Check, Search
} from '@element-plus/icons-vue'
import { getBoardList, getTopicsByBoard, deleteTopic } from '@/api/community'
import { createReport } from '@/api/report'
import { useUserStore } from '@/stores'
import Empty from '@/components/common/Empty.vue'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const hotBoards = ref([])
const hotBoardsLoading = ref(false)
const selectedHotBoard = ref('')

const topicList = ref([])
const topicsLoading = ref(false)
const total = ref(0)
const currentTab = ref(route.query.tab || 'hot')
const searchKeyword = ref('')

const pagination = reactive({
  page: parseInt(route.query.page) || 1,
  pageSize: 10
})

const scrollPosition = ref(parseInt(route.query.scroll) || 0)

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

    // 搜索关键词
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    // 排序方式
    if (currentTab.value === 'hot') {
      params.sortBy = 'hot'
    } else {
      params.sortBy = 'latest'
    }

    const res = await getTopicsByBoard(params)
    topicList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载话题失败:', error)
    ElMessage.error('加载话题失败')
  } finally {
    topicsLoading.value = false
  }
}

// 搜索话题
const handleSearch = () => {
  pagination.page = 1
  loadTopics()
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
  // 更新路由参数
  router.push({
    path: route.path,
    query: { ...route.query, tab: currentTab.value }
  })
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
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  router.push({
    path: `/community/topic/${id}`,
    query: {
      from: 'community',
      tab: currentTab.value,
      page: pagination.page,
      scroll: scrollTop
    }
  })
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

// 板块类型映射
const boardTypeMap = {
  1: '疾病板块',
  2: '医院评价',
  3: '就医经验',
  4: '康复护理'
}

// 获取板块类型名称
const getBoardTypeName = (boardType) => {
  if (!boardType) return ''
  return boardTypeMap[boardType] || ''
}

// 举报相关状态
const reportDialogVisible = ref(false)
const reportForm = ref({
  targetType: 1, // 1=话题
  targetId: null,
  reasonType: '',
  reason: ''
})
const reportReasonTypes = [
  '垃圾广告',
  '违法违规',
  '色情低俗',
  '诈骗钓鱼',
  '人身攻击',
  '抄袭他人',
  '其他'
]

// 显示举报对话框
const showReportDialog = (topicId, event) => {
  event.stopPropagation() // 防止触发跳转
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/auth/login')
    return
  }
  reportForm.value = {
    targetType: 1,
    targetId: topicId,
    reasonType: '',
    reason: ''
  }
  reportDialogVisible.value = true
}

// 提交举报
const submitReport = async () => {
  if (!reportForm.value.reasonType) {
    ElMessage.warning('请选择举报原因')
    return
  }

  try {
    await createReport(reportForm.value)
    ElMessage.success('举报成功，我们会尽快处理')
    reportDialogVisible.value = false
  } catch (error) {
    console.error('提交举报失败:', error)
  }
}

// 管理员删除话题
const handleDeleteTopic = async (topicId, event) => {
  event.stopPropagation() // 防止触发跳转

  try {
    await ElMessageBox.confirm(
      '确定删除这个话题吗？删除后无法恢复。',
      '管理员操作',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteTopic(topicId)
    ElMessage.success('删除成功')

    // 刷新话题列表
    loadTopics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除话题失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadHotBoards()
  loadTopics()
})

// 恢复滚动位置
const restoreScrollPosition = () => {
  nextTick(() => {
    if (scrollPosition.value > 0) {
      window.scrollTo({
        top: scrollPosition.value,
        behavior: 'instant'
      })
    }
  })
}

// 监听路由参数变化
watch(() => route.query.tab, (newTab) => {
  if (newTab && newTab !== currentTab.value) {
    currentTab.value = newTab
    pagination.page = 1
    scrollPosition.value = 0
    loadTopics()
  }
})

// 监听数据加载完成，恢复滚动位置
watch(() => [topicList.value, topicsLoading.value], ([newList, isLoading]) => {
  if (!isLoading && newList.length > 0 && scrollPosition.value > 0) {
    restoreScrollPosition()
  }
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

  // 搜索框
  .topic-search {
    margin-bottom: 16px;
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

        .title-section {
          flex: 1;
          margin-right: 12px;
        }

        .topic-title {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .right-tag {
          flex-shrink: 0;
          padding: 4px 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 500;
          white-space: nowrap;
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
    margin-top: 0;
    margin-bottom: 20px;
  }
}
</style>
