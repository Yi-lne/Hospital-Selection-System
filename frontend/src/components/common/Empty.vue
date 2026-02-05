<template>
  <div class="empty-container">
    <el-empty :description="description">
      <template #image>
        <el-icon :size="100" color="#dcdfe6">
          <Box />
        </el-icon>
      </template>
      <el-button v-if="showAction" type="primary" @click="$emit('action')">
        {{ actionText }}
      </el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { Box } from '@element-plus/icons-vue'

defineProps<{
  description?: string
  showAction?: boolean
  actionText?: string
}>()

defineEmits<{
  action: []
}>()
</script>

<style scoped lang="scss">
.empty-container {
  padding: 60px 20px;
  text-align: center;
}
</style>
