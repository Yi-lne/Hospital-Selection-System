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
      <el-button type="primary" @click="showAddDialog">新增科室</el-button>
    </div>

    <el-table :data="departments" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="deptName" label="科室名称" min-width="200" />
      <el-table-column prop="deptIntro" label="科室简介" min-width="300" />
      <el-table-column label="医生数量" width="120" align="center">
        <template #default="{ row }">
          <el-tag type="info">{{ getDoctorCount(row.id) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="editDepartment(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="deleteDepartment(row)">
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
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
  router.push({ name: 'AdminHospitalList' })
}

// 获取医生数量
const getDoctorCount = (deptId) => {
  return doctorCounts.value[deptId] || 0
}

// 加载科室列表
const loadDepartments = async () => {
  try {
    loading.value = true
    const res = await getHospitalDepartments(hospitalId.value)
    departments.value = res.data || []
    // 加载每个科室的医生数量
    for (const dept of departments.value) {
      try {
        const doctorRes = await getHospitalDoctors(hospitalId.value)
        const doctorsInDept = doctorRes.data?.filter(d => d.deptId === dept.id) || []
        doctorCounts.value[dept.id] = doctorsInDept.length
      } catch (error) {
        doctorCounts.value[dept.id] = 0
      }
    }
  } catch (error) {
    ElMessage.error('加载科室列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}
</style>
