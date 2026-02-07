import { ref } from 'vue'
import { getAreaTree } from '@/api/area'

/**
 * 地区选择组合式函数
 */
export function useArea() {
  const areas = ref([])
  const loading = ref(false)

  /**
   * 加载地区数据
   */
  const loadAreas = async () => {
    try {
      loading.value = true
      const res = await getAreaTree()
      areas.value = res.data
    } catch (error) {
      console.error('加载地区列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 转换为级联选择器格式
   */
  const transformToCascader = (areas) => {
    return areas.map(area => ({
      value: area.id,
      label: area.name,
      children: area.children ? transformToCascader(area.children) : undefined
    }))
  }

  return {
    areas,
    loading,
    loadAreas,
    transformToCascader
  }
}
