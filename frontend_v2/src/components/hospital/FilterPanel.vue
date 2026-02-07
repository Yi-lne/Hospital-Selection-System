<template>
  <div class="filter-panel">
    <el-card>
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <el-button text type="primary" @click="handleReset">重置</el-button>
        </div>
      </template>

      <el-form :model="filterData" label-width="80px" class="filter-form">
        <!-- 疾病类型 -->
        <el-form-item label="疾病类型">
          <el-cascader
            v-model="filterData.diseaseCode"
            :options="diseaseOptions"
            :props="{ label: 'diseaseName', value: 'diseaseCode', children: 'children' }"
            placeholder="选择疾病"
            clearable
            change-on-select
            @change="handleFilterChange"
          />
        </el-form-item>

        <!-- 医院级别 -->
        <el-form-item label="医院级别">
          <el-select v-model="filterData.hospitalLevel" placeholder="选择级别" clearable @change="handleFilterChange">
            <el-option
              v-for="option in hospitalLevelOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>

        <!-- 地理位置 -->
        <el-form-item label="地区">
          <el-cascader
            v-model="filterData.areaCode"
            :options="areaOptions"
            :props="{ label: 'name', value: 'code', children: 'children', checkStrictly: true }"
            placeholder="选择地区"
            clearable
            @change="handleFilterChange"
          />
        </el-form-item>

        <!-- 医保定点 -->
        <el-form-item label="医保定点">
          <el-select v-model="filterData.isMedicalInsurance" placeholder="是否医保" clearable @change="handleFilterChange">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>

        <!-- 重点科室 -->
        <el-form-item label="重点科室">
          <el-input
            v-model="filterData.keyDepartments"
            placeholder="如：心内科、肿瘤科"
            clearable
            @change="handleFilterChange"
          />
        </el-form-item>

        <!-- 排序方式 -->
        <el-form-item label="排序方式">
          <el-select v-model="filterData.sortBy" placeholder="排序方式" @change="handleFilterChange">
            <el-option label="默认排序" value="default" />
            <el-option label="评分最高" value="rating" />
            <el-option label="级别优先" value="level" />
          </el-select>
        </el-form-item>

        <!-- 应用筛选按钮 -->
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleApply">
            应用筛选
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getDiseaseTree } from '@/api/disease'
import { getAreaTree } from '@/api/area'
import { getHospitalLevelOptions } from '@/utils/hospital'

const emit = defineEmits(['filter', 'reset'])

const diseaseOptions = ref([])
const areaOptions = ref([])
const hospitalLevelOptions = getHospitalLevelOptions()

const filterData = reactive({
  diseaseCode: undefined,
  hospitalLevel: undefined,
  areaCode: undefined, // 级联选择器的值（数组）
  provinceCode: undefined,
  cityCode: undefined,
  isMedicalInsurance: undefined,
  keyDepartments: undefined,
  sortBy: 'default'
})

// 加载疾病分类树
const loadDiseaseTree = async () => {
  try {
    const res = await getDiseaseTree()
    diseaseOptions.value = res.data || []
  } catch (error) {
    console.error('加载疾病树失败:', error)
  }
}

// 加载地区树
const loadAreaTree = async () => {
  try {
    const res = await getAreaTree()
    areaOptions.value = res.data || []
  } catch (error) {
    console.error('加载地区树失败:', error)
  }
}

// 筛选条件改变
const handleFilterChange = () => {
  // 自动触发筛选（可选）
}

// 应用筛选
const handleApply = () => {
  const params = { ...filterData }

  // 处理地区编码（支持多级筛选）
  if (Array.isArray(params.areaCode) && params.areaCode.length > 0) {
    const level = params.areaCode.length

    if (level === 1) {
      // 只选择了省份
      params.provinceCode = params.areaCode[0]
      params.cityCode = undefined
      params.areaCode = undefined
    } else if (level === 2) {
      // 选择了省份+城市
      params.provinceCode = params.areaCode[0]
      params.cityCode = params.areaCode[1]
      params.areaCode = undefined
    } else if (level === 3) {
      // 选择了省份+城市+区县
      params.provinceCode = params.areaCode[0]
      params.cityCode = params.areaCode[1]
      params.areaCode = params.areaCode[2]
    }
  } else {
    // 未选择地区
    params.provinceCode = undefined
    params.cityCode = undefined
    params.areaCode = undefined
  }

  // 处理疾病编码
  if (Array.isArray(params.diseaseCode) && params.diseaseCode.length > 0) {
    params.diseaseCode = params.diseaseCode[params.diseaseCode.length - 1]
  }

  emit('filter', params)
}

// 重置筛选
const handleReset = () => {
  Object.assign(filterData, {
    diseaseCode: undefined,
    hospitalLevel: undefined,
    areaCode: undefined,
    provinceCode: undefined,
    cityCode: undefined,
    isMedicalInsurance: undefined,
    keyDepartments: undefined,
    sortBy: 'default'
  })
  emit('reset')
}

onMounted(() => {
  loadDiseaseTree()
  loadAreaTree()
})
</script>

<style scoped lang="scss">
.filter-panel {
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .filter-form {
    .el-form-item {
      margin-bottom: 18px;
    }

    .el-cascader,
    .el-select {
      width: 100%;
    }
  }
}
</style>
