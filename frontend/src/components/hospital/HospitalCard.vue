<template>
  <el-card class="hospital-card" shadow="hover" @click="goToDetail">
    <div class="hospital-header">
      <el-avatar :size="64" :src="hospital.avatar || defaultAvatar" shape="square" />
      <div class="hospital-info">
        <h3 class="hospital-name">{{ hospital.name }}</h3>
        <div class="hospital-tags">
          <el-tag size="small" type="danger">{{ displayLevel }}</el-tag>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Location, ArrowRight, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { formatHospitalLevel } from '@/utils/hospital'
import type { HospitalSimple } from '@/types/hospital'

const props = defineProps<{
  hospital: HospitalSimple
}>()

const router = useRouter()
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 将医院等级代码转换为中文显示
const displayLevel = computed(() => {
  return formatHospitalLevel(props.hospital.level)
})

const goToDetail = () => {
  router.push(`/hospital/${props.hospital.id}`)
}

const toggleCollect = () => {
  isCollected.value = !isCollected.value
  ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏')
}
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
