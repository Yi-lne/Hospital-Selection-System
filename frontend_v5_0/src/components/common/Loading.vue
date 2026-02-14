<template>
  <div class="loading-container">
    <el-icon class="is-loading" :size="40">
      <Loading />
    </el-icon>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup>
import { Loading as LoadingIcon } from '@element-plus/icons-vue'

defineProps({
  text: String
})
</script>

<style scoped lang="scss">
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #409eff;
}

.loading-text {
  margin-top: 16px;
  color: #909399;
  font-size: 14px;
}
</style>
