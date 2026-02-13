<template>
  <el-card class="topic-card" shadow="hover" @click="goToDetail">
    <div class="topic-header">
      <el-avatar :size="40" :src="topic.authorAvatar || defaultAvatar" />
      <div class="author-info">
        <span class="author-name">{{ topic.authorName || '匿名用户' }}</span>
        <span class="publish-time">{{ formatTime(topic.createTime) }}</span>
      </div>
    </div>

    <div class="topic-content">
      <h3 class="topic-title">{{ topic.title || '无标题' }}</h3>
      <p class="topic-description">
        {{ (topic.content || '').slice(0, 150) }}{{ (topic.content || '').length > 150 ? '...' : '' }}
      </p>
      <el-tag v-if="topic.diseaseName" size="small" type="info">{{ topic.diseaseName }}</el-tag>
    </div>

    <div class="topic-stats">
      <div class="stat-item">
        <el-icon><View /></el-icon>
        <span>{{ topic.viewCount || 0 }}</span>
      </div>
      <div class="stat-item">
        <el-icon :color="topic.isLiked ? '#ff6b6b' : ''"><Star /></el-icon>
        <span>{{ topic.likeCount || 0 }}</span>
      </div>
      <div class="stat-item">
        <el-icon><ChatDotRound /></el-icon>
        <span>{{ topic.commentCount || 0 }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'
import type { Topic } from '@/types/community'
import { formatRelativeTime } from '@/utils/date'

const props = defineProps<{
  topic: Topic
}>()

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time: string) => {
  return formatRelativeTime(time)
}

const goToDetail = () => {
  router.push(`/community/topic/${props.topic.id}`)
}
</script>

<style scoped lang="scss">
.topic-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 16px;

  &:hover {
    transform: translateY(-2px);
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.topic-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .author-name {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }

  .publish-time {
    font-size: 12px;
    color: #909399;
  }
}

.topic-content {
  margin-bottom: 12px;

  .topic-title {
    margin: 0 0 8px;
    font-size: 16px;
    color: #303133;
  }

  .topic-description {
    margin: 0 0 8px;
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }
}

.topic-stats {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: #909399;

    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
