<template>
  <div class="my-comments-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">我的评论</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="comments-card">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="activeTab" @change="loadComments">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="topic">话题评论</el-radio-button>
          <el-radio-button value="reply">回复评论</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 评论列表 -->
      <div v-if="commentList.length > 0" class="comment-list">
        <div v-for="comment in commentList" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :size="40" :src="comment.userAvatar || defaultAvatar" />
            <div class="user-info">
              <span class="username">{{ comment.userNickname || '匿名用户' }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
          </div>
          <div class="comment-content">
            <p>{{ comment.content }}</p>
            <div v-if="comment.topicTitle" class="topic-ref">
              <el-icon><ChatDotRound /></el-icon>
              <span>回复话题：{{ comment.topicTitle }}</span>
              <el-link type="primary" @click="goToTopic(comment.topicId)">查看</el-link>
            </div>
          </div>
          <div class="comment-stats">
            <span class="stat">
              <el-icon><View /></el-icon>
              {{ comment.viewCount || 0 }}
            </span>
            <span class="stat">
              <el-icon><Star /></el-icon>
              {{ comment.likeCount || 0 }}
            </span>
            <span v-if="comment.replyCount > 0" class="stat">
              <el-icon><ChatDotRound /></el-icon>
              {{ comment.replyCount }}
            </span>
          </div>
          <div class="comment-actions">
            <el-button text type="primary" @click="goToTopic(comment.topicId)">
              查看详情
            </el-button>
            <el-button text type="danger" @click="handleDelete(comment.id)">
              删除
            </el-button>
          </div>
        </div>
      </div>
      <Empty v-else description="暂无评论记录" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadComments"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'
import { getMyComments, deleteComment } from '@/api/community'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()

const loading = ref(false)
const commentList = ref([])
const total = ref(0)
const activeTab = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const pagination = reactive({
  page: 1,
  pageSize: 10
})

// 格式化时间
const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 加载评论列表
const loadComments = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (activeTab.value) {
      params.type = activeTab.value
    }
    const res = await getMyComments(params)
    commentList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到话题详情
const goToTopic = (topicId) => {
  if (topicId) {
    router.push(`/community/topic/${topicId}`)
  }
}

// 删除评论
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteComment(id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.page = 1
  pagination.pageSize = size
  loadComments()
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped lang="scss">
.my-comments-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .comments-card {
    margin-top: 20px;
    min-height: 500px;
  }

  .filter-bar {
    margin-bottom: 20px;
  }

  .comment-list {
    .comment-item {
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 16px;

      .comment-header {
        display: flex;
        gap: 12px;
        align-items: center;
        margin-bottom: 12px;

        .user-info {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .username {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .time {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .comment-content {
        margin: 0 0 12px 52px;

        p {
          margin: 0 0 12px;
          font-size: 14px;
          line-height: 1.6;
          color: #606266;
        }

        .topic-ref {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 12px;
          background: #fff;
          border-radius: 4px;
          font-size: 13px;
          color: #606266;

          .el-link {
            margin-left: auto;
          }
        }
      }

      .comment-stats {
        display: flex;
        gap: 16px;
        margin-left: 52px;
        margin-bottom: 12px;
        font-size: 13px;
        color: #909399;

        .stat {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .comment-actions {
        margin-left: 52px;
        display: flex;
        gap: 12px;
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
