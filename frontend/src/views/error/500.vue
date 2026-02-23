<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">500</div>
      <h1>服务器错误</h1>
      <p>抱歉，服务器出现了一些问题，请稍后再试</p>
      <el-button type="primary" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.error-content {
  text-align: center;

  .error-icon {
    font-size: 120px;
    font-weight: bold;
    color: #f56c6c;
    margin-bottom: 20px;
  }

  h1 {
    margin: 0 0 16px;
    font-size: 32px;
    color: #303133;
  }

  p {
    margin: 0 0 32px;
    font-size: 16px;
    color: #606266;
  }
}
</style>
