<template>
  <div class="admin-department-manage">
    <div class="header">
      <div class="breadcrumb">
        <el-icon @click="goBack" style="cursor: pointer; margin-right: 8px">
          <ArrowLeft />
        </el-icon>
        <span>{{ hospitalName }}</span>
        <span style="margin: 0 8px">/</span>
        <span>科室管理</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar actions">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索科室名称"
        style="width: 250px"
        @keyup.enter="searchDepartments"
        @clear="clearSearch"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="searchDepartments">搜索</el-button>
      <el-button v-if="hasSearch" @click="clearSearch">清除搜索</el-button>
      <el-button type="success" @click="showAddDialog">
        新增科室
      </el-button>
      <el-checkbox v-model="includeDeleted" @change="loadDepartments">
        显示已删除
      </el-checkbox>
    </div>

    <el-table :data="departments" v-loading="loading" border stripe>
      <el-table-column prop="id" label="编号" width="80" />
      <el-table-column prop="deptName" label="科室名称" min-width="200" />
      <el-table-column prop="deptIntro" label="科室简介" min-width="300" />
      <el-table-column label="医生数量" width="120" align="center">
        <template #default="{ row }">
          <el-tag type="info">{{ getDoctorCount(row.id) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isDeleted" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isDeleted ? 'danger' : 'success'">
            {{ row.isDeleted ? '已删除' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="editDepartment(row)"
            :disabled="row.isDeleted"
          >
            编辑
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="deleteDepartment(row)"
            :disabled="row.isDeleted"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑科室' : '新增科室'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="科室名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室简介" prop="deptIntro">
          <el-input
            v-model="form.deptIntro"
            type="textarea"
            :rows="5"
            placeholder="请输入科室简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Search } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import { getHospitalDepartments } from '@/api/hospital'
import { getHospitalDoctors } from '@/api/doctor'

const router = useRouter()
const route = useRoute()

const hospitalId = ref(Number(route.params.hospitalId))
const hospitalName = ref(route.query.hospitalName || '医院')
const departments = ref([])
const doctorCounts = ref({})
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editingDeptId = ref(null)
const formRef = ref(null)
const searchKeyword = ref('')
const includeDeleted = ref(false)

// 是否有搜索关键词
const hasSearch = computed(() => searchKeyword.value.trim() !== '')

const form = ref({
  deptName: '',
  deptIntro: ''
})

const rules = {
  deptName: [
    { required: true, message: '请输入科室名称', trigger: 'blur' }
  ]
}

// 返回医院列表
const goBack = () => {
  router.push({ name: 'AdminHospital' })
}

// 获取医生数量
const getDoctorCount = (deptId) => {
  return doctorCounts.value[deptId] || 0
}

// 加载科室列表
const loadDepartments = async () => {
  try {
    loading.value = true
    const res = await adminApi.getHospitalDepartments(hospitalId.value, {
      includeDeleted: includeDeleted.value
    })
    departments.value = res.data || []

    // 先获取所有医生（只调用一次API）
    let allDoctors = []
    try {
      const doctorRes = await getHospitalDoctors(hospitalId.value, {
        page: 1,
        pageSize: 1000,
        includeDeleted: includeDeleted.value
      })
      // 处理分页响应或数组响应
      if (Array.isArray(doctorRes.data)) {
        allDoctors = doctorRes.data
      } else if (doctorRes.data?.list) {
        allDoctors = doctorRes.data.list
      } else {
        allDoctors = []
      }
    } catch (error) {
      console.error('获取医生列表失败:', error)
    }

    // 通过科室名称匹配医生数量（解决ID不匹配的问题）
    for (const dept of departments.value) {
      const doctorsInDept = allDoctors.filter(d => d.deptName === dept.deptName)
      doctorCounts.value[dept.id] = doctorsInDept.length
    }
  } catch (error) {
    ElMessage.error('加载科室列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索科室
const searchDepartments = () => {
  if (!searchKeyword.value.trim()) {
    loadDepartments()
    return
  }

  const keyword = searchKeyword.value.trim().toLowerCase()
  const filtered = departments.value.filter(dept =>
    dept.deptName.toLowerCase().includes(keyword)
  )

  if (filtered.length === 0) {
    ElMessage.info('未找到匹配的科室')
  } else {
    departments.value = filtered
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  loadDepartments()
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  editingDeptId.value = null
  form.value = {
    deptName: '',
    deptIntro: ''
  }
  dialogVisible.value = true
}

// 编辑科室
const editDepartment = (dept) => {
  isEdit.value = true
  editingDeptId.value = dept.id
  form.value = {
    deptName: dept.deptName,
    deptIntro: dept.deptIntro || ''
  }
  dialogVisible.value = true
}

// 删除科室
const deleteDepartment = async (dept) => {
  try {
    const doctorCount = getDoctorCount(dept.id)
    await ElMessageBox.confirm(
      doctorCount > 0
        ? `该科室下有 ${doctorCount} 位医生，删除科室将同时删除这些医生。确定要删除科室"${dept.deptName}"吗？`
        : `确定要删除科室"${dept.deptName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteDepartment(dept.id)
    ElMessage.success('删除成功')
    loadDepartments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    const data = {
      hospitalId: hospitalId.value,
      ...form.value
    }

    if (isEdit.value) {
      await adminApi.updateDepartment(editingDeptId.value, data)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createDepartment(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadDepartments()
  } catch (error) {
    if (error.message) {
      ElMessage.error((isEdit.value ? '更新' : '创建') + '失败：' + error.message)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadDepartments()
})
</script>

<style scoped>
.admin-department-manage {
  padding: 20px;
}

.header {
  margin-bottom: 12px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.search-bar {
  margin-bottom: 12px;
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
