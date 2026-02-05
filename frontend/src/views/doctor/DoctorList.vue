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
        <el-select
          v-model="filterParams.hospitalId"
          placeholder="选择医院"
          clearable
          @change="handleFilterChange"
        >
          <el-option
            v-for="hospital in hospitals"
            :key="hospital.id"
            :label="hospital.hospitalName"
            :value="hospital.id"
          />
        </el-select>

        <el-select
          v-model="filterParams.deptId"
          placeholder="选择科室"
          clearable
          @change="handleFilterChange"
        >
          <el-option
            v-for="dept in departments"
            :key="dept.id"
            :label="dept.deptName"
            :value="dept.id"
          />
        </el-select>

        <el-select
          v-model="filterParams.title"
          placeholder="选择职称"
          clearable
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
import { getDoctorList } from '@/api/doctor'
import { getHospitalList } from '@/api/hospital'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()

const loading = ref(false)
const doctorList = ref([])
const total = ref(0)
const hospitals = ref([])
const departments = ref([])

const filterParams = reactive({
  page: 1,
  pageSize: 12,
  hospitalId: undefined,
  deptId: undefined,
  title: undefined,
  name: undefined
})

// 加载医生列表
const loadDoctors = async () => {
  try {
    loading.value = true
    const res = await getDoctorList(filterParams)
    doctorList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('Failed to load doctors:', error)
  } finally {
    loading.value = false
  }
}

// 加载医院列表（用于筛选）
const loadHospitals = async () => {
  try {
    const res = await getHospitalList({ page: 1, pageSize: 1000 })
    hospitals.value = res.data.list
  } catch (error) {
    console.error('Failed to load hospitals:', error)
  }
}

// 筛选改变
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
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  loadDoctors()
}

// 监听路由参数
watch(
  () => route.query.hospitalId,
  (hospitalId) => {
    if (hospitalId) {
      filterParams.hospitalId = Number(hospitalId)
      loadDoctors()
    }
  },
  { immediate: true }
)

onMounted(() => {
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
