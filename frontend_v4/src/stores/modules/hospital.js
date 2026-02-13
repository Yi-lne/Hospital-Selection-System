import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 医院筛选状态管理
 */
export const useHospitalStore = defineStore('hospital', () => {
  // 筛选条件（UI状态）
  const filterParams = ref({
    page: 1,
    pageSize: 9,
    areaCode: undefined,
    provinceCode: undefined,
    cityCode: undefined,
    level: undefined,
    deptName: undefined,
    name: undefined,
    sortPriority: 'level'  // 排序优先级：level-级别优先，rating-评分优先
  })

  // 选中的地区对象（包含level信息）
  const selectedArea = ref(null)

  // 选中的等级（与filterParams.level保持同步）
  const selectedLevel = computed({
    get: () => filterParams.value.level,
    set: (val) => setLevel(val)
  })

  // 选中的科室名称（单选）
  const selectedDeptName = ref(null)

  // 排序优先级（与filterParams.sortPriority保持同步）
  const sortPriority = computed({
    get: () => filterParams.value.sortPriority,
    set: (val) => setSortPriority(val)
  })

  // 转换后的API参数（发送给后端）
  const apiParams = computed(() => {
    // 直接使用等级代码（UI已改为使用英文代码）
    const hospitalLevel = filterParams.value.level

    // 科室名称截取前两个字进行模糊查询
    let deptName = filterParams.value.deptName
    if (deptName && deptName.length > 2) {
      deptName = deptName.substring(0, 2)
    }

    const params = {
      page: filterParams.value.page,
      pageSize: filterParams.value.pageSize,
      hospitalLevel,
      deptName,
      name: filterParams.value.name,
      sortBy: filterParams.value.sortPriority  // 映射到后端的 sortBy 字段
    }

    // 根据选中的地区级别，智能设置对应的字段
    if (selectedArea.value) {
      const area = selectedArea.value
      console.log('Setting area params:', area)

      if (area.level === 1) {
        // 选择了省级别
        params.provinceCode = area.code
        console.log('Set provinceCode:', area.code)
      } else if (area.level === 2) {
        // 选择了市级别
        params.cityCode = area.code
        console.log('Set cityCode:', area.code)
      } else if (area.level === 3) {
        // 选择了区/县级别
        params.areaCode = area.code
        console.log('Set areaCode:', area.code)
      }
    }

    console.log('Final api params:', params)
    return params
  })

  /**
   * 设置筛选参数
   */
  function setFilterParams(params) {
    filterParams.value = { ...filterParams.value, ...params }
  }

  /**
   * 设置地区
   */
  function setArea(area) {
    selectedArea.value = area
    // 清空所有地区相关字段
    filterParams.value.provinceCode = undefined
    filterParams.value.cityCode = undefined
    filterParams.value.areaCode = undefined

    // 根据选择的地区级别，智能设置对应字段
    if (area) {
      if (area.level === 1) {
        filterParams.value.provinceCode = area.code
      } else if (area.level === 2) {
        filterParams.value.cityCode = area.code
      } else if (area.level === 3) {
        filterParams.value.areaCode = area.code
      }
    }
  }

  /**
   * 设置等级
   */
  function setLevel(level) {
    // 不要设置selectedLevel.value，因为它是computed属性
    // 只设置filterParams，computed会自动更新
    setFilterParams({ level })
  }

  /**
   * 设置科室名称（单选）
   */
  function setDeptName(deptName) {
    selectedDeptName.value = deptName
    setFilterParams({ deptName })
  }

  /**
   * 设置搜索关键词
   */
  function setKeyword(keyword) {
    setFilterParams({ name: keyword || undefined })
  }

  /**
   * 设置排序优先级
   */
  function setSortPriority(priority) {
    setFilterParams({ sortPriority: priority })
  }

  /**
   * 设置分页
   */
  function setPagination(page, pageSize) {
    setFilterParams({ page, pageSize })
  }

  /**
   * 重置筛选条件
   */
  function resetFilters() {
    // 重置筛选条件
    filterParams.value = {
      page: 1,
      pageSize: 9,
      areaCode: undefined,
      provinceCode: undefined,
      cityCode: undefined,
      level: undefined,
      deptName: undefined,
      name: undefined,
      sortPriority: 'level'  // 默认级别优先
    }
    selectedArea.value = null
    selectedDeptName.value = null
  }

  return {
    filterParams,
    apiParams,
    selectedArea,
    selectedLevel,
    selectedDeptName,
    sortPriority,
    setFilterParams,
    setArea,
    setLevel,
    setDeptName,
    setKeyword,
    setSortPriority,
    setPagination,
    resetFilters
  }
})
