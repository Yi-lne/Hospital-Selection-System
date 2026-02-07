<template>
  <div class="doctor-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医生列表</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="list-card">
      <!-- 筛选条件 -->
      <div class="filter-bar">
        <!-- 地区选择 -->
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
          placeholder="选择地区"
          clearable
          style="width: 220px"
          @change="handleAreaChange"
        />

        <!-- 医院选择 -->
        <el-select
          v-model="filterParams.hospitalId"
          placeholder="选择医院"
          clearable
          style="width: 220px"
          @change="handleHospitalChange"
        >
          <el-option
            v-for="hospital in hospitals"
            :key="hospital.id"
            :label="hospital.hospitalName"
            :value="hospital.id"
          />
        </el-select>

        <!-- 科室选择 -->
        <el-select
          v-model="filterParams.deptId"
          placeholder="选择科室"
          clearable
          style="width: 180px"
          :disabled="!filterParams.hospitalId"
          @change="handleFilterChange"
        >
          <el-option
            v-for="dept in departments"
            :key="dept.id"
            :label="dept.deptName"
            :value="dept.id"
          />
        </el-select>

        <!-- 职称选择 -->
        <el-select
          v-model="filterParams.title"
          placeholder="选择职称"
          clearable
          style="width: 180px"
          @change="handleFilterChange"
        >
          <el-option label="主任医师" value="主任医师" />
          <el-option label="副主任医师" value="副主任医师" />
          <el-option label="主治医师" value="主治医师" />
          <el-option label="住院医师" value="住院医师" />
        </el-select>

        <el-button @click="resetFilters">重置</el-button>
      </div>

      <!-- 医生列表 -->
      <template v-if="doctorList.length > 0">
        <el-row :gutter="16">
          <el-col v-for="doctor in doctorList" :key="doctor.id" :xs="24" :sm="12" :md="8" :lg="6">
            <DoctorCard :doctor="doctor" />
          </el-col>
        </el-row>
      </template>
      <Empty v-else description="暂无医生数据" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="filterParams.page"
          v-model:page-size="filterParams.pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { filterDoctors } from '@/api/doctor'
import { getHospitalList } from '@/api/hospital'
import { getHospitalDepartments } from '@/api/department'
import { getAreaTree } from '@/api/area'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()

const loading = ref(false)
const doctorList = ref([])
const total = ref(0)
const hospitals = ref([])
const departments = ref([])
const areaOptions = ref([])
const selectedArea = ref([])

const filterParams = reactive({
  page: 1,
  pageSize: 12,
  provinceCode: undefined,
  cityCode: undefined,
  areaCode: undefined,
  hospitalId: undefined,
  deptId: undefined,
  title: undefined
})

// 加载医生列表
const loadDoctors = async () => {
  try {
    loading.value = true
    // 使用筛选接口而不是普通列表接口
    const res = await filterDoctors(filterParams)
    doctorList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载医生列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载医院列表（用于筛选）
const loadHospitals = async () => {
  try {
    const res = await getHospitalList({ page: 1, pageSize: 1000 })
    // 映射字段名
    hospitals.value = (res.data.list || []).map(item => ({
      id: item.id,
      hospitalName: item.hospitalName || item.name
    }))
  } catch (error) {
    console.error('加载医院列表失败:', error)
  }
}

// 转换地区数据为级联选择器格式
const transformAreaData = (areas) => {
  return areas.map(area => ({
    value: area.code,
    label: area.name,
    children: area.children ? transformAreaData(area.children) : undefined
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

// 地区改变（支持多级筛选：省/省+市/省+市+区）
const handleAreaChange = (value) => {
  if (value && value.length > 0) {
    const level = value.length

    if (level === 1) {
      // 只选择了省份
      filterParams.provinceCode = value[0]
      filterParams.cityCode = undefined
      filterParams.areaCode = undefined
    } else if (level === 2) {
      // 选择了省份+城市
      filterParams.provinceCode = value[0]
      filterParams.cityCode = value[1]
      filterParams.areaCode = undefined
    } else if (level === 3) {
      // 选择了省份+城市+区县
      filterParams.provinceCode = value[0]
      filterParams.cityCode = value[1]
      filterParams.areaCode = value[2]
    }
  } else {
    // 清空地区选择
    filterParams.provinceCode = undefined
    filterParams.cityCode = undefined
    filterParams.areaCode = undefined
  }

  filterParams.page = 1

  // 清空医院和科室选择（因为地区变了）
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  departments.value = []

  // 重新加载医生列表
  loadDoctors()
}

// 加载选中医院的科室列表
const loadDepartments = async (hospitalId) => {
  if (!hospitalId) {
    departments.value = []
    filterParams.deptId = undefined
    return
  }

  try {
    const res = await getHospitalDepartments(hospitalId)
    departments.value = res.data || []

    // 重置科室选择
    filterParams.deptId = undefined
  } catch (error) {
    console.error('加载科室列表失败:', error)
    departments.value = []
  }
}

// 医院筛选改变
const handleHospitalChange = (hospitalId) => {
  filterParams.hospitalId = hospitalId
  filterParams.page = 1

  // 加载该医院的科室
  loadDepartments(hospitalId)

  // 重新加载医生列表
  loadDoctors()
}

// 其他筛选改变
const handleFilterChange = () => {
  filterParams.page = 1
  loadDoctors()
}

// 分页改变
const handlePageChange = (page) => {
  filterParams.page = page
  loadDoctors()
}

const handleSizeChange = (size) => {
  filterParams.page = 1
  filterParams.pageSize = size
  loadDoctors()
}

// 重置筛选
const resetFilters = () => {
  filterParams.provinceCode = undefined
  filterParams.cityCode = undefined
  filterParams.areaCode = undefined
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  selectedArea.value = []
  departments.value = []
  loadDoctors()
}

// 监听路由参数
watch(
  () => route.query.hospitalId,
  (hospitalId) => {
    if (hospitalId) {
      filterParams.hospitalId = Number(hospitalId)
      // 加载该医院的科室
      loadDepartments(Number(hospitalId))
      loadDoctors()
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadAreas()
  loadHospitals()
  if (!route.query.hospitalId) {
    loadDoctors()
  }
})
</script>

<style scoped lang="scss">
.doctor-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .list-card {
    margin-top: 20px;
    min-height: 500px;
  }

  .filter-bar {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 24px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;

    .el-select {
      width: 180px;
    }
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
