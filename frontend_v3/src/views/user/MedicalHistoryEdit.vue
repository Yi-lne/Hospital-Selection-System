<template>
  <div class="medical-history-edit-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">{{ isEdit ? '编辑病史' : '添加病史' }}</span>
      </template>
    </el-page-header>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        class="history-form"
      >
        <el-form-item label="疾病名称" prop="diseaseName">
          <el-input
            v-model="formData.diseaseName"
            placeholder="请输入疾病名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="诊断日期" prop="diagnosisDate">
          <el-date-picker
            v-model="formData.diagnosisDate"
            type="date"
            placeholder="选择诊断日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="治疗状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">治疗中</el-radio>
            <el-radio :value="2">已康复</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注说明">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注说明（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '添加病史' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addMedicalHistory, updateMedicalHistory, getMedicalHistoryDetail } from '@/api/medical-history'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const isEdit = ref(false)
const historyId = ref(null)

const formData = reactive({
  diseaseName: '',
  diagnosisDate: '',
  status: 1,
  remark: ''
})

const rules = {
  diseaseName: [
    { required: true, message: '请输入疾病名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  diagnosisDate: [
    { required: true, message: '请选择诊断日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择治疗状态', trigger: 'change' }
  ]
}

// 禁用未来日期
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        if (isEdit.value) {
          await updateMedicalHistory(historyId.value, formData)
          ElMessage.success('修改成功')
        } else {
          await addMedicalHistory(formData)
          ElMessage.success('添加成功')
        }
        router.push('/user/medical-history')
      } catch (error) {
        console.error('Failed to submit form:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 取消操作
const handleCancel = () => {
  router.back()
}

// 加载病史详情
const loadHistoryDetail = async () => {
  if (!historyId.value) return

  try {
    const res = await getMedicalHistoryDetail(historyId.value)
    Object.assign(formData, res.data)
  } catch (error) {
    console.error('Failed to load history detail:', error)
  }
}

onMounted(() => {
  if (route.params.id) {
    isEdit.value = true
    historyId.value = Number(route.params.id)
    loadHistoryDetail()
  }
})
</script>

<style scoped lang="scss">
.medical-history-edit-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .form-card {
    margin-top: 20px;
    max-width: 800px;
  }

  .history-form {
    margin-top: 20px;
  }
}
</style>
