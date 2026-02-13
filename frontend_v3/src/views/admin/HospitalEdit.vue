<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑医院' : '新增医院'"
    width="800px"
    @update:model-value="$emit('update:visible', $event)"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      v-loading="loading"
    >
      <el-form-item label="医院名称" prop="hospitalName">
        <el-input v-model="form.hospitalName" placeholder="请输入医院名称" />
      </el-form-item>

      <el-form-item label="医院级别" prop="hospitalLevel">
        <el-select v-model="form.hospitalLevel" placeholder="请选择医院级别">
          <el-option label="三级甲等" value="grade3A" />
          <el-option label="三级乙等" value="grade3B" />
          <el-option label="三级丙等" value="grade3C" />
          <el-option label="二级甲等" value="grade2A" />
          <el-option label="二级乙等" value="grade2B" />
          <el-option label="二级丙等" value="grade2C" />
          <el-option label="一级甲等" value="grade1A" />
          <el-option label="一级乙等" value="grade1B" />
          <el-option label="一级丙等" value="grade1C" />
        </el-select>
      </el-form-item>

      <el-form-item label="所在地区" prop="area" required>
        <div class="area-selector">
          <el-select
            v-model="form.provinceCode"
            placeholder="请选择省份"
            @change="handleProvinceChange"
            style="width: 180px; margin-right: 10px"
          >
            <el-option
              v-for="province in provinces"
              :key="province.code"
              :label="province.name"
              :value="province.code"
            />
          </el-select>
          <el-select
            v-model="form.cityCode"
            placeholder="请选择城市"
            @change="handleCityChange"
            :disabled="!form.provinceCode"
            style="width: 180px; margin-right: 10px"
          >
            <el-option
              v-for="city in cities"
              :key="city.code"
              :label="city.name"
              :value="city.code"
            />
          </el-select>
          <el-select
            v-model="form.areaCode"
            placeholder="请选择区县"
            :disabled="!form.cityCode"
            style="width: 180px"
          >
            <el-option
              v-for="area in areas"
              :key="area.code"
              :label="area.name"
              :value="area.code"
            />
          </el-select>
        </div>
      </el-form-item>

      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入详细地址" />
      </el-form-item>

      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系电话" />
      </el-form-item>

      <el-form-item label="重点科室" prop="keyDepartments">
        <el-input
          v-model="form.keyDepartments"
          type="textarea"
          :rows="3"
          placeholder="请输入重点科室，多个科室用逗号分隔"
        />
      </el-form-item>

      <el-form-item label="医疗设备" prop="medicalEquipment">
        <el-input
          v-model="form.medicalEquipment"
          type="textarea"
          :rows="3"
          placeholder="请输入医疗设备介绍"
        />
      </el-form-item>

      <el-form-item label="专家团队" prop="expertTeam">
        <el-input
          v-model="form.expertTeam"
          type="textarea"
          :rows="3"
          placeholder="请输入专家团队介绍"
        />
      </el-form-item>

      <el-form-item label="医院简介" prop="intro">
        <el-input
          v-model="form.intro"
          type="textarea"
          :rows="5"
          placeholder="请输入医院简介"
        />
      </el-form-item>

      <el-form-item label="初始评分" prop="rating">
        <el-rate v-model="form.rating" allow-half />
        <span style="margin-left: 10px; color: #999">可选，默认为0</span>
      </el-form-item>

      <el-form-item label="医保定点" prop="isMedicalInsurance">
        <el-switch v-model="form.isMedicalInsurance" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ isEdit ? '保存' : '创建' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'
import { getProvinces, getCities, getDistricts } from '@/api/area'

const props = defineProps({
  visible: Boolean,
  hospitalId: [Number, null]
})

const emit = defineEmits(['update:visible', 'success'])

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const provinces = ref([])
const cities = ref([])
const areas = ref([])

const isEdit = computed(() => !!props.hospitalId)

const form = ref({
  hospitalName: '',
  hospitalLevel: '',
  provinceCode: '',
  cityCode: '',
  areaCode: '',
  address: '',
  phone: '',
  keyDepartments: '',
  medicalEquipment: '',
  expertTeam: '',
  intro: '',
  rating: 0,
  isMedicalInsurance: 0
})

const rules = {
  hospitalName: [
    { required: true, message: '请输入医院名称', trigger: 'blur' }
  ],
  hospitalLevel: [
    { required: true, message: '请选择医院级别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ]
}

// 监听 visible 变化，加载数据
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadProvinces()
    if (props.hospitalId) {
      loadHospitalDetail()
    } else {
      resetForm()
    }
  }
})

// 加载省份
const loadProvinces = async () => {
  try {
    const res = await getProvinces()
    provinces.value = res.data || []
  } catch (error) {
    console.error('加载省份失败:', error)
  }
}

// 省份改变
const handleProvinceChange = async () => {
  form.value.cityCode = ''
  form.value.areaCode = ''
  cities.value = []
  areas.value = []
  if (form.value.provinceCode) {
    try {
      const res = await getCities(form.value.provinceCode)
      cities.value = res.data || []
    } catch (error) {
      console.error('加载城市失败:', error)
    }
  }
}

// 城市改变
const handleCityChange = async () => {
  form.value.areaCode = ''
  areas.value = []
  if (form.value.cityCode) {
    try {
      const res = await getDistricts(form.value.cityCode)
      areas.value = res.data || []
    } catch (error) {
      console.error('加载区县失败:', error)
    }
  }
}

// 加载医院详情
const loadHospitalDetail = async () => {
  try {
    loading.value = true
    const res = await adminApi.getHospitalDetail(props.hospitalId)
    const hospital = res.data
    form.value = {
      hospitalName: hospital.hospitalName || '',
      hospitalLevel: hospital.hospitalLevel || '',
      provinceCode: hospital.provinceCode || '',
      cityCode: hospital.cityCode || '',
      areaCode: hospital.areaCode || '',
      address: hospital.address || '',
      phone: hospital.phone || '',
      keyDepartments: hospital.keyDepartments || '',
      medicalEquipment: hospital.medicalEquipment || '',
      expertTeam: hospital.expertTeam || '',
      intro: hospital.intro || '',
      rating: hospital.rating || 0,
      isMedicalInsurance: hospital.isMedicalInsurance || 0
    }
    // 加载城市和区县
    if (form.value.provinceCode) {
      await handleProvinceChange()
    }
    if (form.value.cityCode) {
      await handleCityChange()
    }
  } catch (error) {
    ElMessage.error('加载医院详情失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    hospitalName: '',
    hospitalLevel: '',
    provinceCode: '',
    cityCode: '',
    areaCode: '',
    address: '',
    phone: '',
    keyDepartments: '',
    medicalEquipment: '',
    expertTeam: '',
    intro: '',
    rating: 0,
    isMedicalInsurance: 0
  }
  cities.value = []
  areas.value = []
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    // 验证地区选择
    if (!form.value.provinceCode || !form.value.cityCode || !form.value.areaCode) {
      ElMessage.warning('请完整选择所在地区')
      return
    }

    if (isEdit.value) {
      await adminApi.updateHospital(props.hospitalId, form.value)
      ElMessage.success('更新成功')
    } else {
      await adminApi.createHospital(form.value)
      ElMessage.success('创建成功')
    }
    emit('success')
  } catch (error) {
    if (error.message) {
      ElMessage.error((isEdit.value ? '更新' : '创建') + '失败：' + error.message)
    }
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
  setTimeout(() => {
    resetForm()
  }, 300)
}
</script>

<style scoped>
.area-selector {
  display: flex;
  align-items: center;
}
</style>
