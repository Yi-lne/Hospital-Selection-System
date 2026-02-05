<template>
  <div class="hospital-detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">医院详情</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="detail-card">
      <template v-if="hospital">
        <!-- 基本信息 -->
        <div class="hospital-header">
          <el-avatar :size="100" :src="hospital.avatar || defaultAvatar" shape="square" />
          <div class="header-info">
            <h1>{{ hospital.name }}</h1>
            <div class="tags">
              <el-tag type="danger" size="large">{{ displayLevel }}</el-tag>
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
        <el-descriptions :column="2" border>
          <el-descriptions-item label="所在地区">
            {{ hospital.province }} {{ hospital.city }}
          </el-descriptions-item>
          <el-descriptions-item label="详细地址">
            {{ hospital.address }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ hospital.phone || '暂无' }}
          </el-descriptions-item>
          <el-descriptions-item label="官方网站">
            <el-link v-if="hospital.website" :href="hospital.website" target="_blank" type="primary">
              {{ hospital.website }}
            </el-link>
            <span v-else>暂无</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 医院介绍 -->
        <div v-if="hospital.introduction" class="section">
          <h3>医院介绍</h3>
          <p>{{ hospital.introduction }}</p>
        </div>

        <!-- 特色专科 -->
        <div v-if="hospital.features" class="section">
          <h3>特色专科</h3>
          <p>{{ hospital.features }}</p>
        </div>

        <!-- 交通指南 -->
        <div v-if="hospital.trafficInfo" class="section">
          <h3>交通指南</h3>
          <p>{{ hospital.trafficInfo }}</p>
        </div>

        <!-- 医生列表 -->
        <div class="section">
          <div class="section-header">
            <h3>医生列表</h3>
            <el-link type="primary" @click="$router.push(`/doctor?hospitalId=${hospital.id}`)">
              查看全部 →
            </el-link>
          </div>
          <el-row v-if="doctors && doctors.length > 0" :gutter="16">
            <el-col v-for="doctor in doctors" :key="doctor.id" :xs="24" :sm="12" :md="8">
              <DoctorCard :doctor="doctor" />
            </el-col>
          </el-row>
          <Empty v-else description="暂无医生信息" />
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getHospitalDetail } from '@/api/hospital'
import { getHospitalDoctors } from '@/api/doctor'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import { formatHospitalLevel } from '@/utils/hospital'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'
import type { Hospital } from '@/types/hospital'
import type { DoctorSimple } from '@/types/doctor'

const route = useRoute()

const loading = ref(false)
const hospital = ref<Hospital | null>(null)
const doctors = ref<DoctorSimple[]>([])
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 将医院等级转换为中文显示
const displayLevel = computed(() => {
  if (!hospital.value) return ''
  return formatHospitalLevel(hospital.value.level)
})

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 加载医院详情
const loadHospitalDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getHospitalDetail(id)

    // 映射医院字段名
    hospital.value = {
      id: res.data.id,
      name: res.data.hospitalName || res.data.name,
      level: res.data.hospitalLevel || res.data.level,
      province: res.data.provinceName || res.data.province,
      city: res.data.cityName || res.data.city,
      address: res.data.address,
      phone: res.data.phone,
      website: res.data.website,
      introduction: res.data.intro || res.data.introduction,
      features: res.data.features,
      trafficInfo: res.data.trafficInfo,
      avatar: res.data.avatar,
      areaId: res.data.areaId,
      createTime: res.data.createTime,
      updateTime: res.data.updateTime
    }

    // 加载医生列表（后端返回的是List，不是分页结果）
    const doctorRes = await getHospitalDoctors(id)
    // 映射医生字段名 - 字段名必须与DoctorCard组件期望的字段名匹配
    doctors.value = (doctorRes.data || []).map((item: any) => ({
      id: item.id,
      doctorName: item.doctorName || item.name,
      title: item.title,
      hospitalName: item.hospitalName,
      deptName: item.deptName || item.departmentName,
      specialty: item.specialty,
      avatar: item.avatar
    }))

    // 检查收藏状态
    if (hospital.value) {
      const collectRes = await checkCollected(COLLECTION_TYPE.HOSPITAL, hospital.value.id)
      isCollected.value = collectRes.data
    }
  } catch (error) {
    console.error('Failed to load hospital detail:', error)
  } finally {
    loading.value = false
  }
}

// 切换收藏
const toggleCollect = async () => {
  if (!hospital.value) return

  try {
    if (isCollected.value) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.HOSPITAL,
        targetId: hospital.value.id
      })
      ElMessage.success('取消收藏成功')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.HOSPITAL,
        targetId: hospital.value.id
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
  loadHospitalDetail()
})
</script>

<style scoped lang="scss">
.hospital-detail-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .detail-card {
    margin-top: 20px;
  }

  .hospital-header {
    display: flex;
    gap: 20px;
    align-items: flex-start;

    .header-info {
      flex: 1;

      h1 {
        margin: 0 0 12px;
        font-size: 28px;
        color: #303133;
      }

      .tags {
        display: flex;
        gap: 8px;
      }
    }
  }

  .section {
    margin-top: 32px;

    h3 {
      margin: 0 0 16px;
      font-size: 18px;
      color: #303133;
    }

    p {
      margin: 0;
      line-height: 1.8;
      color: #606266;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }
  }
}

@media (max-width: 768px) {
  .hospital-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>
