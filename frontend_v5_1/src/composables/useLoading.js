import { ref } from 'vue'

/**
 * 加载状态组合式函数
 */
export function useLoading(initialState = false) {
  const loading = ref(initialState)

  /**
   * 开始加载
   */
  const startLoading = () => {
    loading.value = true
  }

  /**
   * 结束加载
   */
  const stopLoading = () => {
    loading.value = false
  }

  /**
   * 切换加载状态
   */
  const toggleLoading = () => {
    loading.value = !loading.value
  }

  return {
    loading,
    startLoading,
    stopLoading,
    toggleLoading
  }
}
