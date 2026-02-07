<template>
  <div class="admin-doctor-manage">
    <div class="header">
      <div class="breadcrumb">
        <el-icon @click="goBack" style="cursor: pointer; margin-right: 8px">
          <ArrowLeft />
        </el-icon>
        <span>{{ hospitalName }}</span>
        <span style="margin: 0 8px">/</span>
        <span>医生管理</span>
      </div>
      <div class="actions">
        <el-select
          v-model="filterDeptId"
          placeholder="全部科室"
          clearable
          style="width: 180px; margin-right: 10px"
          @change="loadDoctors"
        >
          <el-option
            v-for="dept in departments"
            :key="dept.id"
            :label="dept.deptName"
            :value="dept.id"
          />
        </el-select>
        <el-button type="primary" @click="showAddDialog">新增医生</el-button>
      </div>
    </div>

    <el-table :data="doctors" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="doctorName" label="姓名" min-width="120" />
      <el-table-column prop="deptName" label="科室" width="150" />
      <el-table-column prop="title" label="职称" width="120">
        <template #default="{ row }">
          <el-tag>{{ row.title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="specialty" label="专业特长" min-width="200" />
      <el-table-column prop="consultationFee" label="挂号费" width="100" align="center">
        <template #default="{ row }">
          ¥{{ row.consultationFee }}
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="120" align="center">
        <template #default="{ row }">
          <el-rate v-model="row.rating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="reviewCount" label="评价数" width="100" align="center" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="editDoctor(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="deleteDoctor(row)">
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
      @size-change="loadDoctors"
      @current-change="loadDoctors"
      style="margin-top: 20px; justify-content: center"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑医生' : '新增医生'"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="doctorName">
          <el-input v-model="form.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>

        <el-form-item label="所属科室" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择科室" style="width: 100%">
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.deptName"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="住院医师" value="住院医师" />
            <el-option label="医师" value="医师" />
          </el-select>
        </el-form-item>

        <el-form-item label="专业特长" prop="specialty">
          <el-input
            v-model="form.specialty"
            type="textarea"
            :rows="3"
            placeholder="请输入专业特长"
          />
        </el-form-item>

        <el-form-item label="学历背景" prop="academicBackground">
          <el-input
            v-model="form.academicBackground"
            placeholder="请输入学历背景"
          />
        </el-form-item>

        <el-form-item label="出诊时间" prop="scheduleTime">
          <el-input
            v-model="form.scheduleTime"
            placeholder="例如：周一上午、周三下午"
          />
        </el-form-item>

        <el-form-item label="挂号费" prop="consultationFee">
          <el-input-number
            v-model="form.consultationFee"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="初始评分" prop="rating">
          <el-rate v-model="form.rating" allow-half />
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
const doctors = ref([])
const departments = ref([])
const loading = ref(false)
const filterDeptId = ref(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editingDoctorId = ref(null)
const formRef = ref(null)

const form = ref({
  doctorName: '',
  hospitalId: hospitalId.value,
  deptId: null,
  title: '',
  specialty: '',
  academicBackground: '',
  scheduleTime: '',
  consultationFee: 50,
  rating: 0
})

const rules = {
  doctorName: [
    { required: true, message: '请输入医生姓名', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择科室', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  consultationFee: [
    { required: true, message: '请输入挂号费', trigger: 'blur' }
  ]
}

// 返回医院列表
const goBack = () => {
  router.push({ name: 'AdminHospitalList' })
}

// 加载科室列表
const loadDepartments = async () => {
  try {
    const res = await getHospitalDepartments(hospitalId.value)
    departments.value = res.data || []
  } catch (error) {
    console.error('加载科室列表失败:', error)
  }
}

// 加载医生列表
const loadDoctors = async () => {
  try {
    loading.value = true
    const res = await getHospitalDoctors(hospitalId.value)
    let allDoctors = res.data || []

    // 按科室筛选
    if (filterDeptId.value) {
      allDoctors = allDoctors.filter(d => d.deptId === filterDeptId.value)
    }

    // 前端分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    doctors.value = allDoctors.slice(start, end)
    total.value = allDoctors.length
  } catch (error) {
    ElMessage.error('加载医生列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  editingDoctorId.value = null
  form.value = {
    doctorName: '',
    hospitalId: hospitalId.value,
    deptId: null,
    title: '',
    specialty: '',
    academicBackground: '',
    scheduleTime: '',
    consultationFee: 50,
    rating: 0
  }
  dialogVisible.value = true
}

// 编辑医生
const editDoctor = (doctor) => {
  isEdit.value = true
  editingDoctorId.value = doctor.id
  form.value = {
    doctorName: doctor.doctorName,
    hospitalId: hospitalId.value,
    deptId: doctor.deptId,
    title: doctor.title,
    specialty: doctor.specialty || '',
    academicBackground: doctor.academicBackground || '',
    scheduleTime: doctor.scheduleTime || '',
    consultationFee: doctor.consultationFee,
    rating: doctor.rating || 0
  }
  dialogVisible.value = true
}

// 删除医生
const deleteDoctor = async (doctor) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除医生"${doctor.doctorName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await adminApi.deleteDoctor(doctor.id)
    ElMessage.success('删除成功')
    loadDoctors()
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

    if (isEdit.value) {
      await adminApi.updateDoctor(editingDoctorId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createDoctor(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadDoctors()
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
  loadDoctors()
})
</script>

<style scoped>
.admin-doctor-manage {
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

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
