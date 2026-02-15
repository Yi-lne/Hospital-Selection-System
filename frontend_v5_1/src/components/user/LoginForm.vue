<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    class="login-form"
    @submit.prevent="handleSubmit"
  >
    <h2 class="form-title">登录</h2>

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
        @keyup.enter="handleSubmit"
      />
    </el-form-item>

    <el-form-item>
      <el-button
        type="primary"
        size="large"
        :loading="loading"
        class="submit-btn"
        @click="handleSubmit"
      >
        登录
      </el-button>
    </el-form-item>

    <div class="form-footer">
      <span>还没有账号?</span>
      <el-link type="primary" @click="$emit('switch-to-register')">立即注册</el-link>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores'

const emit = defineEmits(['success', 'switch-to-register'])

const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  phone: '',
  password: ''
})

// 手机号验证规则
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.login({
          phone: formData.phone,
          password: formData.password
        })
        emit('success')
      } finally {
        loading.value = false
      }
    }
  })
}

defineExpose({
  resetForm: () => formRef.value?.resetFields()
})
</script>

<style scoped lang="scss">
.login-form {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.form-title {
  margin: 0 0 30px;
  text-align: center;
  font-size: 24px;
  color: #303133;
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
