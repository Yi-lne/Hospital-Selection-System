<template>
  <div class="doctor-manage-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医生管理</span>
      </template>
    </el-page-header>

    <el-card>
      <el-table :data="doctorList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="hospitalName" label="所属医院" width="180" />
        <el-table-column prop="deptName" label="科室" width="120" />
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="specialty" label="专业特长" min-width="150" />
        <el-table-column prop="consultationFee" label="挂号费" width="100">
          <template #default="{ row }">
            ¥{{ row.consultationFee || '0' }}
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            {{ row.rating || '0' }}分
          </template>
        </el-table-column>
        <el-table-column prop="reviewCount" label="评价数" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHospitalDoctors } from '@/api/doctor'

const loading = ref(false)
const doctorList = ref([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadDoctors = async () => {
  // TODO: 实现医生列表加载
  loading.value = true
  try {
    // 临时数据
    doctorList.value = []
    total.value = 0
  } catch (error) {
    console.error('Failed to load doctors:', error)
  } finally {
    loading.value = false
  }
}

const handleEdit = (doctor) => {
  ElMessage.info('编辑功能待实现')
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该医生吗？', '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadDoctors()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete doctor:', error)
    }
  }
}

const handlePageChange = (newPage) => {
  page.value = newPage
  loadDoctors()
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  page.value = 1
  loadDoctors()
}

onMounted(() => {
  loadDoctors()
})
</script>

<style scoped lang="scss">
.doctor-manage-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
