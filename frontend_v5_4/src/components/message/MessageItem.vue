<template>
  <div class="message-item" :class="{ unread: !message.isRead }" @click="handleClick">
    <el-avatar :size="50" :src="message.avatar || defaultAvatar">
      {{ message.nickname?.charAt(0) || '用' }}
    </el-avatar>
    <div class="message-content">
      <div class="message-header">
        <span class="username">{{ message.nickname || '用户' }}</span>
        <span class="time">{{ formatTime(message.createTime) }}</span>
      </div>
      <div class="message-body">
        <p class="last-message">{{ message.lastMessage || message.content }}</p>
        <el-badge v-if="!message.isRead && message.unreadCount > 0" :value="message.unreadCount" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { formatRelativeTime } from '@/utils/date'

const props = defineProps({
  message: {
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
  emit('click', props.message)
}
</script>

<style scoped lang="scss">
.message-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #f5f7fa;
  }

  &.unread {
    background: #f0f9ff;
  }

  .message-content {
    flex: 1;
    min-width: 0;

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .username {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
      }

      .time {
        font-size: 12px;
        color: #909399;
      }
    }

    .message-body {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .last-message {
        margin: 0;
        font-size: 13px;
        color: #606266;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        flex: 1;
      }
    }
  }
}
</style>
