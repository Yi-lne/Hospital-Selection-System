<template>
  <div class="chat-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">聊天</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="chat-card">
      <template v-if="chatUser">
        <div class="chat-header">
          <el-avatar :size="40" :src="chatUser.avatar || defaultAvatar" />
          <span class="username">{{ chatUser.username }}</span>
        </div>

        <div ref="messagesContainer" class="messages-container">
          <div
            v-for="message in messageList"
            :key="message.id"
            :class="['message-item', message.senderId === currentUserId ? 'sent' : 'received']"
          >
            <el-avatar :size="36" :src="getAvatar(message.senderId)" />
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.createTime) }}</div>
            </div>
          </div>
          <div v-if="messageList.length === 0" class="empty-tip">
            开始聊天吧~
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="messageContent"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            @keydown.enter.ctrl="sendMessage"
          />
          <div class="input-actions">
            <span class="tip">Ctrl + Enter 发送</span>
            <el-button type="primary" :loading="sending" @click="sendMessage">
              发送
            </el-button>
          </div>
        </div>
      </template>
      <Empty v-else description="请选择要聊天的用户" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore, useMessageStore } from '@/stores'
import { getMessageHistory, sendMessage, markAsRead } from '@/api/message'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'


const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const messageStore = useMessageStore()

const loading = ref(false)
const sending = ref(false)
const messageList = ref([])
const messageContent = ref('')
const messagesContainer = ref()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const currentUserId = computed(() => userStore.userId)
const chatUser = computed(() => messageStore.currentChatUser)

const formatTime = (time) => {
  return formatRelativeTime(time)
}

const getAvatar = (senderId) => {
  if (senderId === currentUserId.value) {
    return userStore.userAvatar || defaultAvatar
  }
  return chatUser.value?.avatar || defaultAvatar
}

// 加载聊天记录
const loadMessages = async () => {
  const userId = Number(route.params.userId)
  if (!userId) return

  try {
    loading.value = true

    // 设置当前聊天用户
    const conv = messageStore.conversations.find(c => c.userId === userId)
    messageStore.setCurrentChat({
      userId,
      userName: conv?.userName || '未知用户'
    })

    const res = await getMessageHistory(userId)
    messageList.value = res.data
    messageStore.setCurrentMessages(res.data)

    // 标记为已读
    await markAsRead(userId)

    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载消息失败:', error)
  } finally {
    loading.value = false
  }
}

// 发送消息
const sendMessage = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  if (!chatUser.value) return

  try {
    sending.value = true
    await sendMessage({
      receiverId: chatUser.value.userId, content.value
    })

    // 添加到消息列表
    messageList.value.push({
      id: Date.now(), senderId.value!, receiverId.value.userId, content.value, isRead, createTime Date().toISOString()
    })

    messageContent.value = ''

    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
  } finally {
    sending.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听路由参数变化
watch(
  () => route.params.userId,
  (userId) => {
    if (userId) {
      loadMessages()
    }
  },
  { immediate: true }
)

onMounted(() => {
  if (!route.params.userId) {
    router.push('/message/conversations')
  }
})
</script>

<style scoped lang="scss">
.chat-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .chat-card {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    height: calc(100vh - 200px);

    .chat-header {
      display: flex;
      align-items: center;
      gap: 12px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;

      .username {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }

    .messages-container {
      flex: 1;
      overflow-y: auto;
      padding: 20px 0;
      display: flex;
      flex-direction: column;
      gap: 16px;

      .message-item {
        display: flex;
        gap: 12px;
        max-width: 70%;

        &.sent {
          align-self: flex-end;
          flex-direction: row-reverse;

          .message-content {
            align-items: flex-end;
          }

          .message-text {
            background: #409eff;
            color: #fff;
          }
        }

        &.received {
          align-self: flex-start;

          .message-content {
            align-items: flex-start;
          }

          .message-text {
            background: #f5f7fa;
            color: #303133;
          }
        }

        .message-content {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .message-text {
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 14px;
            line-height: 1.5;
            word-wrap: break-word;
          }

          .message-time {
            font-size: 12px;
            color: #909399;
          }
        }
      }

      .empty-tip {
        text-align: center;
        color: #c0c4cc;
        padding: 40px;
      }
    }

    .input-area {
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;

      .input-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 12px;

        .tip {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>
