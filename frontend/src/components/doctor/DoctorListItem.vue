<template>
  <div class="doctor-list-item" @click="handleClick">
    <div class="item-header">
      <div class="doctor-info">
        <h3 class="doctor-name">{{ doctor.doctorName }}</h3>
        <el-tag type="success" size="small">{{ doctor.title }}</el-tag>
      </div>
      <div v-if="showRating" class="rating">
        <el-rate
          v-model="ratingValue"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value}"
        />
      </div>
    </div>

    <div class="item-content">
      <div class="info-row">
        <span class="label">医院：</span>
        <span class="value">{{ doctor.hospitalName }}</span>
      </div>
      <div class="info-row">
        <span class="label">科室：</span>
        <span class="value">{{ doctor.deptName }}</span>
      </div>
      <div v-if="doctor.specialty" class="info-row">
        <span class="label">擅长：</span>
        <span class="value">{{ doctor.specialty }}</span>
      </div>
      <div v-if="doctor.scheduleTime" class="info-row">
        <span class="label">坐诊：</span>
        <span class="value">{{ doctor.scheduleTime }}</span>
      </div>
    </div>

    <div v-if="showFooter" class="item-footer">
      <div v-if="doctor.consultationFee" class="fee">
        <span class="label">挂号费：</span>
        <span class="value">¥{{ doctor.consultationFee }}</span>
      </div>
      <div class="stats">
        <span>{{ doctor.reviewCount || 0 }}条评价</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  doctor: {
    type: Object,
    required: true
  },
  showRating: {
    type: Boolean,
    default: true
  },
  showFooter: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['click'])

const ratingValue = computed(() => {
  return props.doctor.rating || 0
})

const handleClick = () => {
  emit('click', props.doctor)
}
</script>

<style scoped lang="scss">
.doctor-list-item {
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .doctor-info {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 12px;

      .doctor-name {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .rating {
      flex-shrink: 0;
    }
  }

  .item-content {
    margin-bottom: 16px;

    .info-row {
      display: flex;
      margin-bottom: 8px;
      font-size: 14px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        min-width: 60px;
        color: #909399;
      }

      .value {
        flex: 1;
        color: #606266;
      }
    }
  }

  .item-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #f0f0f0;

    .fee {
      font-size: 14px;

      .label {
        color: #909399;
      }

      .value {
        color: #f56c6c;
        font-weight: 600;
      }
    }

    .stats {
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
