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
            :type="isHospitalCollected ? 'warning' : 'primary'"
            :icon="isHospitalCollected ? StarFilled : Star"
            size="large"
            @click="toggleCollectHospital"
          >
            {{ isHospitalCollected ? '已收藏' : '收藏' }}
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

        <!-- 医生列表模块（增强版）-->
        <div class="section doctor-section">
          <div class="section-header">
            <h3>医生团队</h3>
            <div class="stats">
              <span>共 {{ doctorTotal }} 位医生</span>
            </div>
          </div>

          <!-- 筛选工具栏 -->
          <div class="doctor-toolbar">
            <el-select
              v-model="filterDeptName"
              placeholder="按科室筛选"
              clearable
              @change="handleDeptFilter"
              style="width: 200px; margin-right: 12px"
            >
              <el-option label="全部科室" value="" />
              <el-option
                v-for="(dept, index) in departments"
                :key="dept.deptName + index"
                :label="dept.deptName"
                :value="dept.deptName"
              />
            </el-select>

            <el-select
              v-model="filterTitle"
              placeholder="按职称筛选"
              clearable
              @change="handleTitleFilter"
              style="width: 200px"
            >
              <el-option label="全部职称" value="" />
              <el-option
                v-for="title in titles"
                :key="title"
                :label="title"
                :value="title"
              />
            </el-select>
          </div>

          <!-- 医生列表 -->
          <div v-if="paginatedDoctors.length > 0" class="doctor-list">
            <el-row :gutter="16">
              <el-col
                v-for="doctor in paginatedDoctors"
                :key="doctor.id"
                :xs="24"
                :sm="12"
                :md="8"
                :lg="6"
              >
                <el-card class="doctor-card" shadow="hover" @click="handleDoctorClick(doctor)">
                  <!-- 医生头部 -->
                  <div class="doctor-header">
                    <el-avatar :size="60" :src="doctor.avatar || defaultAvatar" />
                    <div class="doctor-info">
                      <h4 class="doctor-name">{{ doctor.doctorName }}</h4>
                      <el-tag size="small" type="warning">{{ doctor.title }}</el-tag>
                    </div>
                    <el-button
                      :type="doctor.isCollected ? 'warning' : 'primary'"
                      :icon="doctor.isCollected ? StarFilled : Star"
                      circle
                      size="small"
                      @click.stop="toggleCollectDoctor(doctor)"
                    />
                  </div>

                  <!-- 医生信息 -->
                  <div class="doctor-body">
                    <div class="info-row">
                      <span class="label">科室:</span>
                      <span class="value">{{ doctor.deptName }}</span>
                    </div>
                    <div v-if="doctor.specialty" class="info-row">
                      <span class="label">擅长:</span>
                      <span class="value">{{ doctor.specialty }}</span>
                    </div>
                    <div v-if="doctor.scheduleTime" class="info-row">
                      <span class="label">坐诊:</span>
                      <span class="value">{{ doctor.scheduleTime }}</span>
                    </div>
                    <div v-if="doctor.consultationFee" class="info-row">
                      <span class="label">费用:</span>
                      <span class="value fee">¥{{ doctor.consultationFee }}</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <Empty v-else description="暂无医生信息" />

          <!-- 分页 -->
          <div v-if="doctorTotal > pageSize" class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[12, 24, 48]"
              :total="doctorTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getHospitalDetail } from '@/api/hospital'
import { getHospitalDoctorDepartments } from '@/api/department'
import { getHospitalDoctors, getHospitalDoctorsByDeptName } from '@/api/doctor'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import { formatHospitalLevel } from '@/utils/hospital'
import Empty from '@/components/common/Empty.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const hospital = ref(null)
const allDoctors = ref([])
const filteredDoctorsByBackend = ref([])  // 存储后端筛选返回的医生列表
const departments = ref([])
const isHospitalCollected = ref(false)

// 筛选和分页
const filterDeptName = ref('')
const filterTitle = ref('')  // 职称筛选
const currentPage = ref(1)
const pageSize = ref(12)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 将医院等级转换为中文显示
const displayLevel = computed(() => {
  if (!hospital.value) return ''
  return formatHospitalLevel(hospital.value.level)
})

// 提取所有职称（去重）
const titles = computed(() => {
  const titleSet = new Set()
  allDoctors.value.forEach(doctor => {
    if (doctor.title) {
      titleSet.add(doctor.title)
    }
  })
  return Array.from(titleSet).sort()
})

// 筛选和排序后的医生列表
const filteredDoctors = computed(() => {
  // 如果有后端筛选结果，使用后端返回的数据
  if (filteredDoctorsByBackend.value.length > 0) {
    let doctors = [...filteredDoctorsByBackend.value]
    return doctors
  }

  // 否则使用前端过滤
  let doctors = [...allDoctors.value]

  // 科室筛选
  if (filterDeptName.value) {
    doctors = doctors.filter(d => d.deptName === filterDeptName.value)
  }

  // 职称筛选
  if (filterTitle.value) {
    doctors = doctors.filter(d => d.title === filterTitle.value)
  }

  return doctors
})

// 处理医生卡片点击，跳转到医生详情页
const handleDoctorClick = (doctor) => {
  router.push(`/doctor/${doctor.id}`)
}


// 分页后的医生列表
const paginatedDoctors = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredDoctors.value.slice(start, end)
})

// 医生总数
const doctorTotal = computed(() => filteredDoctors.value.length)

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

    // 加载医院信息
    const res = await getHospitalDetail(id)
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

    // 并行加载科室和医生
    const [deptRes, doctorRes] = await Promise.all([
      getHospitalDoctorDepartments(id),
      getHospitalDoctors(id)
    ])

    // 处理科室数据
    departments.value = deptRes.data || []

    // 处理医生数据
    allDoctors.value = (doctorRes.data || []).map((item) => ({
      id: item.id,
      doctorName: item.doctorName || item.name,
      title: item.title,
      hospitalName: item.hospitalName,
      deptId: item.deptId,
      deptName: item.deptName || item.departmentName,
      specialty: item.specialty,
      scheduleTime: item.scheduleTime,
      consultationFee: item.consultationFee,
      rating: item.rating,
      reviewCount: item.reviewCount,
      avatar: item.avatar,
      isCollected: item.isCollected
    }))

    // 检查医院收藏状态
    const collectRes = await checkCollected(COLLECTION_TYPE.HOSPITAL, hospital.value.id)
    isHospitalCollected.value = collectRes.data

    // 检查所有医生的收藏状态
    await checkDoctorsCollectStatus()
  } catch (error) {
    console.error('加载医院详情失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 检查医生收藏状态
const checkDoctorsCollectStatus = async () => {
  try {
    const promises = allDoctors.value.map(doctor =>
      checkCollected(COLLECTION_TYPE.DOCTOR, doctor.id)
        .then(res => {
          doctor.isCollected = res.data
        })
        .catch(() => {
          doctor.isCollected = false
        })
    )
    await Promise.all(promises)
  } catch (error) {
    console.error('检查医生收藏状态失败:', error)
  }
}

// 切换医院收藏
const toggleCollectHospital = async () => {
  if (!hospital.value) return

  try {
    if (isHospitalCollected.value) {
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
    isHospitalCollected.value = !isHospitalCollected.value
  } catch (error) {
    console.error('切换收藏失败:', error)
    ElMessage.error('操作失败')
  }
}

// 切换医生收藏
const toggleCollectDoctor = async (doctor) => {
  try {
    if (doctor.isCollected) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: doctor.id
      })
      ElMessage.success('取消收藏成功')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: doctor.id
      })
      ElMessage.success('收藏成功')
    }
    doctor.isCollected = !doctor.isCollected
  } catch (error) {
    console.error('切换医生收藏失败:', error)
    ElMessage.error('操作失败')
  }
}

// 科室筛选（使用后端API按科室名称筛选）
const handleDeptFilter = async () => {
  currentPage.value = 1
  filteredDoctorsByBackend.value = []  // 清空后端筛选结果，改用前端筛选

  // 前端筛选逻辑在 computed 中处理
}

// 职称筛选（前端联合筛选）
const handleTitleFilter = () => {
  currentPage.value = 1
  // 前端筛选逻辑在 computed 中处理
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  // 滚动到医生列表顶部
  document.querySelector('.doctor-section')?.scrollIntoView({ behavior: 'smooth' })
}

// 每页数量变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

onMounted(() => {
  loadHospitalDetail()
})
</script>

<style scoped lang="scss">
.hospital-detail-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;

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

      .stats {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .doctor-section {
    .doctor-toolbar {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 4px;
    }

    .doctor-list {
      margin-bottom: 20px;
    }

    .doctor-card {
      height: 100%;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-4px);
      }

      .doctor-header {
        display: flex;
        gap: 12px;
        align-items: center;
        margin-bottom: 12px;

        .doctor-info {
          flex: 1;

          .doctor-name {
            margin: 0 0 6px;
            font-size: 16px;
            color: #303133;
          }
        }
      }

      .doctor-body {
        margin-bottom: 12px;

        .info-row {
          display: flex;
          font-size: 13px;
          margin-bottom: 6px;

          .label {
            color: #909399;
            min-width: 50px;
          }

          .value {
            color: #606266;
            flex: 1;

            &.specialty {
              color: #409eff;
            }

            &.fee {
              color: #f56c6c;
              font-weight: 600;
            }
          }
        }
      }

    }

    .pagination-container {
      display: flex;
      justify-content: center;
      padding: 20px 0;
    }
  }
}

@media (max-width: 768px) {
  .hospital-detail-page {
    padding: 10px;

    .hospital-header {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .doctor-section {
      .doctor-toolbar {
        flex-direction: column;
        align-items: stretch;

        .el-select {
          width: 100% !important;
          margin-right: 0;
          margin-bottom: 8px;
        }
      }
    }
  }
}
</style>
