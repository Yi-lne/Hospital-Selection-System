import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { AuthManager } from '@/utils/auth'
import * as userApi from '@/api/user'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(AuthManager.getToken() || '')
  const userInfo = ref(AuthManager.getUserInfo())
  const roles = ref(AuthManager.getUserRoles() || [])
  const isLoading = ref(false)

  // 计算属性
  const isLogin = computed(() => !!token.value && AuthManager.isLoggedIn())
  const userId = computed(() => userInfo.value?.id || null)
  const userName = computed(() => userInfo.value?.nickname || '用户')
  const userAvatar = computed(() => userInfo.value?.avatar || '')
  const isAdmin = computed(() => {
    return roles.value.includes('admin') || roles.value.includes('ADMIN')
  })

  /**
   * 登录
   */
  async function login(loginData) {
    try {
      isLoading.value = true
      const res = await userApi.login(loginData)

      // 保存Token
      token.value = res.data.token
      AuthManager.setToken(res.data.token)

      // 保存用户信息
      userInfo.value = res.data.userInfo
      AuthManager.setUserInfo(res.data.userInfo)

      // 保存用户角色
      roles.value = res.data.userInfo.roles || []
      AuthManager.setUserRoles(res.data.userInfo.roles || [])
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 注册
   */
  async function register(registerData) {
    try {
      isLoading.value = true
      await userApi.register(registerData)
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 登出
   */
  async function logout() {
    try {
      await userApi.logout()
    } finally {
      // 清除本地状态
      token.value = ''
      userInfo.value = null
      roles.value = []
      AuthManager.clearAuth()
    }
  }

  /**
   * 获取用户信息
   */
  async function fetchUserInfo() {
    if (!isLogin.value) return

    try {
      const res = await userApi.getUserInfo()
      userInfo.value = res.data
      roles.value = res.data.roles || []
      AuthManager.setUserInfo(res.data)
      AuthManager.setUserRoles(res.data.roles || [])
    } catch (error) {
      // Token可能已过期，清除本地状态
      logout()
    }
  }

  /**
   * 更新用户信息
   */
  async function updateUserInfo(updateData) {
    try {
      isLoading.value = true
      await userApi.updateUserInfo(updateData)

      // 重新获取用户信息
      await fetchUserInfo()
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 修改密码
   */
  async function changePassword(passwordData) {
    try {
      isLoading.value = true
      await userApi.changePassword(passwordData)
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 上传头像
   */
  async function uploadAvatar(file) {
    try {
      isLoading.value = true
      const res = await userApi.uploadAvatar(file)

      // 更新头像
      if (userInfo.value) {
        userInfo.value.avatar = res.data
        AuthManager.setUserInfo(userInfo.value)
      }
    } finally {
      isLoading.value = false
    }
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    isLoading,

    // 计算属性
    isLogin,
    userId,
    userName,
    userAvatar,
    isAdmin,

    // 方法
    login,
    register,
    logout,
    fetchUserInfo,
    updateUserInfo,
    changePassword,
    uploadAvatar
  }
})
