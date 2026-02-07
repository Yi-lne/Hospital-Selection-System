import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 医院筛选状态管理
 */
export const useHospitalStore = defineStore('hospital', () => {
  // 筛选条件（UI状态）
  const filterParams = ref({
    page: 1,
    pageSize: 10,
    areaCode: undefined, // 区县编码
    provinceCode: undefined, // 省份编码
    cityCode: undefined, // 城市编码
    level: undefined,
    departmentCode: undefined, // ✅ 替换 diseaseCode：科室名称
    name: undefined
  })

  // 选中的地区
  const selectedArea = ref(null)

  // 选中的等级（与filterParams.level保持同步）
  const selectedLevel = computed({
    get: () => filterParams.value.level,
    set: (val) => setLevel(val)
  })

  // 选中的疾病编码
  const selectedDiseaseCode = ref(undefined)

  // 转换后的API参数（发送给后端）
  const apiParams = computed(() => {
    // 直接使用等级代码（UI已改为使用英文代码）
    const hospitalLevel = filterParams.value.level

    const params = {
      page: filterParams.value.page,
      pageSize: filterParams.value.pageSize,
      provinceCode: filterParams.value.provinceCode,
      cityCode: filterParams.value.cityCode,
      areaCode: filterParams.value.areaCode,
      hospitalLevel,
      department: filterParams.value.departmentCode  // ✅ 替换 diseaseCode
      // 注意：name字段不包含在筛选API中，筛选API应该使用filterHospitals
      // 如果有name搜索需求，应该使用专门的搜索接口
    }

    return params
  })

  /**
   * 设置筛选参数
   */
  function setFilterParams(params) {
    filterParams.value = { ...filterParams.value, ...params }
  }

  /**
   * 重置筛选条件
   */
  function resetFilters() {
    // 直接设置filterParams，不要设置computed属性
    filterParams.value = {
      page: 1,
      pageSize: 10,
      areaCode: undefined,
      provinceCode: undefined,
      cityCode: undefined,
      level: undefined,
      departmentCode: undefined,  // ✅ 替换 diseaseCode
      name: undefined
    }
    selectedArea.value = null
    selectedDiseaseCode.value = undefined  // 注意：这个变量名可以改为selectedDepartmentCode
    // selectedLevel是computed，会自动更新
  }

  /**
   * 设置地区
   */
  function setArea(area) {
    selectedArea.value = area
    setFilterParams({ areaCode: area?.code || undefined })
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
   * 设置科室编码
   */
  function setDepartmentCode(departmentCode) {
    selectedDiseaseCode.value = departmentCode  // 暂时保留变量名，避免破坏现有代码
    setFilterParams({ departmentCode })
  }

  /**
   * 设置搜索关键词
   */
  function setKeyword(keyword) {
    setFilterParams({ name: keyword || undefined, page: 1 })
  }

  /**
   * 设置分页
   */
  function setPagination(page, pageSize) {
    setFilterParams({ page, pageSize })
  }

  return {
    filterParams,
    apiParams,
    selectedArea,
    selectedLevel,
    selectedDiseaseCode,  // 保留名称，实际上是科室
    setFilterParams,
    resetFilters,
    setArea,
    setLevel,
    setDepartmentCode,  // ✅ 新增
    setKeyword,
    setPagination
  }
})
