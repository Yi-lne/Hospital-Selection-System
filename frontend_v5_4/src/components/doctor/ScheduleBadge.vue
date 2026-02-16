<template>
  <div class="schedule-badge">
    <el-tag :type="tagType" effect="plain">
      <el-icon><Clock /></el-icon>
      {{ scheduleText }}
    </el-tag>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Clock } from '@element-plus/icons-vue'

const props = defineProps({
  schedule: {
    type: String,
    default: ''
  }
})

const tagType = computed(() => {
  const schedule = props.schedule?.toLowerCase() || ''
  if (schedule.includes('全天')) return 'success'
  if (schedule.includes('上午') || schedule.includes('下午')) return 'primary'
  if (schedule.includes('晚上') || schedule.includes('夜')) return 'warning'
  return 'info'
})

const scheduleText = computed(() => {
  return props.schedule || '坐诊时间待定'
})
</script>

<style scoped lang="scss">
.schedule-badge {
  display: inline-block;

  .el-tag {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
</style>
