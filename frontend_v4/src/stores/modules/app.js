import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 应用全局状态管理
 */
export const useAppStore = defineStore('app', () => {
  // 侧边栏状态
  const sidebarCollapsed = ref(false)

  // 设备类型
  const device = ref('desktop')

  // 加载状态
  const isLoading = ref(false)

  /**
   * 切换侧边栏
   */
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  /**
   * 设置设备类型
   */
  function setDevice(type) {
    device.value = type
  }

  /**
   * 设置加载状态
   */
  function setLoading(loading) {
    isLoading.value = loading
  }

  return {
    sidebarCollapsed,
    device,
    isLoading,
    toggleSidebar,
    setDevice,
    setLoading
  }
})
