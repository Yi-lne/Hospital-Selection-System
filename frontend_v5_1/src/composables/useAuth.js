import { computed } from 'vue'
import { useUserStore } from '@/stores'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

/**
 * 认证相关组合式函数
 */
export function useAuth() {
  const userStore = useUserStore()
  const router = useRouter()

  const isLogin = computed(() => userStore.isLogin)
  const userId = computed(() => userStore.userId)
  const userInfo = computed(() => userStore.userInfo)

  /**
   * 检查登录状态，未登录则跳转登录页
   */
  const requireAuth = () => {
    if (!isLogin.value) {
      ElMessage.warning('请先登录')
      router.push({
        name: 'Login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
      return false
    }
    return true
  }

  /**
   * 登出
   */
  const logout = async () => {
    await userStore.logout()
    router.push('/home')
  }

  return {
    isLogin,
    userId,
    userInfo,
    requireAuth,
    logout
  }
}
