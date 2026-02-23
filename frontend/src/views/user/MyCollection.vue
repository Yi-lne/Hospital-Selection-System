<template>
  <div class="my-collection-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">我的收藏</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="list-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="医院" name="hospital">
          <div v-if="hospitalList.length > 0" class="collection-list">
            <HospitalCard
              v-for="item in hospitalList"
              :key="item.id"
              :hospital="item"
              @click="handleHospitalClick(item)"
              @collection-change="loadCollection"
            />
          </div>
          <Empty v-else description="暂无收藏的医院" />
        </el-tab-pane>

        <el-tab-pane label="医生" name="doctor">
          <div v-if="doctorList.length > 0" class="collection-list">
            <DoctorCard
              v-for="item in doctorList"
              :key="item.id"
              :doctor="item"
              @click="handleDoctorClick(item)"
              @collection-change="loadCollection"
            />
          </div>
          <Empty v-else description="暂无收藏的医生" />
        </el-tab-pane>

        <el-tab-pane label="话题" name="topic">
          <div v-if="topicList.length > 0" class="collection-list">
            <TopicCard
              v-for="item in topicList"
              :key="item.id"
              :topic="item"
              @click="handleTopicClick(item)"
              @collection-change="loadCollection"
            />
          </div>
          <Empty v-else description="暂无收藏的话题" />
        </el-tab-pane>
      </el-tabs>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadCollection"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCollectionList } from '@/api/collection'
import HospitalCard from '@/components/hospital/HospitalCard.vue'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import TopicCard from '@/components/community/TopicCard.vue'
import Empty from '@/components/common/Empty.vue'




const loading = ref(false)
const activeTab = ref('hospital')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const hospitalList = ref([])
const doctorList = ref([])
const topicList = ref([])

const router = useRouter()
const route = useRoute()

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 加载收藏列表
const loadCollection = async () => {
  try {
    loading.value = true
    const res = await getCollectionList(page.value, pageSize.value)
    total.value = res.data.total

    // 根据类型分类
    hospitalList.value = []
    doctorList.value = []
    topicList.value = []

    res.data.list.forEach((item) => {
      if (item.targetType === COLLECTION_TYPE.HOSPITAL) {
        // 提取医院信息并映射字段名
        if (item.hospital) {
          hospitalList.value.push({
            id: item.hospital.id,
            name: item.hospital.hospitalName,
            level: item.hospital.hospitalLevel,
            province: item.hospital.provinceName,
            city: item.hospital.cityName,
            address: item.hospital.address,
            phone: item.hospital.phone,
            avatar: ''
          })
        }
      } else if (item.targetType === COLLECTION_TYPE.DOCTOR) {
        // 提取医生信息并映射字段名
        if (item.doctor) {
          doctorList.value.push({
            id: item.doctor.id,
            doctorName: item.doctor.doctorName,
            title: item.doctor.title,
            hospitalName: item.doctor.hospitalName,
            deptName: item.doctor.deptName,
            specialty: item.doctor.specialty,
            scheduleTime: item.doctor.scheduleTime || '暂无',
            consultationFee: item.doctor.consultationFee || 0,
            rating: item.doctor.rating || 0,
            reviewCount: item.doctor.reviewCount || 0,
            avatar: item.doctor.avatar || ''
          })
        }
      } else if (item.targetType === COLLECTION_TYPE.TOPIC) {
        // 提取话题信息并映射字段名
        if (item.topic) {
          topicList.value.push({
            id: item.topic.id,
            title: item.topic.title,
            content: item.topic.contentSummary,
            nickname: item.topic.nickname,
            avatar: item.topic.avatar,
            likeCount: item.topic.likeCount,
            commentCount: item.topic.commentCount,
            collectCount: item.topic.collectCount,
            viewCount: item.topic.viewCount,
            createTime: item.topic.createTime
          })
        }
      }
    })
  } catch (error) {
    console.error('加载收藏失败:', error)
  } finally {
    loading.value = false
  }
}

// Tab切换
const handleTabChange = () => {
  page.value = 1
  loadCollection()
}

// 分页大小改变
const handleSizeChange = (size) => {
  page.value = 1
  pageSize.value = size
  loadCollection()
}

onMounted(() => {
  // 检查是否有指定的activeTab参数
  if (route.query.activeTab) {
    activeTab.value = route.query.activeTab
  }

  loadCollection()
})

// 点击医生卡片
const handleDoctorClick = (doctor) => {
  router.push({
    path: `/doctor/${doctor.id}`,
    query: {
      from: 'myCollection',
      tab: activeTab.value  // 传递当前激活的Tab（hospital/doctor/topic）
    }
  })
}

// 点击医院卡片
const handleHospitalClick = (hospital) => {
  router.push(`/hospital/${hospital.id}`)
}

// 点击话题卡片
const handleTopicClick = (topic) => {
  router.push(`/community/topic/${topic.id}`)
}
</script>

<style scoped lang="scss">
.my-collection-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .list-card {
    margin-top: 20px;
    min-height: 400px;
  }

  .collection-list {
    display: grid;
    gap: 16px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
