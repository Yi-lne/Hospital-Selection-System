<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
        <h1>医院选择系统</h1>
        <p>欢迎回来</p>
      </div>

      <!-- 登录提示 -->
      <el-alert
        v-if="requiresAuthMessage"
        type="info"
        :closable="false"
        show-icon
        class="auth-alert"
      >
        <template #title>
          <span>{{ requiresAuthMessage }}</span>
        </template>
      </el-alert>

      <LoginForm @success="handleLoginSuccess" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { OfficeBuilding } from '@element-plus/icons-vue'
import LoginForm from '@/components/user/LoginForm.vue'

const router = useRouter()
const route = useRoute()

// 判断是否因为需要登录而跳转到此页面
const requiresAuthMessage = computed(() => {
  const redirect = route.query.redirect as string
  if (!redirect) return ''

  // 根据重定向路径显示不同的提示消息
  if (redirect.includes('/hospital/') && redirect.match(/\/hospital\/\d+/)) {
    return '查看医院详情需要登录，请先登录您的账号'
  } else if (redirect.includes('/doctor/') && redirect.match(/\/doctor\/\d+/)) {
    return '查看医生详情需要登录，请先登录您的账号'
  } else if (redirect.includes('/community/publish')) {
    return '发布话题需要登录，请先登录您的账号'
  } else if (redirect.includes('/user')) {
    return '访问个人中心需要登录，请先登录您的账号'
  } else if (redirect.includes('/message')) {
    return '查看消息需要登录，请先登录您的账号'
  } else {
    return '请先登录您的账号'
  }
})

const handleLoginSuccess = () => {
  const redirect = route.query.redirect as string
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

.auth-alert {
  margin-bottom: 20px;

  :deep(.el-alert__title) {
    font-size: 14px;
    line-height: 1.6;
  }
}
</style>
