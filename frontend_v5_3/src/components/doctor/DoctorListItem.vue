<template>
  <div class="doctor-list-item" @click="$emit('click', doctor)">
    <el-avatar :size="48" :src="doctor.avatar || defaultAvatar">
      {{ doctor.doctorName?.charAt(0) || '医' }}
    </el-avatar>
    <div class="doctor-info">
      <h4>{{ doctor.doctorName }}</h4>
      <p class="title">{{ doctor.title || '医师' }}</p>
      <p class="hospital">{{ doctor.hospitalName }}</p>
      <div class="meta">
        <span class="fee">¥{{ doctor.consultationFee || '0' }}</span>
        <el-rate v-model="displayRating" disabled size="small" />
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
  }
})

defineEmits(['click'])

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const displayRating = computed(() => {
  return props.doctor.rating || 0
})
</script>

<style scoped lang="scss">
.doctor-list-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;

  &:hover {
    background: #f5f7fa;
  }

  .doctor-info {
    flex: 1;

    h4 {
      margin: 0 0 4px;
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }

    .title {
      margin: 0 0 4px;
      font-size: 13px;
      color: #67c23a;
    }

    .hospital {
      margin: 0 0 8px;
      font-size: 13px;
      color: #909399;
    }

    .meta {
      display: flex;
      align-items: center;
      gap: 12px;

      .fee {
        font-size: 14px;
        color: #f56c6c;
        font-weight: 600;
      }
    }
  }
}
</style>
