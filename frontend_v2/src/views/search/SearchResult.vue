<template>
  <div class="search-result-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">搜索结果：{{ keyword }}</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" class="search-tabs">
      <!-- 医院搜索结果 -->
      <el-tab-pane label="医院" name="hospital">
        <el-card v-loading="hospitalLoading">
          <template v-if="hospitalResults.length > 0">
            <HospitalCard
              v-for="hospital in hospitalResults"
              :key="hospital.id"
              :hospital="hospital"
            />
            <el-pagination
              v-if="hospitalTotal > 0"
              :current-page="hospitalPage"
              :page-size="hospitalPageSize"
              :total="hospitalTotal"
              layout="prev, pager, next"
              @current-change="handleHospitalPageChange"
              class="pagination"
            />
          </template>
          <Empty v-else description="暂无医院搜索结果" />
        </el-card>
      </el-tab-pane>

      <!-- 医生搜索结果 -->
      <el-tab-pane label="医生" name="doctor">
        <el-card v-loading="doctorLoading">
          <template v-if="doctorResults.length > 0">
            <DoctorCard
              v-for="doctor in doctorResults"
              :key="doctor.id"
              :doctor="doctor"
            />
            <el-pagination
              v-if="doctorTotal > 0"
              :current-page="doctorPage"
              :page-size="doctorPageSize"
              :total="doctorTotal"
              layout="prev, pager, next"
              @current-change="handleDoctorPageChange"
              class="pagination"
            />
          </template>
          <Empty v-else description="暂无医生搜索结果" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchHospitals } from '@/api/hospital'
import { searchDoctors } from '@/api/doctor'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()

const keyword = ref('')
const activeTab = ref('hospital')

// 医院搜索结果
const hospitalLoading = ref(false)
const hospitalResults = ref([])
const hospitalTotal = ref(0)
const hospitalPage = ref(1)
const hospitalPageSize = ref(10)

// 医生搜索结果
const doctorLoading = ref(false)
const doctorResults = ref([])
const doctorTotal = ref(0)
const doctorPage = ref(1)
const doctorPageSize = ref(10)

// 加载医院搜索结果
const loadHospitalResults = async () => {
  if (!keyword.value) return

  try {
    hospitalLoading.value = true
    const params = {
      page: hospitalPage.value,
      pageSize: hospitalPageSize.value
    }
    const res = await searchHospitals(keyword.value, params)

    // 映射字段
    hospitalResults.value = (res.data.list || []).map(item => ({
      id: item.id,
      name: item.hospitalName || item.name,
      level: item.hospitalLevel || item.level,
      province: item.provinceName || item.province,
      city: item.cityName || item.city,
      area: item.areaName || item.area,
      address: item.address,
      rating: item.rating,
      reviewCount: item.reviewCount,
      isMedicalInsurance: item.isMedicalInsurance
    }))
    hospitalTotal.value = res.data.total || 0
  } catch (error) {
    console.error('搜索医院失败:', error)
  } finally {
    hospitalLoading.value = false
  }
}

// 加载医生搜索结果
const loadDoctorResults = async () => {
  if (!keyword.value) return

  try {
    doctorLoading.value = true
    const params = {
      page: doctorPage.value,
      pageSize: doctorPageSize.value
    }
    const res = await searchDoctors(keyword.value, params)

    // 映射字段
    doctorResults.value = res.data.list || []
    doctorTotal.value = res.data.total || 0
  } catch (error) {
    console.error('搜索医生失败:', error)
  } finally {
    doctorLoading.value = false
  }
}

// 医院分页改变
const handleHospitalPageChange = (page) => {
  hospitalPage.value = page
  loadHospitalResults()
}

// 医生分页改变
const handleDoctorPageChange = (page) => {
  doctorPage.value = page
  loadDoctorResults()
}

// 监听关键词变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    hospitalPage.value = 1
    doctorPage.value = 1
    loadHospitalResults()
    loadDoctorResults()
  }
}, { immediate: true })

onMounted(() => {
  keyword.value = route.query.keyword || ''
  if (keyword.value) {
    loadHospitalResults()
    loadDoctorResults()
  }
})
</script>

<style scoped lang="scss">
.search-result-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .search-tabs {
    margin-top: 20px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
