<template>
  <div class="conversation-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">私信列表</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="list-card">
      <template v-if="conversations.length > 0">
        <div
          v-for="conv in conversations"
          :key="conv.userId"
          class="conversation-item"
          @click="openChat(conv)"
        >
          <el-avatar :size="50" :src="conv.userAvatar || defaultAvatar" />
          <div class="conv-content">
            <div class="conv-header">
              <span class="username">{{ conv.userName || '未知用户' }}</span>
              <span v-if="conv.lastMessageTime" class="time">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <div class="conv-preview">
              <span v-if="conv.lastMessage" class="last-message">{{ conv.lastMessage }}</span>
              <span v-else class="no-message">暂无消息</span>
              <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" :max="99" />
            </div>
          </div>
        </div>
      </template>
      <Empty v-else description="暂无私信" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '@/api/message'
import { useMessageStore } from '@/stores'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'


const router = useRouter()
const messageStore = useMessageStore()

const loading = ref(false)
const conversations = ref([])
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 加载会话列表
const loadConversations = async () => {
  try {
    loading.value = true
    const res = await getConversations()
    conversations.value = res.data
    messageStore.setConversations(res.data)
  } catch (error) {
    console.error('加载会话列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开聊天
const openChat = (conv) => {
  router.push(`/message/chat/${conv.userId}`)
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped lang="scss">
.conversation-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .list-card {
    margin-top: 20px;
    min-height: 400px;
  }

  .conversation-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    &:last-child {
      border-bottom: none;
    }

    .conv-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .conv-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .username {
          font-size: 16px;
          font-weight: 500;
          color: #303133;
        }

        .time {
          font-size: 12px;
          color: #909399;
        }
      }

      .conv-preview {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .last-message {
          font-size: 14px;
          color: #606266;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 300px;
        }

        .no-message {
          font-size: 14px;
          color: #c0c4cc;
        }
      }
    }
  }
}
</style>
