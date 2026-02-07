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
        <el-table-column prop="diseaseName" label="疾病名称" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="visitDate" label="就诊日期" width="120">
          <template #default="{ row }">
            {{ row.visitDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="hospitalName" label="就诊医院" width="180" />
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openDialog(row)">
              编辑
            </el-button>
            <el-popconfirm title="确定删除这条病史吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" size="small">删除</el-button>
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

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入病情描述"
          />
        </el-form-item>

        <el-form-item label="就诊日期" prop="visitDate">
          <el-date-picker
            v-model="formData.visitDate"
            type="date"
            placeholder="请选择就诊日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
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

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import {
  getMedicalHistoryList,
  addMedicalHistory,
  updateMedicalHistory,
  deleteMedicalHistory
} from '@/api/medical-history'
import type { MedicalHistory } from '@/types/other'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const historyList = ref<MedicalHistory[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const currentId = ref<number | null>(null)

const formData = reactive({
  diseaseName: '',
  description: '',
  visitDate: '',
  hospitalName: '',
  doctorName: ''
})

const rules: FormRules = {
  diseaseName: [
    { required: true, message: '请输入疾病名称', trigger: 'blur' }
  ]
}

// 加载病史列表
const loadHistory = async () => {
  try {
    loading.value = true
    const res = await getMedicalHistoryList(page.value, pageSize.value)
    historyList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载病史列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开对话框
const openDialog = (row?: MedicalHistory) => {
  if (row) {
    isEdit.value = true
    currentId.value = row.id
    Object.assign(formData, {
      diseaseName: row.diseaseName,
      description: row.description || '',
      visitDate: row.visitDate || '',
      hospitalName: row.hospitalName || '',
      doctorName: row.doctorName || ''
    })
  } else {
    isEdit.value = false
    currentId.value = null
    formRef.value?.resetFields()
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
const handleDelete = async (id: number) => {
  try {
    await deleteMedicalHistory(id)
    ElMessage.success('删除成功')
    loadHistory()
  } catch (error) {
    console.error('删除病史失败:', error)
  }
}

// 分页大小改变
const handleSizeChange = (size: number) => {
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
