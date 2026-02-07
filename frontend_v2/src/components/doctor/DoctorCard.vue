<template>
  <el-card class="doctor-card" shadow="hover" @click="goToDetail">
    <div class="doctor-header">
      <el-avatar :size="64" :src="doctor.avatar || defaultAvatar" />
      <div class="doctor-info">
        <h3 class="doctor-name">{{ doctor.doctorName }}</h3>
        <div class="doctor-title">
          <el-tag size="small" type="warning">{{ doctor.title }}</el-tag>
        </div>
      </div>
    </div>

    <div class="doctor-body">
      <div class="doctor-row">
        <span class="label">所属医院:</span>
        <span class="value">{{ doctor.hospitalName }}</span>
      </div>
      <div class="doctor-row">
        <span class="label">所属科室:</span>
        <span class="value">{{ doctor.deptName }}</span>
      </div>
      <div v-if="doctor.specialty" class="doctor-row">
        <span class="label">擅长:</span>
        <span class="value">{{ doctor.specialty }}</span>
      </div>
    </div>

    <div class="doctor-footer">
      <el-button text type="primary" @click.stop="goToDetail">
        查看详情 <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  doctor: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const goToDetail = () => {
  router.push(`/doctor/${props.doctor.id}`)
}
</script>

<style scoped lang="scss">
.doctor-card {
  cursor: pointer;
  transition: transform 0.3s;

  &:hover {
    transform: translateY(-4px);
  }

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.doctor-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.doctor-info {
  flex: 1;

  .doctor-name {
    margin: 0 0 6px;
    font-size: 16px;
    color: #303133;
  }
}

.doctor-body {
  margin-bottom: 12px;

  .doctor-row {
    display: flex;
    font-size: 13px;
    margin-bottom: 6px;

    .label {
      color: #909399;
      min-width: 70px;
    }

    .value {
      color: #606266;
      flex: 1;
    }
  }
}

.doctor-footer {
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}
</style>
