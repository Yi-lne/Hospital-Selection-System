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

        <!-- 滑块验证码 -->
        <el-form-item prop="captcha">
          <SliderCaptcha
            ref="captchaRef"
            @captcha-verified="handleCaptchaVerified"
            @captcha-failed="handleCaptchaFailed"
            @before-verify="handleBeforeVerify"
            @captcha-refreshed="checkFormAndEnableSlider"
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
          <el-link type="primary" @click="$router.push('/auth/login')">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { OfficeBuilding } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const captchaRef = ref()
const loading = ref(false)

const formData = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  captchaId: '',
  moveX: null
})

// 监听表单字段变化，实时更新滑块是否可拖动
watch(() => [
  formData.phone,
  formData.password,
  formData.confirmPassword
], () => {
  const isPhoneValid = formData.phone && /^1[3-9]\d{9}$/.test(formData.phone)
  const isPasswordValid = formData.password && formData.password.length >= 6
  const isConfirmPasswordValid = formData.confirmPassword && formData.confirmPassword === formData.password

  // 只有当所有必填项都有效时，才允许拖动滑块
  const canDrag = isPhoneValid && isPasswordValid && isConfirmPasswordValid
  captchaRef.value?.setCanDrag(canDrag)
}, { deep: true })

// 验证码是否验证通过
const captchaVerified = ref(false)

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

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 验证码验证规则
const validateCaptcha = (rule, value, callback) => {
  if (!captchaVerified.value) {
    callback(new Error('请完成滑块验证'))
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
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nickname: [
    { max: 20, message: '昵称最多20个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, validator: validateCaptcha, trigger: 'change' }
  ]
}

// 处理验证码验证成功
const handleCaptchaVerified = (captchaData) => {
  formData.captchaId = captchaData.captchaId
  formData.moveX = captchaData.moveX
  captchaVerified.value = true
  // 触发表单验证
  formRef.value?.validateField('captcha')
}

// 处理验证码验证失败
const handleCaptchaFailed = () => {
  captchaVerified.value = false
  formData.captchaId = ''
  formData.moveX = null
  // 清除表单验证错误提示
  formRef.value?.clearValidate()
}

// 处理验证前检查
const handleBeforeVerify = () => {
  // 验证必填字段是否已填写
  if (!formData.phone) {
    ElMessage.warning('请先输入手机号')
    captchaRef.value?.setCanDrag(false)
    return
  }
  if (!formData.password) {
    ElMessage.warning('请先输入密码')
    captchaRef.value?.setCanDrag(false)
    return
  }
  if (!formData.confirmPassword) {
    ElMessage.warning('请先确认密码')
    captchaRef.value?.setCanDrag(false)
    return
  }

  // 验证手机号格式
  if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    ElMessage.warning('请输入正确的手机号')
    captchaRef.value?.setCanDrag(false)
    return
  }

  // 验证密码长度
  if (formData.password.length < 6) {
    ElMessage.warning('密码长度为6-20个字符')
    captchaRef.value?.setCanDrag(false)
    return
  }

  // 验证两次密码是否一致
  if (formData.password !== formData.confirmPassword) {
    ElMessage.warning('两次输入密码不一致')
    captchaRef.value?.setCanDrag(false)
    return
  }

  // 验证通过，允许拖动
  captchaRef.value?.setCanDrag(true)
}

// 检查表单并启用滑块（验证码刷新后调用）
const checkFormAndEnableSlider = () => {
  const isPhoneValid = formData.phone && /^1[3-9]\d{9}$/.test(formData.phone)
  const isPasswordValid = formData.password && formData.password.length >= 6
  const isConfirmPasswordValid = formData.confirmPassword && formData.confirmPassword === formData.password

  // 只有当所有必填项都有效时，才允许拖动滑块
  captchaRef.value?.setCanDrag(isPhoneValid && isPasswordValid && isConfirmPasswordValid)
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaVerified.value = false
  formData.captchaId = ''
  formData.moveX = null
  captchaRef.value?.refreshCaptcha()
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true

        // 构建注册数据
        const registerData = {
          phone: formData.phone,
          password: formData.password,
          captchaId: formData.captchaId,
          moveX: formData.moveX
        }

        // 昵称可选
        if (formData.nickname.trim()) {
          registerData.nickname = formData.nickname.trim()
        }

        await userStore.register(registerData)
        ElMessage.success('注册成功！正在为您自动登录...')
        // 注册成功后已经自动登录，跳转到首页
        setTimeout(() => {
          router.push('/home')
        }, 1000)
      } catch (error) {
        // 注册失败，刷新验证码
        refreshCaptcha()
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
