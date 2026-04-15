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
        <el-form-item label="话题板块大类" prop="boardType">
          <el-select
            v-model="formData.boardType"
            placeholder="请选择话题板块大类"
            style="width: 100%"
            @change="handleBoardTypeChange"
          >
            <el-option label="疾病板块" :value="1" />
            <el-option label="医院评价" :value="2" />
            <el-option label="就医经验" :value="3" />
            <el-option label="康复护理" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="话题板块子类" prop="boardSub">
          <el-select
            v-model="formData.boardSub"
            placeholder="请选择话题板块子类"
            style="width: 100%"
            :loading="diseaseBoardsLoading"
            :disabled="!formData.boardType"
          >
            <el-option
              v-for="board in getBoardSubOptions(formData.boardType)"
              :key="board"
              :label="board"
              :value="board"
            />
          </el-select>
        </el-form-item>

        <el-form-item
          label="疾病分类" prop="diseaseCode"v-if="formData.boardType === 1">
          <el-select
            v-model="formData.diseaseCode" placeholder="请选择疾病分类" clearable style="width: 100%"
            :disabled="!formData.boardSub">
            <el-option v-for="item in filteredDiseaseOptions" :key="item.value" :label="item.label"
            :value="item.value"/>
          </el-select>
        </el-form-item>
<!-- 非疾病板块时显示原来的级联选择器（可选） -->
        <el-form-item v-else label="疾病分类" prop="diseaseCode">
          <el-cascader v-model="selectedDisease" :options="diseaseOptions" :props="{ expandTrigger: 'hover', value: 'code', label: 'name' }"
            placeholder="请选择疾病分类（可选）" clearable @change="handleDiseaseChange" style="width: 100%"/>
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

// 疾病板块选项（从数据库获取）
const diseaseBoards = ref([])
const diseaseBoardsLoading = ref(false)

// 保存完整的疾病数据（用于过滤）
const allDiseases = ref([])
// 加载疾病板块列表
const loadDiseaseBoards = async () => {
try {
  diseaseBoardsLoading.value = true
  const res = await getDiseaseTree()
  allDiseases.value = res.data  // 保存完整数据
  // 过滤出一级分类（parent_id = 0 或没有 parentId 的）
  diseaseBoards.value = res.data
    .filter(item => !item.parentId || item.parentId === 0)
    .map(item => item.diseaseName)
} catch (error) {
  console.error('加载疾病板块失败:', error)
  // 失败时使用默认值
  diseaseBoards.value = ['心血管疾病', '内分泌疾病', '肿瘤科', '儿科疾病', '妇科疾病', '骨科疾病',
    '神经系统疾病']
} finally {
  diseaseBoardsLoading.value = false
}
}

// 话题板块大类对应的话题板块子类
const getBoardSubOptions = (boardType) => {
  if (!boardType) return []
  switch (boardType) {
    case 1: // 疾病板块 - 从数据库获取一级分类
      return diseaseBoards.value
    case 2: // 医院评价
      return ['医院推荐', '医生推荐', '就医体验', '科室评价']
    case 3: // 就医经验
      return ['就诊经历', '用药经验', '检查经验', '手术经验']
    case 4: // 康复护理
      return ['康复训练', '护理知识', '心理健康', '营养饮食']
    default:
      return []
  }
}

// 根据话题板块子类过滤疾病分类选项
const filteredDiseaseOptions = computed(() => {
// 如果不是疾病板块，返回空数组
if (formData.boardType !== 1) {
  return []
}
// 如果选择了话题板块子类，过滤出对应的二级分类
if (formData.boardSub) {
  // 找到对应的一级疾病
  const level1Disease = allDiseases.value.find(
    item => item.diseaseName === formData.boardSub
  )
  if (level1Disease && level1Disease.children && level1Disease.children.length > 0) {
    // 返回二级分类（只返回 children）
    return level1Disease.children.map(child => ({
      value: child.diseaseCode,
      label: child.diseaseName
    }))
  }
}
// 如果没有选择板块子类，返回空数组
  return []
})

const formData = reactive({
  boardType: undefined,
  boardSub: '',
  diseaseCode: '',
  title: '',
  content: ''
})

// 验证规则
const rules = reactive({
  boardType: [
    { required: true, message: '请选择话题板块大类', trigger: 'change' }
  ],
  boardSub: [
    { required: true, message: '请选择话题板块子类', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入话题标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度为 5-200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入话题内容', trigger: 'blur' },
    { min: 10, max: 5000, message: '内容长度为 10-5000 个字符', trigger: 'blur' }
  ]
})

// 监听话题板块大类变化，动态更新验证规则
watch(() => formData.boardType, (newVal) => {
  // 清空其他字段
  formData.boardSub = ''
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

// 监听话题板块子类变化，清空疾病分类
watch(() => formData.boardSub, () => {
  formData.diseaseCode = ''
  selectedDisease.value = []
})
// 话题板块大类改变（兼容原来的调用）
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
  loadDiseaseBoards()
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
  :deep(.el-form-item__label) {
     width: 100px !important;
     white-space: nowrap;
  }
}
</style>
