<template>
  <div class="hospital-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医院筛选</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="content-row">
      <!-- 筛选侧边栏 -->
      <el-col :xs="24" :sm="24" :md="6" class="filter-col">
        <el-card class="filter-card sticky-card">
          <!-- AI智能推荐 -->
          <AIRecommendation @recommend-success="handleAIRecommend" />

          <el-divider>或手动筛选</el-divider>
          <template #header>
            <div class="filter-header">
              <span>筛选条件</span>
              <el-button link type="primary" @click="resetFilters">重置</el-button>
            </div>
          </template>

          <!-- 地区选择 -->
          <div class="filter-section">
            <h4>地区</h4>
            <el-cascader
              v-model="cascaderValue"
              :options="areaOptions"
              :props="{ expandTrigger: 'hover', label: 'label', value: 'value', children: 'children', checkStrictly: true }"
              placeholder="请选择地区"
              clearable
              style="width: 100%;"
              @change="handleAreaChange"
            />
          </div>

          <!-- 科室筛选 -->
          <div class="filter-section">
            <h4>科室筛选</h4>
            <el-select
              v-model="hospitalStore.selectedDeptName"
              placeholder="请选择科室"
              clearable
              style="width: 100%;"
              @change="handleDepartmentChange"
            >
              <el-option
                v-for="dept in departmentOptions"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.deptName"
              />
            </el-select>
          </div>

          <!-- 医院等级 -->
          <div class="filter-section">
            <h4>医院级别</h4>
            <el-radio-group v-model="hospitalStore.selectedLevel" @change="handleFilterChange">
              <el-radio :value="undefined">全部</el-radio>
              <el-radio value="grade3A">三甲</el-radio>
              <el-radio value="grade3B">三乙</el-radio>
              <el-radio value="grade2A">二甲</el-radio>
              <el-radio value="grade2B">二乙</el-radio>
              <el-radio value="grade2C">二丙</el-radio>
              <el-radio value="grade1A">一甲</el-radio>
            </el-radio-group>
          </div>

          <!-- 排序优先 -->
          <div class="filter-section">
            <h4>排序优先</h4>
            <el-radio-group v-model="hospitalStore.sortPriority" @change="handleFilterChange">
              <el-radio value="level">级别优先</el-radio>
              <el-radio value="rating">评分优先</el-radio>
            </el-radio-group>
          </div>
        </el-card>
      </el-col>

      <!-- 医院列表 -->
      <el-col :xs="24" :sm="24" :md="18" class="list-col">
        <el-card v-loading="loading" class="list-card">
          <template v-if="hospitalList.length > 0">
            <el-row :gutter="16">
              <el-col
                v-for="hospital in hospitalList"
                :key="hospital.id"
                :xs="24"
                :sm="12"
                :md="8"
                :lg="8"
                :xl="8"
                style="margin-bottom: 16px;"
              >
                <HospitalCard :hospital="hospital" :sort-priority="hospitalStore.sortPriority" />
              </el-col>
            </el-row>
          </template>
          <Empty v-else description="暂无医院数据" />

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="hospitalStore.filterParams.page"
              v-model:page-size="hospitalStore.filterParams.pageSize"
              :total="total"
              :page-sizes="[9, 18, 27]"
              layout="total, sizes, prev, pager, next, jumper"
              prev-text="上一页"
              next-text="下一页"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useHospitalStore } from '@/stores'
import { getHospitalList, filterHospitals, searchHospitals } from '@/api/hospital'
import { getAreaTree } from '@/api/area'
import { getAllDepartments } from '@/api/department'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import Empty from '@/components/common/Empty.vue'
import AIRecommendation from '@/components/hospital/AIRecommendation.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const hospitalStore = useHospitalStore()

const loading = ref(false)
const hospitalList = ref([])
const total = ref(0)
const areaOptions = ref([])
const departmentOptions = ref([])

// AI推荐结果缓存
const aiRecommendResult = ref(null)

// 计算属性：将 store 中的 selectedArea 对象转换为级联选择器需要的数组格式
const cascaderValue = computed({
  get: () => {
    // 如果没有选中地区，返回空数组
    if (!hospitalStore.selectedArea || !hospitalStore.selectedArea.code) {
      return []
    }

    // 从 areaOptions 中查找完整路径
    const findPath = (options, targetCode, path = []) => {
      for (const node of options) {
        const currentPath = [...path, node.value]
        if (node.value === targetCode) {
          return currentPath
        }
        if (node.children) {
          const found = findPath(node.children, targetCode, currentPath)
          if (found) return found
        }
      }
      return null
    }

    const path = findPath(areaOptions.value, hospitalStore.selectedArea.code)
    return path || []
  },
  set: (value) => {
    // value 是级联选择器返回的数组
    // 当用户清空选择时，value 为空数组
    if (!value || value.length === 0) {
      hospitalStore.setArea(null)
    }
    // @change 事件会触发 handleAreaChange，不需要在这里处理
  }
})

// 转换地区数据为级联选择器格式
const transformAreaData = (areas) => {
  return areas.map(area => ({
    value: area.code,  // 使用 code 而不是 id，因为医院的 area_code 存储的是 code
    label: area.name,
    level: area.level,  // 传递 level 以便区分省/市/区
    children: area.children ? transformAreaData(area.children) : undefined
  }))
}

// 加载地区数据
const loadAreas = async () => {
  try {
    const res = await getAreaTree()
    areaOptions.value = transformAreaData(res.data || [])
  } catch (error) {
    console.error('加载地区失败:', error)
  }
}

// 加载所有科室数据
const loadDepartments = async () => {
  try {
    const res = await getAllDepartments()
    // 按科室名称去重
    const uniqueDepartments = []
    const seenNames = new Set()

    for (const dept of (res.data || [])) {
      if (!seenNames.has(dept.deptName)) {
        seenNames.add(dept.deptName)
        uniqueDepartments.push(dept)
      }
    }

    departmentOptions.value = uniqueDepartments
  } catch (error) {
    console.error('加载科室失败:', error)
  }
}

// 判断是否有筛选条件或AI推荐结果
const hasFilterConditions = () => {
  // 优先使用AI推荐结果
  if (aiRecommendResult.value) {
    return true
  }

  const params = hospitalStore.filterParams
  return !!(
    params.provinceCode ||
    params.cityCode ||
    params.areaCode ||
    params.level ||
    params.deptName
  )
}

// 加载医院列表
const loadHospitals = async () => {
  try {
    loading.value = true

    // 如果有AI推荐结果缓存，直接使用
    if (aiRecommendResult.value) {
      hospitalList.value = aiRecommendResult.value.list
      total.value = aiRecommendResult.value.total
      return
    }

    let res
    const keyword = route.query.keyword

    // 根据不同场景调用不同的API
    if (keyword) {
      // 场景1：有搜索关键词 → 使用搜索API
      const params = {
        page: hospitalStore.filterParams.page,
        pageSize: hospitalStore.filterParams.pageSize
      }
      res = await searchHospitals(keyword, params)
    } else if (hasFilterConditions()) {
      // 场景2：有筛选条件 → 使用筛选API
      res = await filterHospitals(hospitalStore.apiParams)
    } else {
      // 场景3：无搜索、无筛选 → 使用列表API
      res = await getHospitalList(hospitalStore.apiParams)
    }

    // 映射字段名：hospitalName -> name
    hospitalList.value = (res.data.list || []).map((item) => ({
      id: item.id,
      name: item.hospitalName || item.name,
      level: item.hospitalLevel || item.level,
      province: item.provinceName || item.province,
      city: item.cityName || item.city,
      address: item.address,
      rating: item.rating,
      // 不设置avatar，让组件使用默认头像
      avatar: null
    }))
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载医院失败:', error)
  } finally {
    loading.value = false
  }
}

// 地区改变（支持省/市/区三级筛选）
const handleAreaChange = (value) => {
  // value 是级联选择器返回的路径数组，如 ['110000'] 或 ['110000', '110100'] 或 ['110000', '110100', '110101']
  // 最后一个元素是用户当前选择的地区
  if (value && value.length > 0) {
    const selectedCode = value[value.length - 1]

    // 从 areaOptions 中查找选中节点的 level 信息
    const findNodeLevel = (options, code, currentLevel = 1) => {
      for (const node of options) {
        if (node.value === code) {
          return { code, level: node.level }
        }
        if (node.children) {
          const found = findNodeLevel(node.children, code, currentLevel + 1)
          if (found) return found
        }
      }
      return null
    }

    const selectedArea = findNodeLevel(areaOptions.value, selectedCode)
    hospitalStore.setArea(selectedArea)
  } else {
    hospitalStore.setArea(null)
  }
  loadHospitals()
}

// 科室改变
const handleDepartmentChange = (value) => {
  hospitalStore.setDeptName(value)
  loadHospitals()
}

// 筛选条件改变
const handleFilterChange = () => {
  hospitalStore.filterParams.page = 1
  loadHospitals()
}

// 分页改变
const handlePageChange = (page) => {
  hospitalStore.setPagination(page, hospitalStore.filterParams.pageSize)
  loadHospitals()
}

const handleSizeChange = (size) => {
  hospitalStore.setPagination(1, size)
  loadHospitals()
}

// 重置筛选
const resetFilters = () => {
  // 清除AI推荐缓存
  aiRecommendResult.value = null

  // 重置筛选条件
  hospitalStore.resetFilters()
  loadHospitals()
}

// 处理AI推荐成功
const handleAIRecommend = (data) => {
  // 保存AI推荐结果到缓存
  aiRecommendResult.value = {
    list: data.list,
    total: data.total
  }

  // 更新医院列表
  hospitalList.value = data.list
  total.value = data.total

  // 重置分页到第一页
  hospitalStore.filterParams.page = 1

  // 滚动到列表区域
  const listCard = document.querySelector('.list-card')
  if (listCard) {
    listCard.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  ElMessage.success(`AI推荐完成，共找到 ${data.total} 家医院`)
}

// 监听路由参数（搜索关键词）
watch(
  () => route.query.keyword,
  (keyword) => {
    // 如果有AI推荐结果，不要清除
    if (aiRecommendResult.value) {
      return
    }
    // keyword变化时重新加载医院列表
    loadHospitals()
  },
  { immediate: true }
)

onMounted(() => {
  loadAreas()
  loadDepartments()
  if (!route.query.keyword) {
    loadHospitals()
  }
})
</script>

<style scoped lang="scss">
.hospital-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-row {
    margin-top: 20px;
    align-items: flex-start;
  }

  .filter-card {
    .filter-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .filter-section {
      margin-bottom: 24px;

      &:last-child {
        margin-bottom: 0;
      }

      h4 {
        margin: 0 0 12px;
        font-size: 14px;
        color: #606266;
      }
    }
  }

  .list-card {
    min-height: 400px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
