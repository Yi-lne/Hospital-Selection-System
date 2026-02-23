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
import { ref, reactive, watch } from 'vue'
import { useUserStore } from '@/stores'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const emit = defineEmits(['success', 'switch-to-register'])

const userStore = useUserStore()
const formRef = ref()
const captchaRef = ref()
const loading = ref(false)

const formData = reactive({
  phone: '',
  password: '',
  captchaId: '',
  moveX: null
})

// 监听表单字段变化，实时更新滑块是否可拖动
watch(() => [formData.phone, formData.password], () => {
  const isPhoneValid = formData.phone && /^1[3-9]\d{9}$/.test(formData.phone)
  const isPasswordValid = formData.password && formData.password.length >= 6

  // 只有当手机号和密码都有效时，才允许拖动滑块
  const canDrag = isPhoneValid && isPasswordValid
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
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
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
  // 验证手机号和密码是否已填写
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
  // 验证手机号格式
  if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    ElMessage.warning('请输入正确的手机号')
    captchaRef.value?.setCanDrag(false)
    return
  }
  // 验证密码长度
  if (formData.password.length < 6) {
    ElMessage.warning('密码长度至少6个字符')
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

  // 只有当手机号和密码都有效时，才允许拖动滑块
  captchaRef.value?.setCanDrag(isPhoneValid && isPasswordValid)
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaVerified.value = false
  formData.captchaId = ''
  formData.moveX = null
  captchaRef.value?.refreshCaptcha()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.login({
          phone: formData.phone,
          password: formData.password,
          captchaId: formData.captchaId,
          moveX: formData.moveX
        })
        emit('success')
      } catch (error) {
        // 登录失败，刷新验证码
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}

defineExpose({
  resetForm: () => {
    formRef.value?.resetFields()
    refreshCaptcha()
  }
})
</script>

<style scoped lang="scss">
.login-form {
  width: 450px;
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
