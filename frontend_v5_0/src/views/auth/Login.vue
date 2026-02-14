<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
        <h1>医院选择系统</h1>
        <p>欢迎回来</p>
      </div>

      <LoginForm @success="handleLoginSuccess" @switch-to-register="$router.push('/auth/register')" />
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { OfficeBuilding } from '@element-plus/icons-vue'
import LoginForm from '@/components/user/LoginForm.vue'

const router = useRouter()
const route = useRoute()

const handleLoginSuccess = () => {
  const redirect = route.query.redirect
  router.push(redirect || '/home')
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: #fff;

  h1 {
    margin: 16px 0 8px;
    font-size: 32px;
    font-weight: 600;
  }

  p {
    margin: 0;
    font-size: 16px;
    opacity: 0.9;
  }
}
</style>
