<template>
  <div class="topic-detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">话题详情</span>
      </template>
    </el-page-header>

    <div v-loading="loading" class="content-wrapper">
      <template v-if="topicDetail">
        <!-- 主体内容 -->
        <el-card class="topic-card">
          <div class="topic-header">
            <div class="author-info">
              <el-avatar :size="48" :src="topicDetail.avatar || defaultAvatar" />
              <div>
                <h3>{{ topicDetail.nickname || '匿名用户' }}</h3>
                <span class="publish-time">{{ formatTime(topicDetail.createTime) }}</span>
              </div>
            </div>
            <div class="topic-actions">
              <el-button
                v-if="!isAuthor"
                :type="topicDetail.isCollected ? 'warning' : 'default'"
                :icon="topicDetail.isCollected ? StarFilled : Star"
                @click="toggleCollect"
              >
                {{ topicDetail.isCollected ? '已收藏' : '收藏' }}
              </el-button>
              <el-button
                :type="topicDetail.isLiked ? 'danger' : 'default'"
                :icon="Star"
                @click="toggleLike"
              >
                {{ topicDetail.isLiked ? '已点赞' : '点赞' }} ({{ topicDetail.likeCount }})
              </el-button>
            </div>
          </div>

          <h1 class="topic-title">{{ topicDetail.title }}</h1>

          <el-tag v-if="topicDetail.diseaseName" class="disease-tag" type="info">
            {{ topicDetail.diseaseName }}
          </el-tag>

          <div class="topic-content">
            {{ topicDetail.content }}
          </div>

          <div class="topic-stats">
            <span><el-icon><View /></el-icon> {{ topicDetail.viewCount }} 浏览</span>
            <span><el-icon><ChatDotRound /></el-icon> {{ topicDetail.commentCount }} 评论</span>
          </div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-card">
          <template #header>
            <div class="comment-header">
              <span>评论 ({{ commentList.length }})</span>
            </div>
          </template>

          <!-- 发表评论 -->
          <div v-if="userStore.isLogin" class="comment-input-area">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="发表你的评论..."
            />
            <el-button
              type="primary"
              :loading="submitting"
              @click="submitComment"
            >
              发表评论
            </el-button>
          </div>
          <div v-else class="login-tip">
            <el-link type="primary" @click="$router.push('/login')">登录</el-link> 后发表评论
          </div>

          <!-- 评论列表 -->
          <div v-if="commentList.length > 0" class="comment-list">
            <div v-for="comment in commentList" :key="comment.id" class="comment-item">
              <el-avatar :size="40" :src="comment.avatar || defaultAvatar" />
              <div class="comment-content">
                <div class="comment-header">
                  <span class="author-name">{{ comment.nickname || '匿名用户' }}</span>
                  <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button
                    text
                    size="small"
                    @click="toggleLikeComment(comment)"
                  >
                    <el-icon><Star /></el-icon>
                    {{ comment.likeCount }}
                  </el-button>
                  <el-button
                    v-if="userStore.isLogin"
                    text
                    size="small"
                    @click="showReplyInput(comment)"
                  >
                    <el-icon><ChatDotRound /></el-icon>
                    回复
                  </el-button>
                </div>

                <!-- 回复输入框 -->
                <div v-if="replyingTo === comment.id" class="reply-input-area">
                  <el-input
                    v-model="replyContent"
                    type="textarea"
                    :rows="2"
                    :placeholder="`回复 @${comment.nickname || '匿名用户'}...`"
                  />
                  <div class="reply-actions">
                    <el-button size="small" @click="cancelReply">取消</el-button>
                    <el-button
                      type="primary"
                      size="small"
                      :loading="submitting"
                      @click="submitReply(comment)"
                    >
                      发送
                    </el-button>
                  </div>
                </div>

                <!-- 显示回复列表 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <el-avatar :size="32" :src="reply.avatar || defaultAvatar" />
                    <div class="reply-content">
                      <div class="reply-header">
                        <span class="author-name">{{ reply.nickname || '匿名用户' }}</span>
                        <span v-if="reply.replyToNickname" class="reply-to">
                          回复 @{{ reply.replyToNickname }}
                        </span>
                        <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                      </div>
                      <p class="reply-text">{{ reply.content }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <Empty v-else description="暂无评论，快来发表第一条评论吧！" />
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, View, ChatDotRound } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import { getTopicDetail, getCommentList, addComment, replyComment, likeComment, unlikeComment } from '@/api/community'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'



const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const topicDetail = ref(null)
const commentList = ref([])
const commentContent = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 判断是否是话题作者
const isAuthor = computed(() => {
  if (!userStore.isLogin || !topicDetail.value || !topicDetail.value.userId) {
    return false
  }
  return topicDetail.value.userId === userStore.userId
})

const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 加载话题详情
const loadTopicDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getTopicDetail(id)
    topicDetail.value = res.data

    // 加载评论
    const commentRes = await getCommentList(id)
    commentList.value = commentRes.data
  } catch (error) {
    console.error('加载话题详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 切换收藏状态
const toggleCollect = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/auth/login')
    return
  }

  if (isAuthor.value) {
    ElMessage.warning('不能收藏自己的话题')
    return
  }

  if (!topicDetail.value) return

  try {
    if (topicDetail.value.isCollected) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: topicDetail.value.id
      })
      ElMessage.success('取消收藏成功')
      topicDetail.value.isCollected = false
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: topicDetail.value.id
      })
      ElMessage.success('收藏成功')
      topicDetail.value.isCollected = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败')
  }
}

// 点赞话题
const handleLike = async () => {
  if (!topicDetail.value) return

  try {
    await toggleLike('topic', topicDetail.value.id)
    topicDetail.value.isLiked = !topicDetail.value.isLiked
    topicDetail.value.likeCount += topicDetail.value.isLiked ? 1 : -1
  } catch (error) {
    console.error('切换点赞失败:', error)
  }
}

// 点赞评论
const toggleLikeComment = async (comment) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    if (comment.isLiked) {
      await unlikeComment(comment.id)
      comment.likeCount--
    } else {
      await likeComment(comment.id)
      comment.likeCount++
    }
    comment.isLiked = !comment.isLiked
  } catch (error) {
    console.error('切换评论点赞失败:', error)
  }
}

const toggleLike = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }
  handleLike()
}

// 显示回复输入框
const showReplyInput = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = async (parentComment) => {
  if (!topicDetail.value || replyingTo.value === null) return

  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    submitting.value = true
    await replyComment({
      topicId: topicDetail.value.id,
      parentId: replyingTo.value,
      content: replyContent.value
    })

    ElMessage.success('回复成功')
    cancelReply()

    // 重新加载评论
    const commentRes = await getCommentList(topicDetail.value.id)
    commentList.value = commentRes.data

    // 更新话题评论数
    if (topicDetail.value) {
      topicDetail.value.commentCount++
    }
  } catch (error) {
    console.error('提交回复失败:', error)
  } finally {
    submitting.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!topicDetail.value) return

  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    submitting.value = true
    await addComment({
      topicId: topicDetail.value.id,
      content: commentContent.value
    })

    ElMessage.success('评论发表成功')
    commentContent.value = ''

    // 重新加载评论
    const commentRes = await getCommentList(topicDetail.value.id)
    commentList.value = commentRes.data

    // 更新话题评论数
    if (topicDetail.value) {
      topicDetail.value.commentCount++
    }
  } catch (error) {
    console.error('提交评论失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadTopicDetail()
})
</script>

<style scoped lang="scss">
.topic-detail-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-wrapper {
    margin-top: 20px;
  }

  .topic-card {
    margin-bottom: 20px;

    .topic-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;

      .author-info {
        display: flex;
        gap: 12px;

        h3 {
          margin: 0 0 4px;
          font-size: 16px;
          color: #303133;
        }

        .publish-time {
          font-size: 13px;
          color: #909399;
        }
      }
    }

    .topic-title {
      margin: 0 0 16px;
      font-size: 24px;
      color: #303133;
    }

    .disease-tag {
      margin-bottom: 16px;
    }

    .topic-content {
      margin-bottom: 20px;
      font-size: 15px;
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
    }

    .topic-stats {
      display: flex;
      gap: 24px;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
      color: #909399;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .comment-card {
    .comment-input-area {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 24px;

      .el-button {
        align-self: flex-end;
      }
    }

    .login-tip {
      padding: 20px;
      text-align: center;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 24px;
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 12px;
        padding: 16px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .comment-content {
          flex: 1;

          .comment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .author-name {
              font-size: 14px;
              font-weight: 500;
              color: #303133;
            }

            .comment-time {
              font-size: 12px;
              color: #909399;
            }
          }

          .comment-text {
            margin: 0 0 8px;
            font-size: 14px;
            line-height: 1.6;
            color: #606266;
          }

          .comment-actions {
            display: flex;
            gap: 16px;
          }
        }
      }
    }
  }
}
</style>
