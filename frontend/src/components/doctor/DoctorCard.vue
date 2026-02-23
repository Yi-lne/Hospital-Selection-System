<template>
  <el-card class="doctor-card" shadow="hover">
    <!-- 医生头部 -->
    <div class="doctor-header" @click="handleClick">
      <el-avatar :size="60" :src="doctor.avatar || defaultAvatar">
        {{ doctor.doctorName?.charAt(0) || '医' }}
      </el-avatar>
      <div class="doctor-info">
        <h4>{{ doctor.doctorName }}</h4>
        <p class="title">{{ doctor.title || '医师' }}</p>
        <p class="department">{{ doctor.hospitalName }} {{ doctor.deptName }}</p>
      </div>
      <el-button
        :type="isCollected ? 'warning' : 'primary'"
        :icon="isCollected ? StarFilled : Star"
        circle
        size="small"
        @click.stop="toggleCollect"
      />
    </div>

    <!-- 医生详情 -->
    <div class="doctor-details">
      <div class="detail-item">
        <span class="label">擅长：</span>
        <span class="value">{{ doctor.specialty || '暂无' }}</span>
      </div>
      <div class="detail-item">
        <span class="label">坐诊时间：</span>
        <span class="value">{{ doctor.scheduleTime || '暂无' }}</span>
      </div>
      <div class="detail-item">
        <span class="label">挂号费：</span>
        <span class="value fee">¥{{ doctor.consultationFee || '0' }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores'
import { checkCollected, addCollection, cancelCollection } from '@/api/collection'

const props = defineProps({
  doctor: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click', 'collection-change'])

const router = useRouter()
const userStore = useUserStore()
const isCollected = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 收藏类型常量（1=医院，2=医生，3=话题）
const COLLECTION_TYPE = {
  HOSPITAL: 1,
  DOCTOR: 2,
  TOPIC: 3
}

// 检查收藏状态
const checkCollectStatus = async () => {
  if (!userStore.isLogin) {
    isCollected.value = false
    return
  }

  try {
    const res = await checkCollected(COLLECTION_TYPE.DOCTOR, props.doctor.id)
    isCollected.value = res.data
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    isCollected.value = false
  }
}

// 切换收藏状态
const toggleCollect = async () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    router.push('/auth/login')
    return
  }

  try {
    if (isCollected.value) {
      await cancelCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: props.doctor.id
      })
      ElMessage.success('取消收藏成功')
      emit('collection-change')
    } else {
      await addCollection({
        targetType: COLLECTION_TYPE.DOCTOR,
        targetId: props.doctor.id
      })
      ElMessage.success('收藏成功')
      isCollected.value = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleClick = () => {
  emit('click', props.doctor)
}

onMounted(() => {
  checkCollectStatus()
})
</script>

<style scoped lang="scss">
.doctor-card {
  .doctor-header {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    cursor: pointer;

    .doctor-info {
      flex: 1;

      h4 {
        margin: 0 0 6px;
        font-size: 15px;
        font-weight: 600;
        color: #303133;
      }

      .title {
        margin: 0 0 3px;
        font-size: 13px;
        color: #67c23a;
      }

      .department {
        margin: 0;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .doctor-details {
    margin-bottom: 12px;

    .detail-item {
      display: flex;
      margin-bottom: 6px;
      font-size: 13px;

      .label {
        color: #909399;
        min-width: 80px;
      }

      .value {
        color: #606266;
        flex: 1;

        &.fee {
          color: #f56c6c;
          font-weight: 600;
        }
      }
    }
  }
}
</style>
