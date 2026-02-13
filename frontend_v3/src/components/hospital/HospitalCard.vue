<template>
  <el-card class="hospital-card" shadow="hover" @click="goToDetail">
    <div class="hospital-header">
      <el-avatar :size="64" :src="hospital.avatar || defaultAvatar" shape="square" />
      <div class="hospital-info">
        <h3 class="hospital-name">{{ hospital.name }}</h3>
        <div class="hospital-tags">
          <el-tag size="small" type="danger">{{ displayLevel }}</el-tag>
          <el-tag v-if="sortPriority === 'rating' && hospital.rating" size="small" type="warning">
            评分：{{ hospital.rating.toFixed(1) }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="hospital-body">
      <div class="hospital-location">
        <el-icon><Location /></el-icon>
        <span>{{ hospital.city }} {{ hospital.address }}</span>
      </div>

      <p v-if="hospital.introduction" class="hospital-introduction">
        {{ hospital.introduction.slice(0, 100) }}{{ hospital.introduction.length > 100 ? '...' : '' }}
      </p>
    </div>

    <div class="hospital-footer">
      <el-button text type="primary" @click.stop="goToDetail">
        查看详情 <el-icon><ArrowRight /></el-icon>
      </el-button>
      <el-button
        text
        :type="isCollected ? 'warning' : 'info'"
        @click.stop="toggleCollect"
      >
        <el-icon><Star /></el-icon>
        {{ isCollected ? '已收藏' : '收藏' }}
      </el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, ArrowRight, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { formatHospitalLevel } from '@/utils/hospital'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import type { HospitalSimple } from '@/types/hospital'

const props = defineProps<{
  hospital: HospitalSimple
  sortPriority?: string  // 排序优先级：level-级别优先，rating-评分优先
}>()

const router = useRouter()
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 将医院等级代码转换为中文显示
const displayLevel = computed(() => {
  return formatHospitalLevel(props.hospital.level)
})

// 检查收藏状态
const checkCollectStatus = async () => {
  try {
    const res = await checkCollected(COLLECTION_TYPE.HOSPITAL, props.hospital.id)
    isCollected.value = res.data
  } catch (error) {
    console.error('Failed to check collect status:', error)
    isCollected.value = false
  }
}

const goToDetail = () => {
  router.push(`/hospital/${props.hospital.id}`)
}

const toggleCollect = async () => {
  try {
    if (isCollected.value) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.HOSPITAL,
        targetId: props.hospital.id
      })
      ElMessage.success('取消收藏成功')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.HOSPITAL,
        targetId: props.hospital.id
      })
      ElMessage.success('收藏成功')
    }
    isCollected.value = !isCollected.value
  } catch (error) {
    console.error('Failed to toggle collection:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  checkCollectStatus()
})
</script>

<style scoped lang="scss">
.hospital-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 12px;

  &:hover {
    transform: translateY(-2px);
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.hospital-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.hospital-info {
  flex: 1;

  .hospital-name {
    margin: 0 0 6px;
    font-size: 16px;
    color: #303133;
    font-weight: 600;
  }

  .hospital-tags {
    display: flex;
    gap: 6px;
  }
}

.hospital-body {
  margin-bottom: 12px;

  .hospital-location {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #909399;
    font-size: 13px;
    margin-bottom: 8px;

    .el-icon {
      color: #409eff;
      font-size: 14px;
    }
  }

  .hospital-introduction {
    color: #606266;
    font-size: 13px;
    line-height: 1.5;
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.hospital-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;

  .el-button {
    font-size: 13px;
  }
}
</style>
