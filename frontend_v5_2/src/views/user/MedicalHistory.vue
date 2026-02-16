<template>
  <div class="medical-history-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">病史管理</span>
      </template>
    </el-page-header>

    <el-card class="history-card">
      <template #header>
        <div class="card-header">
          <span>病史记录</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">添加病史</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="historyList" stripe>
        <el-table-column prop="diseaseName" label="疾病名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="warning">治疗中</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">已康复</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="diagnosisDate" label="就诊日期" width="120">
          <template #default="{ row }">
            {{ row.diagnosisDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="hospitalName" label="就诊医院" min-width="150" show-overflow-tooltip />
        <el-table-column prop="doctorName" label="医生" min-width="100" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除这条病史吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadHistory"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑病史' : '添加病史'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="疾病名称" prop="diseaseName">
          <el-input v-model="formData.diseaseName" placeholder="请输入疾病名称" />
        </el-form-item>

        <el-form-item label="就诊日期" prop="diagnosisDate">
          <el-date-picker
            v-model="formData.diagnosisDate"
            type="date"
            placeholder="请选择就诊日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledFutureDate"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="治疗中" :value="1" />
            <el-option label="已康复" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="就诊医院" prop="hospitalName">
          <el-input v-model="formData.hospitalName" placeholder="请输入就诊医院" />
        </el-form-item>

        <el-form-item label="医生" prop="doctorName">
          <el-input v-model="formData.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'

import { ElMessage } from 'element-plus'
import {
  getMedicalHistoryList,
  addMedicalHistory,
  updateMedicalHistory,
  deleteMedicalHistory
} from '@/api/medical-history'


const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const historyList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const currentId = ref(null)

const formData = reactive({
  diseaseName: '',
  diagnosisDate: '',
  hospitalName: '',
  doctorName: '',
  status: 1
})

const rules = {
  diseaseName: [
    { required: true, message: '请输入疾病名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 禁用未来日期
const disabledFutureDate = (time) => {
  return time.getTime() > Date.now()
}

// 加载病史列表
const loadHistory = async () => {
  try {
    loading.value = true
    const res = await getMedicalHistoryList(page.value, pageSize.value)
    historyList.value = res.data || []
    total.value = res.data?.length || 0
  } catch (error) {
    console.error('加载病史失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开对话框
const openDialog = (row) => {
  if (row) {
    // 编辑模式
    isEdit.value = true
    currentId.value = row.id
    Object.assign(formData, {
      diseaseName: row.diseaseName,
      diagnosisDate: row.diagnosisDate || '',
      hospitalName: row.hospitalName || '',
      doctorName: row.doctorName || '',
      status: row.status || 1
    })
  } else {
    // 添加模式：清空表单数据
    isEdit.value = false
    currentId.value = null
    Object.assign(formData, {
      diseaseName: '',
      diagnosisDate: '',
      hospitalName: '',
      doctorName: '',
      status: 1
    })
    formRef.value?.clearValidate()
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        if (isEdit.value && currentId.value) {
          await updateMedicalHistory(currentId.value, formData)
          ElMessage.success('更新成功')
        } else {
          await addMedicalHistory(formData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadHistory()
      } catch (error) {
        console.error('提交病史失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 删除病史
const handleDelete = async (id) => {
  try {
    await deleteMedicalHistory(id)
    ElMessage.success('删除成功')
    loadHistory()
  } catch (error) {
    console.error('删除病史失败:', error)
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  page.value = 1
  pageSize.value = size
  loadHistory()
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped lang="scss">
.medical-history-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .history-card {
    margin-top: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
