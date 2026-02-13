<template>
  <div class="comment-item">
    <el-avatar :size="40" :src="comment.userAvatar || defaultAvatar" />
    <div class="comment-content">
      <div class="comment-header">
        <span class="author-name">{{ comment.userNickname || '匿名用户' }}</span>
        <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
      </div>
      <p class="comment-text">{{ comment.content }}</p>
      <div class="comment-actions">
        <el-button
          text
          size="small"
          :type="comment.isLiked ? 'danger' : 'default'"
          @click="handleLike"
        >
          <el-icon><Star /></el-icon>
          {{ comment.likeCount || 0 }}
        </el-button>
        <el-button text size="small" @click="handleReply">
          <el-icon><ChatDotRound /></el-icon>
          回复
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatRelativeTime } from '@/utils/date'
import { Star, ChatDotRound } from '@element-plus/icons-vue'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like', 'reply'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time) => {
  return formatRelativeTime(time)
}

const handleLike = () => {
  emit('like', props.comment)
}

const handleReply = () => {
  emit('reply', props.comment)
}
</script>

<style scoped lang="scss">
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
</style>
