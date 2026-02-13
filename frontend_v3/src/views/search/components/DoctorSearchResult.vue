<template>
  <div class="doctor-search-result">
    <div v-if="doctorList.length > 0" class="doctor-list">
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
  </div>

    <Empty v-else description="暂无医生数据" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DoctorListItem from '@/components/doctor/DoctorListItem.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()
const router = useRouter()

const doctorList = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadDoctors = async () => {
  // TODO: 实现医生搜索加载
  const keyword = route.query.keyword
  console.log('Search doctors:', keyword)
  // 临时空数据
  doctorList.value = []
  total.value = 0
}

const handleDoctorClick = (doctor) => {
  router.push(`/hospital/${doctor.hospitalId}`)
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

watch(() => route.query.keyword, () => {
  currentPage.value = 1
  loadDoctors()
}, { immediate: true })
</script>

<style scoped lang="scss">
.doctor-search-result {
  .doctor-list {
    background: #fff;
    border-radius: 4px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
