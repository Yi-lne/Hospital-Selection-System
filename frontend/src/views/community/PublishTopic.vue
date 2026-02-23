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
        label-width="100px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="板块类型" prop="boardType">
          <el-select
            v-model="formData.boardType"
            placeholder="请选择板块类型"
            style="width: 100%"
            @change="handleBoardTypeChange"
          >
            <el-option label="疾病板块" :value="1" />
            <el-option label="医院评价" :value="2" />
            <el-option label="就医经验" :value="3" />
            <el-option label="康复护理" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="一级板块" prop="boardLevel1">
          <el-select
            v-model="formData.boardLevel1"
            placeholder="请选择一级板块"
            style="width: 100%"
            :disabled="!formData.boardType"
          >
            <el-option
              v-for="board in level1Boards"
              :key="board"
              :label="board"
              :value="board"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="二级板块" prop="boardLevel2">
          <el-input
            v-model="formData.boardLevel2"
            placeholder="请输入二级板块（可选）"
            maxlength="50"
            show-word-limit
            clearable
          />
        </el-form-item>

        <el-form-item :label="formData.boardType === 1 ? '疾病分类 *' : '疾病分类'" prop="diseaseCode">
          <el-cascader
            v-model="selectedDisease"
            :options="diseaseOptions"
            :props="{ expandTrigger: 'hover', value: 'code', label: 'name' }"
            :placeholder="formData.boardType === 1 ? '请选择疾病分类' : '请选择疾病分类（可选）'"
            clearable
            @change="handleDiseaseChange"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入话题标题"
            maxlength="200"
            show-word-limit
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
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishTopic, getBoardList } from '@/api/community'
import { getDiseaseTree } from '@/api/disease'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const diseaseOptions = ref([])
const selectedDisease = ref([])
const boardList = ref([])

// 板块类型对应的一级板块
const level1Boards = computed(() => {
  if (!formData.boardType) return []
  switch (formData.boardType) {
    case 1: // 疾病板块
      return ['心血管', '内分泌', '肿瘤', '儿科', '妇产科', '骨科', '神经科']
    case 2: // 医院评价
      return ['医院推荐', '医生推荐', '就医体验', '科室评价']
    case 3: // 就医经验
      return ['就诊经历', '用药经验', '检查经验', '手术经验']
    case 4: // 康复护理
      return ['康复训练', '护理知识', '心理健康', '营养饮食']
    default:
      return []
  }
})

const formData = reactive({
  boardType: undefined,
  boardLevel1: '',
  boardLevel2: '',
  diseaseCode: '',
  title: '',
  content: ''
})

// 验证规则
const rules = reactive({
  boardType: [
    { required: true, message: '请选择板块类型', trigger: 'change' }
  ],
  boardLevel1: [
    { required: true, message: '请选择一级板块', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入话题标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度为5-200个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入话题内容', trigger: 'blur' },
    { min: 10, max: 5000, message: '内容长度为10-5000个字符', trigger: 'blur' }
  ]
})

// 监听板块类型变化，动态更新验证规则
watch(() => formData.boardType, (newVal) => {
  // 清空其他字段
  formData.boardLevel1 = ''
  formData.boardLevel2 = ''
  formData.diseaseCode = ''
  selectedDisease.value = []

  if (newVal === 1) {
    // 选择疾病板块，添加疾病分类必填验证
    rules.diseaseCode = [
      { required: true, message: '请选择疾病分类', trigger: 'change' }
    ]
  } else {
    // 移除疾病分类验证
    delete rules.diseaseCode
  }

  // 清除之前的验证结果
  nextTick(() => {
    formRef.value?.clearValidate()
  })
})

// 板块类型改变（兼容原来的调用）
const handleBoardTypeChange = () => {
  // watch 已经处理，这里留空或可以添加其他逻辑
}

// 转换疾病数据
const transformDiseaseData = (diseases = []) => {
  return diseases.map(disease => ({
    id: disease.id,
    name: disease.diseaseName || disease.name,
    code: disease.diseaseCode,
    parentId: disease.parentId,
    level: disease.level,
    children: disease.children ? transformDiseaseData(disease.children) : undefined
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
const handleDiseaseChange = (value) => {
  formData.diseaseCode = value?.length > 0 ? value[value.length - 1] : ''
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
  }

  :deep(.el-textarea__inner) {
    resize: vertical;
  }
}
</style>
