<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
        <h1>医院选择系统</h1>
        <p>创建新账号</p>
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="formData.phone"
            placeholder="请输入手机号"
            prefix-icon="Iphone"
            size="large"
            clearable
            maxlength="11"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="formData.nickname"
            placeholder="请输入昵称（可选）"
            prefix-icon="User"
            size="large"
            clearable
            maxlength="20"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <span>已有账号?</span>
          <el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { OfficeBuilding } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

// 手机号验证规则
const validatePhone = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { max: 20, message: '昵称最多20个字符', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true

        // 构建注册数据
        const registerData: any = {
          phone: formData.phone,
          password: formData.password
        }

        // 昵称可选
        if (formData.nickname.trim()) {
          registerData.nickname = formData.nickname.trim()
        }

        await userStore.register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-container {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.register-header {
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

.register-form {
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  width: 100%;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #606266;

  .el-link {
    margin-left: 8px;
  }
}
</style>
