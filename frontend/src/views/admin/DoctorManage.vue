<template>
  <div class="doctor-manage-page">
    <div v-if="hospitalId" class="header">
      <div class="breadcrumb">
        <el-icon @click="goBack" style="cursor: pointer; margin-right: 8px">
          <ArrowLeft />
        </el-icon>
        <span>{{ hospitalName }}</span>
        <span style="margin: 0 8px">/</span>
        <span>医生管理</span>
      </div>
    </div>
    <div v-else class="header">
      <div class="breadcrumb">
        <el-icon @click="$router.back()" style="cursor: pointer; margin-right: 8px">
          <ArrowLeft />
        </el-icon>
        <span>医生管理</span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar actions">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索医生名称"
        style="width: 250px"
        @keyup.enter="searchDoctors"
        @clear="clearSearch"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="searchDoctors">搜索</el-button>
      <el-button v-if="hasSearch" @click="clearSearch">清除搜索</el-button>
      <el-button type="success" @click="showAddDialog">
        新增医生
      </el-button>
      <el-checkbox v-model="includeDeleted" @change="loadDoctors">
        显示已删除
      </el-checkbox>
    </div>

    <el-card>
      <el-table :data="doctorList" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="80" />
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
              @click="handleEdit(row)"
              :disabled="row.isDeleted"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row.id)"
              :disabled="row.isDeleted"
            >
              删除
            </el-button>
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

    <!-- 编辑医生对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="isEdit ? '编辑医生' : '新增医生'"
      width="700px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
        v-loading="editLoading"
      >
        <el-form-item label="医生姓名" prop="doctorName">
          <el-input v-model="editForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>

        <el-form-item label="所属医院" prop="hospitalId">
          <el-select
            v-model="editForm.hospitalId"
            placeholder="请选择医院"
            filterable
            @change="handleHospitalChange"
            style="width: 100%"
          >
            <el-option
              v-for="hospital in hospitalList"
              :key="hospital.id"
              :label="hospital.hospitalName"
              :value="hospital.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="科室" prop="deptId">
          <el-select
            v-model="editForm.deptId"
            placeholder="请先选择医院"
            filterable
            :disabled="!editForm.hospitalId"
            style="width: 100%"
          >
            <el-option
              v-for="dept in departmentList"
              :key="dept.id"
              :label="dept.deptName"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="职称" prop="title">
          <el-select v-model="editForm.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="医师" value="医师" />
            <el-option label="住院医师" value="住院医师" />
          </el-select>
        </el-form-item>

        <el-form-item label="专业特长" prop="specialty">
          <el-input
            v-model="editForm.specialty"
            type="textarea"
            :rows="3"
            placeholder="请输入专业特长"
          />
        </el-form-item>

        <el-form-item label="学术背景" prop="academicBackground">
          <el-input
            v-model="editForm.academicBackground"
            type="textarea"
            :rows="2"
            placeholder="请输入学术背景"
          />
        </el-form-item>

        <el-form-item label="坐诊时间" prop="scheduleTime">
          <el-input
            v-model="editForm.scheduleTime"
            placeholder="例如：周一上午、周三下午"
          />
        </el-form-item>

        <el-form-item label="挂号费" prop="consultationFee">
          <el-input-number
            v-model="editForm.consultationFee"
            :min="0"
            :precision="2"
            :step="10"
            placeholder="请输入挂号费"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="初始评分" prop="rating">
          <el-rate v-model="editForm.rating" allow-half />
          <span style="margin-left: 10px; color: #999">可选，默认为0</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="saving">
          保存
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
import { getHospitalDoctors, getDoctorDetail, searchDoctors as searchDoctorsApi } from '@/api/doctor'
import { adminApi } from '@/api/admin'

const router = useRouter()
const route = useRoute()

const hospitalId = ref(Number(route.params.hospitalId))
const hospitalName = ref(route.query.hospitalName || '医院')
const loading = ref(false)
const doctorList = ref([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const includeDeleted = ref(false)

// 是否有搜索关键词
const hasSearch = computed(() => searchKeyword.value.trim() !== '')

// 编辑相关
const editDialogVisible = ref(false)
const editLoading = ref(false)
const saving = ref(false)
const editFormRef = ref(null)
const isEdit = ref(false)
const editingDoctorId = ref(null)
const hospitalList = ref([])
const departmentList = ref([])

const editForm = ref({
  doctorName: '',
  hospitalId: hospitalId.value || null,
  deptId: null,
  title: '',
  specialty: '',
  academicBackground: '',
  scheduleTime: '',
  consultationFee: 0,
  rating: 0
})

const editRules = {
  doctorName: [
    { required: true, message: '请输入医生姓名', trigger: 'blur' }
  ],
  hospitalId: [
    { required: true, message: '请选择医院', trigger: 'change' }
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
  router.push({ name: 'AdminHospital' })
}

const loadDoctors = async () => {
  if (!hospitalId.value) {
    // 如果没有指定医院，加载所有医生
    loading.value = true
    try {
      const res = await getDoctorList({
        page: page.value,
        pageSize: pageSize.value,
        includeDeleted: includeDeleted.value
      })
      doctorList.value = res.data?.list || []
      total.value = res.data?.total || 0
    } catch (error) {
      console.error('加载医生列表失败:', error)
      ElMessage.error('加载医生列表失败')
    } finally {
      loading.value = false
    }
  } else {
    // 加载指定医院的医生（使用分页）
    loading.value = true
    try {
      const res = await getHospitalDoctors(hospitalId.value, {
        page: page.value,
        pageSize: pageSize.value,
        includeDeleted: includeDeleted.value
      })
      // 后端如果返回分页数据结构，使用 list 和 total
      // 如果返回的是数组，使用数组的长度
      if (Array.isArray(res.data)) {
        doctorList.value = res.data
        total.value = res.data.length
      } else {
        doctorList.value = res.data?.list || []
        total.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('加载医生列表失败:', error)
      ElMessage.error('加载医生列表失败')
    } finally {
      loading.value = false
    }
  }
}

// 加载医院列表
const loadHospitalList = async () => {
  try {
    const res = await adminApi.getHospitals({
      page: 1,
      pageSize: 1000,
      includeDeleted: false
    })
    hospitalList.value = res.data?.list || []
  } catch (error) {
    console.error('加载医院列表失败:', error)
  }
}

// 加载科室列表
const loadDepartmentList = async (hospitalId) => {
  try {
    // 假设有获取医院科室的API
    const res = await adminApi.getHospitalDepartments(hospitalId)
    departmentList.value = res.data || []
  } catch (error) {
    console.error('加载科室列表失败:', error)
    departmentList.value = []
  }
}

// 医院改变时加载科室
const handleHospitalChange = async () => {
  editForm.value.deptId = null
  if (editForm.value.hospitalId) {
    await loadDepartmentList(editForm.value.hospitalId)
  }
}

// 显示新增医生对话框
const showAddDialog = async () => {
  isEdit.value = false
  editingDoctorId.value = null

  // 重置表单
  editForm.value = {
    doctorName: '',
    hospitalId: hospitalId.value || null,
    deptId: null,
    title: '',
    specialty: '',
    academicBackground: '',
    scheduleTime: '',
    consultationFee: 0,
    rating: 0
  }

  // 加载医院列表
  await loadHospitalList()

  // 如果有指定医院，加载科室列表
  if (hospitalId.value) {
    await loadDepartmentList(hospitalId.value)
  }

  editDialogVisible.value = true
}

// 点击编辑按钮
const handleEdit = async (doctor) => {
  isEdit.value = true
  editingDoctorId.value = doctor.id

  // 加载医院列表
  await loadHospitalList()

  // 加载该医生所属医院的科室列表
  await loadDepartmentList(doctor.hospitalId)

  // 填充表单
  editForm.value = {
    doctorName: doctor.doctorName,
    hospitalId: doctor.hospitalId,
    deptId: doctor.deptId,
    title: doctor.title,
    specialty: doctor.specialty || '',
    academicBackground: doctor.academicBackground || '',
    scheduleTime: doctor.scheduleTime || '',
    consultationFee: doctor.consultationFee || 0,
    rating: doctor.rating || 0
  }

  editDialogVisible.value = true
}

// 保存编辑
const handleSaveEdit = async () => {
  try {
    await editFormRef.value.validate()
    saving.value = true

    if (isEdit.value) {
      // 编辑模式
      await adminApi.updateDoctor(editingDoctorId.value, editForm.value)
      ElMessage.success('医生信息更新成功')
    } else {
      // 新增模式
      await adminApi.createDoctor(editForm.value)
      ElMessage.success('医生添加成功')
    }

    editDialogVisible.value = false
    loadDoctors()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存医生失败:', error)
      ElMessage.error(error.response?.data?.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 重置编辑表单
const resetEditForm = () => {
  editForm.value = {
    doctorName: '',
    hospitalId: hospitalId.value || null,
    deptId: null,
    title: '',
    specialty: '',
    academicBackground: '',
    scheduleTime: '',
    consultationFee: 0,
    rating: 0
  }
  editFormRef.value?.resetFields()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该医生吗？', '提示', {
      type: 'warning'
    })
    await adminApi.deleteDoctor(id)
    ElMessage.success('删除成功')
    loadDoctors()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除医生失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
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

// 搜索医生
const searchDoctors = async () => {
  if (!searchKeyword.value.trim()) {
    loadDoctors()
    return
  }

  loading.value = true
  try {
    let res

    if (hospitalId.value) {
      // 如果有指定医院，使用全局搜索，然后过滤结果
      res = await searchDoctorsApi(searchKeyword.value.trim(), {
        page: 1,
        pageSize: 1000,
        includeDeleted: includeDeleted.value
      })
      // 过滤出该医院的医生
      if (res.data?.list) {
        doctorList.value = res.data.list.filter(d => d.hospitalId === hospitalId.value)
        total.value = doctorList.value.length
      } else if (Array.isArray(res.data)) {
        doctorList.value = res.data.filter(d => d.hospitalId === hospitalId.value)
        total.value = doctorList.value.length
      } else {
        doctorList.value = []
        total.value = 0
      }
    } else {
      // 全局搜索
      res = await searchDoctorsApi(searchKeyword.value.trim(), {
        page: 1,
        pageSize: pageSize.value,
        includeDeleted: includeDeleted.value
      })

      if (res.data?.list) {
        doctorList.value = res.data.list
        total.value = res.data.total || 0
      } else if (Array.isArray(res.data)) {
        doctorList.value = res.data
        total.value = res.data.length
      } else {
        doctorList.value = []
        total.value = 0
      }
    }

    if (doctorList.value.length === 0) {
      ElMessage.info('未找到匹配的医生')
    }
  } catch (error) {
    console.error('搜索医生失败:', error)
    ElMessage.error('搜索医生失败')
  } finally {
    loading.value = false
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  page.value = 1
  loadDoctors()
}

onMounted(() => {
  loadDoctors()
})
</script>

<style scoped lang="scss">
.doctor-manage-page {
  padding: 20px;

  .header {
    margin-bottom: 20px;

    .breadcrumb {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
    }
  }

  .search-bar {
    margin-bottom: 12px;
    display: flex;
    gap: 10px;
    align-items: center;
  }

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
