<template>
  <div class="doctor-search-result">
    <div v-loading="loading" v-if="doctorList.length > 0" class="doctor-list">
      <DoctorListItem
        v-for="doctor in doctorList"
        :key="doctor.id"
        :doctor="doctor"
        @click="handleDoctorClick"
      />
    </div>
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
    <Empty v-else-if="!loading" description="暂无医生数据" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchDoctors } from '@/api/doctor'
import DoctorListItem from '@/components/doctor/DoctorListItem.vue'
import Empty from '@/components/common/Empty.vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  keyword: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update-total'])

const route = useRoute()
const router = useRouter()

const doctorList = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)

const loadDoctors = async () => {
  if (!props.keyword) {
    doctorList.value = []
    total.value = 0
    emit('update-total', 0)
    return
  }

  loading.value = true
  try {
    const res = await searchDoctors(props.keyword, {
      page: currentPage.value,
      pageSize: pageSize.value
    })

    if (res.code === 200) {
      doctorList.value = res.data.list || []
      total.value = res.data.total || 0
      emit('update-total', total.value)
    } else {
      ElMessage.error(res.message || '搜索医生失败')
    }
  } catch (error) {
    console.error('搜索医生失败:', error)
    ElMessage.error('搜索医生失败，请稍后重试')
    doctorList.value = []
    total.value = 0
    emit('update-total', 0)
  } finally {
    loading.value = false
  }
}

const handleDoctorClick = (doctor) => {
  router.push(`/doctor/${doctor.id}`)
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadDoctors()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadDoctors()
}

// 监听keyword变化
watch(() => props.keyword, () => {
  currentPage.value = 1
  loadDoctors()
}, { immediate: false })

// 组件挂载时加载数据
onMounted(() => {
  loadDoctors()
})
</script>

<style scoped lang="scss">
.doctor-search-result {
  min-height: calc(100vh - 120px);
}

.doctor-list {
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>
