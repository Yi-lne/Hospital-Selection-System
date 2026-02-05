<template>
  <div class="hospital-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医院列表</span>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="content-row">
      <!-- 筛选侧边栏 -->
      <el-col :xs="24" :sm="24" :md="6" class="filter-col">
        <el-card class="filter-card sticky-card">
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
              v-model="selectedArea"
              :options="areaOptions"
              :props="{ expandTrigger: 'hover', label: 'label', value: 'value', children: 'children' }"
              placeholder="请选择地区"
              clearable
              @change="handleAreaChange"
            />
          </div>

          <!-- 疾病分类 -->
          <div class="filter-section">
            <h4>疾病分类</h4>
            <el-cascader
              v-model="selectedDisease"
              :options="diseaseOptions"
              :props="{ expandTrigger: 'hover', value: 'code', label: 'label', children: 'children' }"
              placeholder="请选择疾病（可选）"
              clearable
              @change="handleDiseaseChange"
            />
          </div>

          <!-- 医院等级 -->
          <div class="filter-section">
            <h4>医院等级</h4>
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
        </el-card>
      </el-col>

      <!-- 医院列表 -->
      <el-col :xs="24" :sm="24" :md="18" class="list-col">
        <el-card v-loading="loading" class="list-card">
          <template v-if="hospitalList.length > 0">
            <HospitalCard
              v-for="hospital in hospitalList"
              :key="hospital.id"
              :hospital="hospital"
            />
          </template>
          <Empty v-else description="暂无医院数据" />

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="hospitalStore.filterParams.page"
              v-model:page-size="hospitalStore.filterParams.pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
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
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useHospitalStore } from '@/stores'
import { getHospitalList, filterHospitals, searchHospitals } from '@/api/hospital'
import { getAreaTree } from '@/api/area'
import { getDiseaseTree } from '@/api/disease'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()
const hospitalStore = useHospitalStore()

const loading = ref(false)
const hospitalList = ref([])
const total = ref(0)
const areaOptions = ref([])
const selectedArea = ref([])
const diseaseOptions = ref([])
const selectedDisease = ref([])

// 转换地区数据为级联选择器格式
const transformAreaData = (areas) => {
  return areas.map(area => ({
    value: area.code,  // 使用 code 而不是 id，因为医院的 area_code 存储的是 code
    label: area.name,
    children: area.children ? transformAreaData(area.children) : undefined
  }))
}

// 转换疾病数据为级联选择器格式
const transformDiseaseData = (diseases) => {
  return diseases.map(disease => ({
    id: disease.id,
    label: disease.diseaseName || disease.name,
    code: disease.diseaseCode,
    value: disease.diseaseCode,
    parentId: disease.parentId,
    level: disease.level,
    children: disease.children ? transformDiseaseData(disease.children) : undefined
  }))
}

// 加载地区数据
const loadAreas = async () => {
  try {
    const res = await getAreaTree()
    areaOptions.value = transformAreaData(res.data || [])
    console.log('Area options loaded:', areaOptions.value)
  } catch (error) {
    console.error('Failed to load areas:', error)
  }
}

// 加载疾病数据
const loadDiseases = async () => {
  try {
    const res = await getDiseaseTree()
    diseaseOptions.value = transformDiseaseData(res.data || [])
    console.log('Disease options loaded:', diseaseOptions.value)
  } catch (error) {
    console.error('Failed to load diseases:', error)
  }
}

// 判断是否有筛选条件
const hasFilterConditions = () => {
  const params = hospitalStore.filterParams
  return !!(
    params.areaCode ||
    params.level ||
    params.diseaseCode
  )
}

// 加载医院列表
const loadHospitals = async () => {
  try {
    loading.value = true
    console.log('=== Loading hospitals ===')
    console.log('filterParams:', hospitalStore.filterParams)
    console.log('apiParams:', hospitalStore.apiParams)
    console.log('Route query keyword:', route.query.keyword)
    console.log('Has filter conditions:', hasFilterConditions())

    let res
    const keyword = route.query.keyword

    // 根据不同场景调用不同的API
    if (keyword) {
      // 场景1：有搜索关键词 → 使用搜索API
      console.log('Using searchHospitals API with keyword:', keyword)
      const params = {
        page: hospitalStore.filterParams.page,
        pageSize: hospitalStore.filterParams.pageSize
      }
      res = await searchHospitals(keyword, params)
    } else if (hasFilterConditions()) {
      // 场景2：有筛选条件 → 使用筛选API
      console.log('Using filterHospitals API')
      console.log('Request body:', JSON.stringify(hospitalStore.apiParams, null, 2))
      res = await filterHospitals(hospitalStore.apiParams)
    } else {
      // 场景3：无搜索、无筛选 → 使用列表API
      console.log('Using getHospitalList API')
      res = await getHospitalList(hospitalStore.apiParams)
    }

    console.log('Response:', res)
    console.log('Response data:', res.data)
    console.log('Response list:', res.data?.list)

    // 映射字段名：hospitalName -> name
    hospitalList.value = (res.data.list || []).map((item) => ({
      id: item.id,
      name: item.hospitalName || item.name,
      level: item.hospitalLevel || item.level,
      province: item.provinceName || item.province,
      city: item.cityName || item.city,
      address: item.address,
      // 不设置avatar，让组件使用默认头像
      avatar: null
    }))
    total.value = res.data.total || 0
    console.log('Final hospitalList length:', hospitalList.value.length)
  } catch (error) {
    console.error('Failed to load hospitals:', error)
    console.error('Error response:', error.response)
  } finally {
    loading.value = false
  }
}

// 地区改变
const handleAreaChange = (value) => {
  console.log('Area changed:', value)
  const areaCode = value && value.length > 0 ? value[value.length - 1] : undefined
  hospitalStore.setArea(areaCode ? { code: areaCode, name: '' } : null)
  loadHospitals()
}

// 疾病改变
const handleDiseaseChange = (value) => {
  console.log('Disease changed:', value)
  const diseaseCode = value && value.length > 0 ? value[value.length - 1] : undefined
  hospitalStore.setDiseaseCode(diseaseCode)
  loadHospitals()
}

// 筛选条件改变
const handleFilterChange = () => {
  console.log('Filter changed, current level:', hospitalStore.selectedLevel)
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
  hospitalStore.resetFilters()
  selectedArea.value = []
  selectedDisease.value = []
  loadHospitals()
}

// 监听路由参数（搜索关键词）
watch(
  () => route.query.keyword,
  (keyword) => {
    // keyword变化时重新加载医院列表
    loadHospitals()
  },
  { immediate: true }
)

onMounted(() => {
  loadAreas()
  loadDiseases()
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
