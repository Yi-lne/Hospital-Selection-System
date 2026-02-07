<template>
  <div class="doctor-by-department-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">{{ deptName }} - 医生列表</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="doctor-list-card">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select
          v-model="filterParams.title"
          placeholder="职称筛选"
          clearable
          @change="handleFilterChange"
        >
          <el-option label="主任医师" value="主任医师" />
          <el-option label="副主任医师" value="副主任医师" />
          <el-option label="主治医师" value="主治医师" />
          <el-option label="住院医师" value="住院医师" />
        </el-select>
        <el-select
          v-model="filterParams.sortBy"
          placeholder="排序方式"
          @change="handleFilterChange"
        >
          <el-option label="评分最高" value="rating" />
          <el-option label="评价最多" value="reviewCount" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getDepartmentDoctors } from '@/api/doctor'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()

const loading = ref(false)
const doctorList = ref([])
const total = ref(0)
const deptId = ref(null)
const deptName = ref('')

const filterParams = reactive({
  page: 1,
  pageSize: 12,
  title: undefined,
  sortBy: undefined
})

// 加载医生列表
const loadDoctors = async () => {
  if (!deptId.value) return

  try {
    loading.value = true
    const params = {
      deptId: deptId.value,
      page: filterParams.page,
      pageSize: filterParams.pageSize
    }
    if (filterParams.title) {
      params.title = filterParams.title
    }
    if (filterParams.sortBy) {
      params.sortBy = filterParams.sortBy
    }
    const res = await getDepartmentDoctors(params)
    doctorList.value = res.data.list || []
    total.value = res.data.total || 0
    deptName.value = res.data.deptName || '科室'
  } catch (error) {
    console.error('加载医生列表失败:', error)
  } finally {
    loading.value = false
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
  filterParams.title = undefined
  filterParams.sortBy = undefined
  filterParams.page = 1
  loadDoctors()
}

onMounted(() => {
  deptId.value = Number(route.params.deptId)
  if (route.query.deptName) {
    deptName.value = route.query.deptName
  }
  loadDoctors()
})
</script>

<style scoped lang="scss">
.doctor-by-department-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .doctor-list-card {
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
      width: 150px;
    }
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
