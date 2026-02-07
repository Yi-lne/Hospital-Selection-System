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
          <h3>医生列表</h3>

          <!-- 医生筛选 -->
          <div class="doctor-filter">
            <!-- 科室筛选（只显示有医生的科室） -->
            <el-select
              v-if="departmentsWithDoctors && departmentsWithDoctors.length > 0"
              v-model="doctorFilter.deptId"
              placeholder="选择科室"
              clearable
              style="width: 180px"
              @change="handleDoctorFilter"
            >
              <el-option
                v-for="dept in departmentsWithDoctors"
                :key="dept.id"
                :label="dept.deptName"
                :value="dept.id"
              />
            </el-select>

            <!-- 职称筛选（始终显示） -->
            <el-select
              v-model="doctorFilter.title"
              placeholder="选择职称"
              clearable
              style="width: 180px"
              @change="handleDoctorFilter"
            >
              <el-option label="主任医师" value="主任医师" />
              <el-option label="副主任医师" value="副主任医师" />
              <el-option label="主治医师" value="主治医师" />
              <el-option label="住院医师" value="住院医师" />
            </el-select>

            <!-- 重置按钮（有筛选条件时显示） -->
            <el-button v-if="hasDoctorFilter" link type="primary" @click="resetDoctorFilter">
              重置筛选
            </el-button>
          </div>

          <!-- 医生卡片列表 -->
          <el-row v-if="doctors && doctors.length > 0" :gutter="16">
            <el-col v-for="doctor in doctors" :key="doctor.id" :xs="24" :sm="12" :md="8">
              <DoctorCard :doctor="doctor" />
            </el-col>
          </el-row>
          <Empty v-else description="暂无医生信息" />

          <!-- 分页 -->
          <div v-if="totalDoctors > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="doctorPagination.page"
              v-model:page-size="doctorPagination.pageSize"
              :total="totalDoctors"
              :page-sizes="[9, 18, 36]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handleDoctorPageChange"
              @size-change="handleDoctorPageSizeChange"
            />
          </div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getHospitalDetail } from '@/api/hospital'
import { filterDoctors } from '@/api/doctor'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'
import { formatHospitalLevel } from '@/utils/hospital'
import DoctorCard from '@/components/doctor/DoctorCard.vue'
import Empty from '@/components/common/Empty.vue'
import type { Hospital } from '@/types/hospital'
import type { DoctorSimple } from '@/types/doctor'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const hospital = ref<Hospital | null>(null)
const doctors = ref<DoctorSimple[]>([])
const totalDoctors = ref(0)  // 医生总数
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 医生分页状态
const doctorPagination = ref({
  page: 1,
  pageSize: 9
})

// 医生筛选条件
const doctorFilter = ref({
  deptId: undefined as number | undefined,
  title: undefined as string | undefined
})

// 判断是否有医生筛选条件
const hasDoctorFilter = computed(() => {
  return doctorFilter.value.deptId !== undefined || doctorFilter.value.title !== undefined
})

// 从医生列表中动态提取有医生的科室（用于科室筛选下拉框）
const departmentsWithDoctors = computed(() => {
  const deptMap = new Map<number | string, { id: number; deptName: string }>()

  doctors.value.forEach(doctor => {
    // 确保 deptId 有效（排除 null、undefined、空字符串）
    const deptId = doctor.deptId
    const deptName = doctor.deptName

    // 验证 deptId 和 deptName 都有效
    if (
      deptId !== null &&
      deptId !== undefined &&
      deptId !== '' &&
      deptName &&
      deptName.trim() !== '' &&
      !deptMap.has(deptId)
    ) {
      deptMap.set(deptId, {
        id: typeof deptId === 'string' ? parseInt(deptId) : deptId,
        deptName: deptName.trim()
      })
    }
  })

  // 转换为数组并按科室名称排序
  const deptArray = Array.from(deptMap.values())

  // 再次去重（按科室名称），确保完全不会重复
  const uniqueDepts: { id: number; deptName: string }[] = []
  const nameSet = new Set<string>()

  deptArray.forEach(dept => {
    if (!nameSet.has(dept.deptName)) {
      nameSet.add(dept.deptName)
      uniqueDepts.push(dept)
    }
  })

  // 按科室名称拼音排序
  return uniqueDepts.sort((a, b) =>
    a.deptName.localeCompare(b.deptName, 'zh-CN')
  )
})

// 处理医生筛选变化
const handleDoctorFilter = () => {
  // 重置到第一页
  doctorPagination.value.page = 1
  // 重新加载医生列表
  loadDoctors()
}

// 重置医生筛选
const resetDoctorFilter = () => {
  doctorFilter.value = {
    deptId: undefined,
    title: undefined
  }
  // 重置到第一页
  doctorPagination.value.page = 1
  // 重新加载医生列表
  loadDoctors()
}

// 分页大小改变
const handleDoctorPageSizeChange = (pageSize: number) => {
  doctorPagination.value.pageSize = pageSize
  doctorPagination.value.page = 1
  loadDoctors()
}

// 当前页改变
const handleDoctorPageChange = (page: number) => {
  doctorPagination.value.page = page
  loadDoctors()
}

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

// 加载医生列表
const loadDoctors = async () => {
  if (!hospital.value) return

  try {
    const params = {
      hospitalId: hospital.value.id,
      page: doctorPagination.value.page,
      pageSize: doctorPagination.value.pageSize
    }

    // 添加筛选条件
    if (doctorFilter.value.deptId !== undefined) {
      params.deptId = doctorFilter.value.deptId
    }
    if (doctorFilter.value.title) {
      params.title = doctorFilter.value.title
    }

    const res = await filterDoctors(params)

    // 映射医生字段名
    doctors.value = (res.data.list || []).map((item: any) => ({
      id: item.id,
      doctorName: item.doctorName || item.name,
      title: item.title,
      hospitalName: item.hospitalName,
      deptName: item.deptName || item.departmentName,
      deptId: item.deptId,
      specialty: item.specialty,
      avatar: item.avatar
    }))

    totalDoctors.value = res.data.total || 0
  } catch (error) {
    console.error('加载医生列表失败:', error)
  }
}

// 加载医院详情
const loadHospitalDetail = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)

    // 验证 ID 是否有效
    if (!id || isNaN(id)) {
      ElMessage.error('无效的医院ID')
      router.push('/hospital')
      return
    }

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

    // 加载医生列表（第一页）
    await loadDoctors()

    // 检查收藏状态
    if (hospital.value) {
      const collectRes = await checkCollected(COLLECTION_TYPE.HOSPITAL, hospital.value.id)
      isCollected.value = collectRes.data
    }
  } catch (error) {
    console.error('加载医院详情失败:', error)
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
    console.error('收藏操作失败:', error)
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

    .doctor-filter {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 4px;
    }

    .pagination-wrapper {
      margin-top: 24px;
      display: flex;
      justify-content: center;
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
