<template>
  <div class="publish-topic-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">发布话题</span>
      </template>
    </el-page-header>

    <el-card class="form-card">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入话题标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="疾病分类" prop="diseaseId">
          <el-cascader
            v-model="selectedDisease"
            :options="diseaseOptions"
            :props="{ expandTrigger: 'hover', value: 'id', label: 'name' }"
            placeholder="请选择疾病分类（可选）"
            clearable
            @change="handleDiseaseChange"
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请分享你的经验或问题..."
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            发布
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { ElMessage } from 'element-plus'
import { publishTopic } from '@/api/community'
import { getDiseaseTree } from '@/api/disease'


const router = useRouter()
const formRef = ref()
const loading = ref(false)
const diseaseOptions = ref([])
const selectedDisease = ref([])

const formData = reactive({
  title: '',
  content: '',
  diseaseId: undefined
})

const rules = {
  title: [
    { required: true, message: '请输入话题标题', trigger: 'blur' },
    { min: 5, _max: 100, message: '标题长度为5-100个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入话题内容', trigger: 'blur' },
    { min: 10, max, message: '内容长度为10-5000个字符', trigger: 'blur' }
  ]
}

// 转换疾病数据
const transformDiseaseData = (diseases[])[] => {
  return diseases.map(disease => ({
    id: disease.id, name.diseaseName || disease.name, code.diseaseCode, parentId.parentId, level.level, children.children ? transformDiseaseData(disease.children)
  }))
}

// 加载疾病分类
const loadDiseases = async () => {
  try {
    const res = await getDiseaseTree()
    diseaseOptions.value = transformDiseaseData(res.data)
  } catch (error) {
    console.error('加载疾病列表失败:', error)
  }
}

// 疾病选择改变
const handleDiseaseChange = (value[]) => {
  formData.diseaseId = value?.length > 0 ? value[value.length - 1] : undefined
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        await publishTopic(formData)
        ElMessage.success('话题发布成功')
        router.push('/community')
      } catch (error) {
        console.error('发布话题失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadDiseases()
})
</script>

<style scoped lang="scss">
.publish-topic-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .form-card {
    max-width: 800px;
    margin: 20px auto;

    .el-cascader {
      width: 100%;
    }

    .el-textarea :deep(textarea) {
      resize: vertical;
    }
  }
}
</style>
