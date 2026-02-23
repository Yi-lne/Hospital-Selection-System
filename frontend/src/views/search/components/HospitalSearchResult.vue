<template>
  <div v-loading="loading" class="hospital-search-result">
    <template v-if="hospitalList.length > 0">
      <!-- 医院列表 -->
      <el-row :gutter="16">
        <el-col
          v-for="hospital in hospitalList"
          :key="hospital.id"
          :xs="24"
          :sm="12"
          :md="8"
          class="hospital-item-col"
        >
          <HospitalCard :hospital="hospital" />
        </el-col>
      </el-row>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </template>
    <Empty v-else description="暂无医院数据" />
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { searchHospitals } from '@/api/hospital'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import Empty from '@/components/common/Empty.vue'

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update-total'])

const loading = ref(false)
const hospitalList = ref([])
const total = ref(0)

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const loadHospitals = async () => {
  if (!props.keyword) {
    emit('update-total', 0)
    return
  }

  try {
    loading.value = true
    const res = await searchHospitals(props.keyword, {
      page: pagination.page,
      pageSize: pagination.pageSize
    })

    hospitalList.value = (res.data.list || []).map((item) => ({
      id: item.id,
      name: item.hospitalName || item.name,
      level: item.hospitalLevel || item.level,
      province: item.provinceName || item.province,
      city: item.cityName || item.city,
      address: item.address,
      rating: item.rating,
      isMedicalInsurance: item.isMedicalInsurance,
      avatar: null
    }))
    total.value = res.data.total || 0
    emit('update-total', total.value)
  } catch (error) {
    console.error('搜索医院失败:', error)
    emit('update-total', 0)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pagination.page = page
  loadHospitals()
}

const handleSizeChange = (size) => {
  pagination.page = 1
  pagination.pageSize = size
  loadHospitals()
}

watch(() => props.keyword, () => {
  pagination.page = 1
  loadHospitals()
}, { immediate: true })
</script>

<style scoped lang="scss">
.hospital-search-result {
  .hospital-item-col {
    margin-bottom: 16px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
