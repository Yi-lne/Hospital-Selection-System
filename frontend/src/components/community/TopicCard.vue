<template>
  <el-card class="topic-card" shadow="hover" @click="goToDetail">
    <div class="topic-header">
      <el-avatar :size="40" :src="topic.avatar || defaultAvatar" />
      <div class="author-info">
        <span class="author-name">{{ topic.nickname || '匿名用户' }}</span>
        <span class="publish-time">{{ formatTime(topic.createTime) }}</span>
      </div>
      <!-- 板块类型 -->
      <el-tag v-if="boardTypeName" size="small" type="primary" class="board-type-tag">
        {{ boardTypeName }}
      </el-tag>
      <el-button
        v-if="!isAuthor"
        :type="isCollected ? 'warning' : 'default'"
        :icon="isCollected ? StarFilled : Star"
        circle
        size="small"
        @click.stop="toggleCollect"
      />
    </div>

    <div class="topic-content">
      <div class="content-left">
        <h3 class="topic-title">{{ topic.title || '无标题' }}</h3>
        <p class="topic-description">
          {{ (topic.content || '').slice(0, 150) }}{{ (topic.content || '').length > 150 ? '...' : '' }}
        </p>
        <!-- 非疾病板块时才在内容中显示疾病标签 -->
        <el-tag v-if="topic.boardType !== 1 && topic.diseaseName" size="small" type="info">{{ topic.diseaseName }}</el-tag>
      </div>
      <!-- 疾病板块时显示疾病类型，其他情况显示一级板块 -->
      <div v-if="topic.boardType === 1 && topic.diseaseName" class="board-level1 disease-tag">
        {{ topic.diseaseName }}
      </div>
      <div v-else-if="topic.boardLevel1" class="board-level1">
        {{ topic.boardLevel1 }}
      </div>
    </div>

    <div class="topic-stats">
      <div class="stat-item">
        <el-icon><View /></el-icon>
        <span>{{ topic.viewCount || 0 }}</span>
      </div>
      <div class="stat-item">
        <el-icon><Star /></el-icon>
        <span>{{ topic.collectCount || 0 }}</span>
      </div>
      <div class="stat-item">
        <el-icon><ChatDotRound /></el-icon>
        <span>{{ topic.commentCount || 0 }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { View, Star, StarFilled, ChatDotRound, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores'
import { formatRelativeTime } from '@/utils/date'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'

const props = defineProps({
  topic: Object
})

const emit = defineEmits(['collection-change'])

const router = useRouter()
const userStore = useUserStore()
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 板块类型映射
const boardTypeMap = {
  1: '疾病板块',
  2: '医院评价',
  3: '就医经验',
  4: '康复护理'
}

// 板块类型名称
const boardTypeName = computed(() => {
  if (!props.topic.boardType) return ''
  return boardTypeMap[props.topic.boardType] || ''
})

// 判断是否是话题作者
const isAuthor = computed(() => {
  if (!userStore.isLogin || !props.topic.userId) {
    return false
  }
  return props.topic.userId === userStore.userId
})

const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 检查收藏状态
const checkCollectStatus = async () => {
  if (!userStore.isLogin || isAuthor.value) {
    isCollected.value = false
    return
  }

  try {
    const res = await checkCollected(COLLECTION_TYPE.TOPIC, props.topic.id)
    isCollected.value = res.data
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    isCollected.value = false
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

  try {
    if (isCollected.value) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: props.topic.id
      })
      ElMessage.success('取消收藏成功')
      emit('collection-change')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.TOPIC,
        targetId: props.topic.id
      })
      ElMessage.success('收藏成功')
      isCollected.value = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const goToDetail = () => {
  router.push(`/community/topic/${props.topic.id}`)
}

onMounted(() => {
  checkCollectStatus()
})
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
  align-items: center;

  .author-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1;

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

  .board-type-tag {
    flex-shrink: 0;
  }
}

.topic-content {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;

  .content-left {
    flex: 1;
    min-width: 0;

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

  .board-level1 {
    flex-shrink: 0;
    padding: 4px 12px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
    display: flex;
    align-items: center;
    white-space: nowrap;
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
