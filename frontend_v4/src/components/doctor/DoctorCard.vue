<template>
  <el-card class="doctor-card" shadow="hover">
    <!-- 医生头部 -->
    <div class="doctor-header" @click="handleClick">
      <el-avatar :size="60" :src="doctor.avatar || defaultAvatar">
        {{ doctor.doctorName?.charAt(0) || '医' }}
      </el-avatar>
      <div class="doctor-info">
        <h4>{{ doctor.doctorName }}</h4>
        <p class="title">{{ doctor.title || '医师' }}</p>
        <p class="department">{{ doctor.hospitalName }} - {{ doctor.deptName }}</p>
      </div>
    </div>

    <!-- 医生详情 -->
    <div class="doctor-details">
      <div class="detail-item">
        <span class="label">擅长：</span>
        <span class="value">{{ doctor.specialty || '暂无' }}</span>
      </div>
      <div class="detail-item">
        <span class="label">坐诊时间：</span>
        <span class="value">{{ doctor.scheduleTime || '暂无' }}</span>
      </div>
      <div class="detail-item">
        <span class="label">挂号费：</span>
        <span class="value fee">¥{{ doctor.consultationFee || '0' }}</span>
      </div>
    </div>

    <!-- 评分和收藏 -->
    <div class="doctor-actions">
      <div class="rating">
        <el-rate v-model="displayRating" disabled show-score text-color="#ff9900" />
        <span class="rating-text">{{ displayRating }}分</span>
        <span class="review-count">({{ doctor.reviewCount || 0 }}条评价)</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  doctor: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const displayRating = computed(() => {
  return props.doctor.rating || 0
})

const handleClick = () => {
  emit('click', props.doctor)
}
</script>

<style scoped lang="scss">
.doctor-card {
  .doctor-header {
    display: flex;
    gap: 16px;
    margin-bottom: 16px;

    .doctor-info {
      flex: 1;

      h4 {
        margin: 0 0 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .title {
        margin: 0 0 4px;
        font-size: 14px;
        color: #67c23a;
      }

      .department {
        margin: 0;
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .doctor-details {
    margin-bottom: 16px;

    .detail-item {
      display: flex;
      margin-bottom: 8px;
      font-size: 14px;

      .label {
        color: #909399;
        min-width: 80px;
      }

      .value {
        color: #606266;
        flex: 1;

        &.fee {
          color: #f56c6c;
          font-weight: 600;
        }
      }
    }
  }

  .doctor-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    .rating {
      display: flex;
      align-items: center;
      gap: 8px;

      .rating-text {
        font-size: 14px;
        color: #ff9900;
        font-weight: 600;
      }

      .review-count {
        font-size: 13px;
        color: #909399;
      }
    }
  }
}
</style>
