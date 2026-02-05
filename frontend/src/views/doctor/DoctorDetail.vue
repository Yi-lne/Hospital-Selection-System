<template>
  <div class="doctor-detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医生详情</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="detail-card">
      <template v-if="doctor">
        <!-- 基本信息 -->
        <div class="doctor-header" v-if="doctor">
          <el-avatar :size="100" :src="doctor.avatar || defaultAvatar" />
          <div class="header-info">
            <h1>{{ doctor.doctorName || '医生姓名' }} <span class="title">{{ doctor.title || '职称' }}</span></h1>
            <p class="hospital-info">{{ doctor.hospitalName || '医院' }} · {{ doctor.deptName || '科室' }}</p>
            <div class="tags">
              <el-tag v-if="doctor.title" type="success">{{ doctor.title }}</el-tag>
            </div>
          </div>
          <el-button
            :type="isCollected ? 'warning' : 'primary'"
            :icon="isCollected ? StarFilled : Star"
            size="large"
            @click="toggleCollect"
          >
            {{ isCollected ? '已收藏' : '收藏' }}
          </el-button>
        </div>

        <el-divider />

        <!-- 详细信息 -->
        <el-descriptions :column="2" border v-if="doctor">
          <el-descriptions-item label="所属医院">
            {{ doctor.hospitalName || '暂无' }}
          </el-descriptions-item>
          <el-descriptions-item label="所属科室">
            {{ doctor.deptName || '暂无' }}
          </el-descriptions-item>
          <el-descriptions-item label="职称">
            {{ doctor.title || '暂无' }}
          </el-descriptions-item>
          <el-descriptions-item label="挂号费">
            {{ doctor.consultationFee ? `¥${doctor.consultationFee}` : '暂无' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 擅长领域 -->
        <div v-if="doctor.specialty" class="section">
          <h3>擅长领域</h3>
          <p>{{ doctor.specialty }}</p>
        </div>

        <!-- 学术背景 -->
        <div v-if="doctor.academicBackground" class="section">
          <h3>学术背景</h3>
          <p>{{ doctor.academicBackground }}</p>
        </div>

        <!-- 出诊时间 -->
        <div v-if="doctor.scheduleTime" class="section">
          <h3>出诊时间</h3>
          <p>{{ doctor.scheduleTime }}</p>
        </div>

        <!-- 同医院医生 -->
        <div class="section">
          <div class="section-header">
            <h3>同医院医生</h3>
            <el-link type="primary" @click="$router.push(`/doctor?hospitalId=${doctor.hospitalId}`)">
              查看全部 →
            </el-link>
          </div>
          <el-row v-if="relatedDoctors.length > 0" :gutter="16">
            <el-col v-for="doc in relatedDoctors" :key="doc.id" :xs="24" :sm="12" :md="8">
              <DoctorCard :doctor="doc" />
            </el-col>
          </el-row>
          <Empty v-else description="暂无相关医生" />
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getDoctorDetail } from '@/api/doctor'
import { getHospitalDoctors } from '@/api/doctor'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()

const loading = ref(false)
const doctor = ref(null)
const relatedDoctors = ref([])
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 加载医生详情
const loadDoctorDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getDoctorDetail(id)
    doctor.value = res.data

    // 调试：打印返回的数据
    console.log('医生详情数据:', doctor.value)

    // 加载同医院医生
    if (doctor.value?.hospitalId) {
      const doctorRes = await getHospitalDoctors(doctor.value.hospitalId, 1, 6)
      // 确保数据结构正确
      if (doctorRes.data && doctorRes.data.list && Array.isArray(doctorRes.data.list)) {
        relatedDoctors.value = doctorRes.data.list.filter(d => d.id !== id)
      }
    }

    // 检查收藏状态
    if (doctor.value) {
      const collectRes = await checkCollected(COLLECTION_TYPE.DOCTOR, doctor.value.id)
      isCollected.value = collectRes.data
    }
  } catch (error) {
    console.error('Failed to load doctor detail:', error)
    ElMessage.error('加载医生详情失败')
  } finally {
    loading.value = false
  }
}

// 切换收藏
const toggleCollect = async () => {
  if (!doctor.value) return

  try {
    if (isCollected.value) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: doctor.value.id
      })
      ElMessage.success('取消收藏成功')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: doctor.value.id
      })
      ElMessage.success('收藏成功')
    }
    isCollected.value = !isCollected.value
  } catch (error) {
    console.error('Failed to toggle collection:', error)
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadDoctorDetail()
})
</script>

<style scoped lang="scss">
.doctor-detail-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .detail-card {
    margin-top: 20px;
  }

  .doctor-header {
    display: flex;
    gap: 20px;
    align-items: flex-start;

    .header-info {
      flex: 1;

      h1 {
        margin: 0 0 8px 0;
        font-size: 24px;
        font-weight: 600;

        .title {
          font-size: 18px;
          font-weight: 400;
          color: #606266;
          margin-left: 12px;
        }
      }

      .hospital-info {
        margin: 0 0 12px 0;
        color: #909399;
        font-size: 14px;
      }

      .tags {
        margin-top: 12px;
      }
    }
  }

  .section {
    margin-top: 24px;

    h3 {
      margin: 0 0 12px 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    p {
      margin: 0;
      line-height: 1.8;
      color: #606266;
    }
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
    }
  }
}
</style>
