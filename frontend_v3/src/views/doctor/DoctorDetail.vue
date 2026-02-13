<template>
  <div class="doctor-detail-page" v-loading="loading">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医生详情</span>
      </template>
    </el-page-header>

    <el-card v-if="doctor" class="doctor-card">
      <!-- 医生头部 -->
      <div class="doctor-header">
        <el-avatar :size="80" :src="doctor.avatar || defaultAvatar">
          {{ doctor.doctorName?.charAt(0) || '医' }}
        </el-avatar>
        <div class="doctor-info">
          <h4>{{ doctor.doctorName }}</h4>
          <p class="title">{{ doctor.title || '医师' }}</p>
          <p class="department">{{ doctor.hospitalName }} - {{ doctor.deptName }}</p>
        </div>
      </div>

      <!-- 医生详情 -->
      <div class="doctor-details">
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="医生姓名">
              {{ doctor.doctorName }}
            </el-descriptions-item>
            <el-descriptions-item label="职称">
              {{ doctor.title || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="所属医院">
              {{ doctor.hospitalName }}
            </el-descriptions-item>
            <el-descriptions-item label="所属科室">
              {{ doctor.deptName }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h3 class="section-title">专业信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="专业特长">
              {{ doctor.specialty || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="出诊时间">
              {{ doctor.scheduleTime || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="挂号费">
              <span class="fee">¥{{ doctor.consultationFee || '0' }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h3 class="section-title">评价信息</h3>
          <div class="rating-info">
            <el-rate v-model="displayRating" disabled show-score text-color="#ff9900" />
            <span class="rating-text">{{ displayRating }} 分</span>
            <span class="review-count">({{ doctor.reviewCount || 0 }}条评价)</span>
          </div>
        </div>

        <div class="action-section">
          <el-button type="primary" @click="handleCollect" v-if="!isCollected">
            <el-icon><Star /></el-icon>
            <span>收藏医生</span>
          </el-button>
          <el-button type="primary" @click="handleCancelCollect" v-else>
            <el-icon><StarFilled /></el-icon>
            <span>取消收藏</span>
          </el-button>
          <el-button type="primary" @click="goBack">返回</el-button>
        </div>
      </div>
    </el-card>

    <Empty v-else description="医生信息不存在" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getDoctorDetail } from '@/api/doctor'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const doctor = ref<any>(null)
const isCollected = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 显示评分
const displayRating = computed(() => {
  return doctor.value?.rating || 0
})

// 加载医生详情
const loadDoctorDetail = async () => {
  try {
    loading.value = true
    const doctorId = route.params.id
    const res = await getDoctorDetail(doctorId)

    if (res.code === 200 && res.data) {
      doctor.value = {
        id: res.data.id,
        doctorName: res.data.doctorName || res.data.name,
        title: res.data.title,
        hospitalName: res.data.hospitalName,
        deptName: res.data.deptName || res.data.departmentName,
        specialty: res.data.specialty,
        scheduleTime: res.data.scheduleTime,
        consultationFee: res.data.consultationFee,
        rating: res.data.rating,
        reviewCount: res.data.reviewCount,
        avatar: res.data.avatar,
        hospitalId: res.data.hospitalId,
        deptId: res.data.deptId
      }

      // 检查是否已收藏
      const collectionRes = await checkCollected(COLLECTION_TYPE.DOCTOR, doctorId)
      isCollected.value = collectionRes.code === 200 && collectionRes.data === true
    } else {
      ElMessage.error(res.message || '加载医生详情失败')
      doctor.value = null
    }
  } catch (error) {
    console.error('Failed to load doctor detail:', error)
    ElMessage.error('加载医生详情失败')
  } finally {
    loading.value = false
  }
}

// 收藏医生
const handleCollect = async () => {
  try {
    const res = await addCollection({
      targetType: COLLECTION_TYPE.DOCTOR,
      targetId: doctor.value.id
    })
    if (res.code === 200) {
      ElMessage.success('收藏成功')
      isCollected.value = true
    } else {
      ElMessage.error(res.message || '收藏失败')
    }
  } catch (error) {
    console.error('Failed to collect doctor:', error)
    ElMessage.error('收藏失败')
  }
}

// 取消收藏
const handleCancelCollect = async () => {
  try {
    const res = await cancelCollection({
      targetType: COLLECTION_TYPE.DOCTOR,
      targetId: doctor.value.id
    })
    if (res.code === 200) {
      ElMessage.success('取消收藏成功')
      isCollected.value = false
    } else {
      ElMessage.error(res.message || '取消收藏失败')
    }
  } catch (error) {
    console.error('Failed to cancel collection:', error)
    ElMessage.error('取消收藏失败')
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadDoctorDetail()
})
</script>

<style scoped lang="scss">
.doctor-detail-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;

  .page-title {
    font-size: 22px;
    font-weight: 600;
  }

  .doctor-card {
    max-width: 1200px;
    margin: 20px auto;

    .doctor-header {
      display: flex;
      gap: 16px;
      align-items: center;
      padding: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;

      .el-avatar {
        flex-shrink: 0;
      }

      .doctor-info {
        flex: 1;
        color: #fff;

        h4 {
          margin: 0 0 8px 0;
          font-size: 20px;
          font-weight: 600;
        }

        .title {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
          margin: 0;
        }

        .department {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.7);
        }
      }
    }

    .doctor-details {
      padding: 20px;
    }

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 20px 0;
      padding-bottom: 10px;
      border-bottom: 2px solid #e4e7ed;
    }

    .detail-section {
      margin-bottom: 30px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .rating-info {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
    }

    .action-section {
      display: flex;
      gap: 12px;
      padding: 20px;
      justify-content: center;

      .el-button {
        flex: 1;
      }
    }
  }

  .fee {
    font-size: 20px;
    font-weight: 600;
    color: #f56c6c;
  }
}
</style>
