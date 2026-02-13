<template>
  <div class="profile-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">个人资料</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :sm="8" :md="6">
        <el-card class="avatar-card">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userStore.userAvatar || defaultAvatar">
              {{ userStore.userName?.charAt(0) || '用' }}
            </el-avatar>
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <el-button size="small">更换头像</el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="16" :md="18">
        <el-card class="info-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form
                ref="formRef"
                :model="formData"
                :rules="rules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="手机号">
                  <el-input v-model="formData.phone" disabled />
                </el-form-item>

                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="formData.nickname" placeholder="请输入昵称" maxlength="20" />
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="formData.gender">
                    <el-radio :value="0">保密</el-radio>
                    <el-radio :value="1">男</el-radio>
                    <el-radio :value="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="handleSave">
                    保存
                  </el-button>
                  <el-button @click="handleReset">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordData"
                :rules="passwordRules"
                label-width="100px"
                class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordData.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordData.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordData.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                    修改密码
                  </el-button>
                  <el-button @click="passwordFormRef?.resetFields()">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const activeTab = ref('basic')
const loading = ref(false)
const passwordLoading = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formData = reactive({
  phone: '',
  nickname: '',
  gender: 0
})

const passwordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const uploadUrl = computed(() => import.meta.env.VITE_API_BASE_URL + '/user/avatar')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordData.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  nickname: [
    { max: 20, message: '昵称最多20个字符', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 初始化表单数据
const initFormData = () => {
  if (userStore.userInfo) {
    Object.assign(formData, {
      phone: userStore.userInfo.phone || '',
      nickname: userStore.userInfo.nickname || '',
      gender: userStore.userInfo.gender ?? 0
    })
  }
}

// 保存基本信息
const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await userStore.updateUserInfo({
          nickname: formData.nickname,
          gender: formData.gender
        })
        ElMessage.success('保存成功')
      } catch (error) {
        console.error('Failed to update user info:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 重置表单
const handleReset = () => {
  initFormData()
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        passwordLoading.value = true
        await userStore.changePassword({
          oldPassword: passwordData.oldPassword,
          newPassword: passwordData.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordFormRef.value?.resetFields()
        userStore.logout()
        window.location.href = '/login'
      } catch (error) {
        console.error('Failed to change password:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

// 上传前校验
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    ElMessage.success('头像上传成功')
    userStore.fetchUserInfo()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

onMounted(() => {
  initFormData()
})
</script>

<style scoped lang="scss">
.profile-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-row {
    margin-top: 20px;
  }

  .avatar-card {
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
      padding: 20px 0;
    }
  }

  .info-card {
    .profile-form,
    .password-form {
      max-width: 600px;
      margin-top: 20px;
    }
  }
}
</style>
