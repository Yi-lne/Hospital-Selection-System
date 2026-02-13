import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 社区状态管理
 */
export const useCommunityStore = defineStore('community', () => {
  // 当前话题
  const currentTopic = ref(null)

  // 话题列表
  const topicList = ref([])

  // 评论列表
  const commentList = ref([])

  // 未读消息数
  const unreadCount = ref(0)

  /**
   * 设置当前话题
   */
  function setCurrentTopic(topic) {
    currentTopic.value = topic
  }

  /**
   * 设置话题列表
   */
  function setTopicList(topics) {
    topicList.value = topics
  }

  /**
   * 添加话题
   */
  function addTopic(topic) {
    topicList.value.unshift(topic)
  }

  /**
   * 更新话题
   */
  function updateTopic(topicId, updates) {
    const index = topicList.value.findIndex(t => t.id === topicId)
    if (index !== -1) {
      topicList.value[index] = { ...topicList.value[index], ...updates }
    }
  }

  /**
   * 删除话题
   */
  function removeTopic(topicId) {
    const index = topicList.value.findIndex(t => t.id === topicId)
    if (index !== -1) {
      topicList.value.splice(index, 1)
    }
  }

  /**
   * 设置评论列表
   */
  function setCommentList(comments) {
    commentList.value = comments
  }

  /**
   * 添加评论
   */
  function addComment(comment) {
    commentList.value.push(comment)

    // 更新话题的评论数
    if (currentTopic.value) {
      currentTopic.value.commentCount++
    }
  }

  /**
   * 删除评论
   */
  function removeComment(commentId) {
    const index = commentList.value.findIndex(c => c.id === commentId)
    if (index !== -1) {
      commentList.value.splice(index, 1)

      // 更新话题的评论数
      if (currentTopic.value) {
        currentTopic.value.commentCount--
      }
    }
  }

  /**
   * 设置未读消息数
   */
  function setUnreadCount(count) {
    unreadCount.value = count
  }

  return {
    currentTopic,
    topicList,
    commentList,
    unreadCount,
    setCurrentTopic,
    setTopicList,
    addTopic,
    updateTopic,
    removeTopic,
    setCommentList,
    addComment,
    removeComment,
    setUnreadCount
  }
})
