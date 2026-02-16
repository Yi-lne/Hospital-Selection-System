<template>
  <div class="topic-list-item" @click="handleClick">
    <div class="item-header">
      <div class="author-info">
        <el-avatar :size="40" :src="topic.authorAvatar || defaultAvatar" />
        <div class="author-text">
          <span class="author-name">{{ topic.authorName || '匿名用户' }}</span>
          <span class="publish-time">{{ formatTime(topic.createTime) }}</span>
        </div>
      </div>
      <div class="board-tags">
        <el-tag v-if="topic.boardLevel1" size="small" type="info">
          {{ topic.boardLevel1 }}
        </el-tag>
      </div>
    </div>

    <h3 class="topic-title">{{ topic.title }}</h3>
    <p class="topic-content">{{ topic.content }}</p>

    <div v-if="topic.diseaseName || topic.boardLevel1" class="topic-tags">
      <el-tag v-if="topic.diseaseName" size="small" type="success">
        {{ topic.diseaseName }}
      </el-tag>
    </div>

    <div class="item-stats">
      <span class="stat">
        <el-icon><View /></el-icon>
        {{ topic.viewCount || 0 }}
      </span>
      <span class="stat">
        <el-icon><ChatDotRound /></el-icon>
        {{ topic.commentCount || 0 }}
      </span>
      <span class="stat">
        <el-icon><Star /></el-icon>
        {{ topic.collectCount || 0 }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { formatRelativeTime } from '@/utils/date'
import { View, ChatDotRound, Star } from '@element-plus/icons-vue'

const props = defineProps({
  topic: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time) => {
  return formatRelativeTime(time)
}

const handleClick = () => {
  emit('click', props.topic)
}
</script>

<style scoped lang="scss">
.topic-list-item {
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .author-info {
      display: flex;
      gap: 12px;
      align-items: center;

      .author-text {
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
    }

    .board-tags {
      display: flex;
      gap: 8px;
    }
  }

  .topic-title {
    margin: 0 0 12px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    line-height: 1.4;
  }

  .topic-content {
    margin: 0 0 12px;
    font-size: 14px;
    line-height: 1.6;
    color: #606266;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .item-stats {
    display: flex;
    gap: 20px;
    font-size: 13px;
    color: #909399;

    .stat {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}
</style>
