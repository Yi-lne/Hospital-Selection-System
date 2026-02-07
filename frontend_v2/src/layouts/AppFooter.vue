<template>
  <el-footer class="app-footer">
    <div class="footer-content">
      <div class="footer-links">
        <a href="/about">关于我们</a>
        <el-divider direction="vertical" />
        <a href="/contact">联系我们</a>
        <el-divider direction="vertical" />
        <a href="/privacy">隐私政策</a>
        <el-divider direction="vertical" />
        <a href="/terms">服务条款</a>
      </div>

      <div class="footer-copyright">
        © {{ currentYear }} 医院选择系统. All rights reserved.
      </div>
    </div>
  </el-footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped lang="scss">
.app-footer {
  background: #f5f7fa;
  border-top: 1px solid #dcdfe6;
  padding: 20px 0;
  height: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
}

.footer-links {
  margin-bottom: 12px;

  a {
    color: #606266;
    text-decoration: none;
    transition: color 0.3s;

    &:hover {
      color: #409eff;
    }
  }
}

.footer-copyright {
  color: #909399;
  font-size: 14px;
}
</style>
