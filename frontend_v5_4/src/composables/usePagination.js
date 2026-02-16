import { ref, computed } from 'vue'

/**
 * 分页相关组合式函数
 */
export function usePagination(initialPageSize = 10) {
  const page = ref(1)
  const pageSize = ref(initialPageSize)
  const total = ref(0)

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

  /**
   * 设置分页
   */
  const setPagination = (pageNum, size) => {
    page.value = pageNum
    if (size) {
      pageSize.value = size
    }
  }

  /**
   * 重置分页
   */
  const resetPagination = () => {
    page.value = 1
  }

  /**
   * 设置总数
   */
  const setTotal = (count) => {
    total.value = count
  }

  return {
    page,
    pageSize,
    total,
    totalPages,
    setPagination,
    resetPagination,
    setTotal
  }
}
