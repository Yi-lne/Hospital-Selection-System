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
              :props="{
                expandTrigger: 'hover',
                label: 'label',
                value: 'value',
                children: 'children',
                checkStrictly: true
              }"
              placeholder="请选择地区"
              clearable
              style="width: 100%"
              @change="handleAreaChange"
            />
          </div>

          <!-- 科室筛选 -->
          <div class="filter-section">
            <h4>科室筛选</h4>
            <el-select
              v-model="selectedDepartment"
              placeholder="请选择科室"
              filterable
              allow-create
              clearable
              style="width: 100%"
              @change="handleDepartmentChange"
            >
              <el-option
                v-for="dept in departmentOptions"
                :key="dept.code"
                :label="dept.label"
                :value="dept.label"
              />
            </el-select>
          </div>

          <!-- 医院级别 -->
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
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useHospitalStore } from '@/stores'
import { getHospitalList, filterHospitals, searchHospitals } from '@/api/hospital'
import { getAreaTree } from '@/api/area'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()
const hospitalStore = useHospitalStore()

const loading = ref(false)
const hospitalList = ref([])
const total = ref(0)
const areaOptions = ref([])
const selectedArea = ref([])

// ✅ 科室筛选相关
const selectedDepartment = ref('')
const departmentOptions = ref([
  { code: 'cardiology', label: '心血管内科' },
  { code: 'respiratory', label: '呼吸内科' },
  { code: 'gastroenterology', label: '消化内科' },
  { code: 'neurology', label: '神经内科' },
  { code: 'orthopedics', label: '骨科' },
  { code: 'oncology', label: '肿瘤科' },
  { code: 'pediatrics', label: '儿科' },
  { code: 'gynecology', label: '妇产科' },
  { code: 'ophthalmology', label: '眼科' },
  { code: 'ent', label: '耳鼻喉科' },
  { code: 'dermatology', label: '皮肤科' },
  { code: 'urology', label: '泌尿外科' },
  { code: 'endocrinology', label: '内分泌科' },
  { code: 'nephrology', label: '肾内科' },
  { code: 'rheumatology', label: '风湿免疫科' },
  { code: 'hematology', label: '血液科' }
])

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
  } catch (error) {
    console.error('加载地区列表失败:', error)
  }
}

// ✅ 科室选择变化
const handleDepartmentChange = async (value) => {
  if (value) {
    // 有值：设置科室筛选
    hospitalStore.setDepartmentCode(value)
    hospitalStore.filterParams.page = 1
  } else {
    // 无值：清除科室筛选
    hospitalStore.setDepartmentCode(undefined)
  }

  // 等待状态更新完成
  await nextTick()

  loadHospitals()
}

// 判断是否有筛选条件
const hasFilterConditions = () => {
  const params = hospitalStore.filterParams
  const hasCondition = !!(
    params.provinceCode ||
    params.cityCode ||
    params.areaCode ||
    params.level ||
    params.departmentCode
  )
  return hasCondition
}

const loadHospitals = async () => {
  try {
    loading.value = true

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
      // 不设置avatar，让组件使用默认头像
      avatar: null
    }))
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载医院列表失败:', error)
    console.error('错误响应:', error.response)
  } finally {
    loading.value = false
  }
}

// 地区改变（支持多级筛选：省/省+市/省+市+区）
const handleAreaChange = (value) => {
  if (value && value.length > 0) {
    const level = value.length

    if (level === 1) {
      // 只选择了省份
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: undefined,
        areaCode: undefined,
        page: 1  // 重置到第一页
      })
    } else if (level === 2) {
      // 选择了省份+城市
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: value[1],
        areaCode: undefined,
        page: 1  // 重置到第一页
      })
    } else if (level === 3) {
      // 选择了省份+城市+区县
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: value[1],
        areaCode: value[2],
        page: 1  // 重置到第一页
      })
    }
  } else {
    // 清空地区选择
    hospitalStore.setFilterParams({
      provinceCode: undefined,
      cityCode: undefined,
      areaCode: undefined,
      page: 1  // 重置到第一页
    })
  }

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
  hospitalStore.resetFilters()
  selectedArea.value = []
  selectedDepartment.value = ''
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
  // 先加载地区数据
  loadAreas().then(() => {
    // 地区数据加载完成后，从 store 恢复筛选状态
    const filterParams = hospitalStore.filterParams

    // 恢复地区选择
    if (filterParams.provinceCode || filterParams.cityCode || filterParams.areaCode) {
      const areaCodes = []
      if (filterParams.provinceCode) areaCodes.push(filterParams.provinceCode)
      if (filterParams.cityCode) areaCodes.push(filterParams.cityCode)
      if (filterParams.areaCode) areaCodes.push(filterParams.areaCode)
      selectedArea.value = areaCodes
    }

    // 恢复科室选择
    if (filterParams.departmentCode) {
      selectedDepartment.value = filterParams.departmentCode
    }
  })

  // 加载医院列表
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
