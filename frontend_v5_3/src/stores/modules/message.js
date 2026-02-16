import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 消息状态管理
 */
export const useMessageStore = defineStore('message', () => {
  // 会话列表
  const conversations = ref([])

  // 当前聊天消息
  const currentMessages = ref([])

  // 当前聊天用户
  const currentChatUser = ref(null)

  // 未读消息数
  const unreadCount = computed(() => {
    return conversations.value.reduce((total, conv) => total + conv.unreadCount, 0)
  })

  /**
   * 设置会话列表
   */
  function setConversations(list) {
    conversations.value = list
  }

  /**
   * 更新会话的未读数
   */
  function updateConversationUnread(userId, delta) {
    const conv = conversations.value.find(c => c.userId === userId)
    if (conv) {
      conv.unreadCount = Math.max(0, conv.unreadCount + delta)
    }
  }

  /**
   * 设置当前聊天
   */
  function setCurrentChat(user) {
    currentChatUser.value = user
    currentMessages.value = []
  }

  /**
   * 设置当前聊天消息
   */
  function setCurrentMessages(messages) {
    currentMessages.value = messages
  }

  /**
   * 添加消息
   */
  function addMessage(message) {
    currentMessages.value.push(message)
  }

  /**
   * 更新会话的最后消息
   */
  function updateLastMessage(userId, message, time) {
    const conv = conversations.value.find(c => c.userId === userId)
    if (conv) {
      conv.lastMessage = message
      conv.lastMessageTime = time

      // 将该会话移到最前
      conversations.value = [conv, ...conversations.value.filter(c => c.userId !== userId)]
    }
  }

  return {
    conversations,
    currentMessages,
    currentChatUser,
    unreadCount,
    setConversations,
    updateConversationUnread,
    setCurrentChat,
    setCurrentMessages,
    addMessage,
    updateLastMessage
  }
})
