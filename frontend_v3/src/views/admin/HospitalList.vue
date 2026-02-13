<template>
  <div class="admin-hospital-list">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医院管理</span>
      </template>
    </el-page-header>

    <div class="content-wrapper">
      <div class="header">
        <div class="actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索医院名称"
          style="width: 250px; margin-right: 10px"
          @keyup.enter="searchHospitals"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="searchHospitals">搜索</el-button>
        <el-button type="success" @click="showAddDialog">新增医院</el-button>
        <el-button @click="resetFilter">重置筛选</el-button>
        <el-checkbox v-model="includeDeleted" @change="loadHospitals">
          显示已删除
        </el-checkbox>
      </div>
    </div>
    </div>

    <el-card class="table-card">
      <el-table :data="hospitals" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="hospitalName" label="医院名称" min-width="200" />
      <el-table-column prop="hospitalLevel" label="级别" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.hospitalLevel" :type="getLevelTagType(row.hospitalLevel)">
            {{ formatHospitalLevel(row.hospitalLevel) }}
          </el-tag>
          <span v-else style="color: #999">未设置</span>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="rating" label="评分" width="100" align="center">
        <template #default="{ row }">
          <el-rate v-model="row.rating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="reviewCount" label="评价数" width="100" align="center" />
      <el-table-column prop="isDeleted" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isDeleted ? 'danger' : 'success'">
            {{ row.isDeleted ? '已删除' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="editHospital(row)"
            :disabled="row.isDeleted"
          >
            编辑
          </el-button>
          <el-button
            type="warning"
            size="small"
            @click="manageDepartments(row)"
            :disabled="row.isDeleted"
          >
            科室
          </el-button>
          <el-button
            type="info"
            size="small"
            @click="manageDoctors(row)"
            :disabled="row.isDeleted"
          >
            医生
          </el-button>
          <el-button
            v-if="row.isDeleted"
            type="success"
            size="small"
            @click="restoreHospital(row)"
          >
            恢复
          </el-button>
          <el-button
            v-else
            type="danger"
            size="small"
            @click="deleteHospital(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadHospitals"
      @current-change="loadHospitals"
      style="margin-top: 20px; justify-content: center"
    />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <HospitalEdit
      v-model:visible="editDialogVisible"
      :hospital-id="editingHospitalId"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import HospitalEdit from './HospitalEdit.vue'
import { adminApi } from '@/api/admin'
import { formatHospitalLevel } from '@/utils/hospital'

const router = useRouter()

const hospitals = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const includeDeleted = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const editDialogVisible = ref(false)
const editingHospitalId = ref(null)

// 获取医院等级标签类型
const getLevelTagType = (levelCode) => {
  const typeMap = {
    'grade3A': 'danger',
    'grade3B': 'warning',
    'grade3C': '',
    'grade2A': 'primary',
    'grade2B': 'info',
    'grade2C': '',
    'grade1A': 'success',
    'grade1B': '',
    'grade1C': ''
  }
  return typeMap[levelCode] || ''
}

// 加载医院列表
const loadHospitals = async () => {
  try {
    loading.value = true
    const res = await adminApi.getHospitals({
      page: currentPage.value,
      pageSize: pageSize.value,
      includeDeleted: includeDeleted.value
    })
    hospitals.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载医院列表失败:', error)
    ElMessage.error('加载医院列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索医院
const searchHospitals = async () => {
  if (!searchKeyword.value.trim()) {
    loadHospitals()
    return
  }
  try {
    loading.value = true
    const res = await adminApi.searchHospitals(searchKeyword.value)
    hospitals.value = res.data
    total.value = res.data.length
  } catch (error) {
    ElMessage.error('搜索失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  searchKeyword.value = ''
  includeDeleted.value = false
  currentPage.value = 1
  loadHospitals()
}

// 显示新增对话框
const showAddDialog = () => {
  editingHospitalId.value = null
  editDialogVisible.value = true
}

// 编辑医院
const editHospital = (hospital) => {
  editingHospitalId.value = hospital.id
  editDialogVisible.value = true
}

// 管理科室
const manageDepartments = (hospital) => {
  router.push({
    name: 'AdminDepartmentManage',
    params: { hospitalId: hospital.id },
    query: { hospitalName: hospital.hospitalName }
  })
}

// 管理医生
const manageDoctors = (hospital) => {
  router.push({
    name: 'AdminDoctorManage',
    params: { hospitalId: hospital.id },
    query: { hospitalName: hospital.hospitalName }
  })
}

// 删除医院
const deleteHospital = async (hospital) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除医院"${hospital.hospitalName}"吗？此操作将级联删除该医院下的所有科室和医生！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteHospital(hospital.id)
    ElMessage.success('删除成功')
    loadHospitals()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 恢复医院
const restoreHospital = async (hospital) => {
  try {
    await ElMessageBox.confirm(
      `确定要恢复医院"${hospital.hospitalName}"吗？此操作将同时恢复该医院下的所有科室和医生！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    await adminApi.restoreHospital(hospital.id)
    ElMessage.success('恢复成功')
    loadHospitals()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('恢复失败：' + (error.message || '未知错误'))
    }
  }
}

// 编辑成功回调
const handleEditSuccess = () => {
  editDialogVisible.value = false
  loadHospitals()
}

onMounted(() => {
  loadHospitals()
})
</script>

<style scoped>
.admin-hospital-list {
  padding: 20px;

  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-wrapper {
    margin-top: 20px;
  }

  .table-card {
    margin-bottom: 20px;
  }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
