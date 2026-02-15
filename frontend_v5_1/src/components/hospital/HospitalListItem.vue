<template>
  <div class="hospital-list-item" @click="handleClick">
    <div class="item-header">
      <div class="hospital-info">
        <h3 class="hospital-name">
          {{ hospital.hospitalName || hospital.name }}
          <el-tag v-if="displayLevel" type="danger" size="small">
            {{ displayLevel }}
          </el-tag>
        </h3>
        <div class="location">
          <el-icon><Location /></el-icon>
          <span>{{ hospital.cityName || hospital.city }} {{ hospital.areaName || hospital.area }}</span>
        </div>
      </div>
      <div v-if="showRating" class="rating">
        <el-rate
          v-model="ratingValue"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value}"
        />
        <span class="review-count">{{ hospital.reviewCount || 0 }}条评价</span>
      </div>
    </div>

    <div class="item-content">
      <div v-if="hospital.keyDepartments" class="departments">
        <span class="label">重点科室：</span>
        <el-tag
          v-for="(dept, index) in (hospital.keyDepartments || '').split(',').slice(0, 3)"
          :key="index"
          size="small"
          type="info"
        >
          {{ dept.trim() }}
        </el-tag>
      </div>
      <div v-if="hospital.address" class="address">
        <el-icon><Location /></el-icon>
        <span>{{ hospital.address }}</span>
      </div>
    </div>

    <div v-if="showActions" class="item-actions">
      <el-button type="primary" size="small" @click.stop="handleViewDetail">
        查看详情
      </el-button>
      <el-button size="small" @click.stop="handleCollect">
        <el-icon><Star /></el-icon>
        {{ isCollected ? '已收藏' : '收藏' }}
      </el-button>
    </div>

    <div v-if="showInsurance && hospital.isMedicalInsurance" class="insurance-tag">
      <el-tag type="success" effect="dark">医保定点</el-tag>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Location, Star } from '@element-plus/icons-vue'
import { formatHospitalLevel } from '@/utils/hospital'

const props = defineProps({
  hospital: {
    type: Object,
    required: true
  },
  showRating: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  showInsurance: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['click', 'detail', 'collect'])

const isCollected = ref(false)

const ratingValue = computed(() => {
  return props.hospital.rating || 0
})

// 将医院等级代码转换为中文显示
const displayLevel = computed(() => {
  const levelCode = props.hospital.hospitalLevel || props.hospital.level
  return formatHospitalLevel(levelCode)
})

const handleClick = () => {
  emit('click', props.hospital)
}

const handleViewDetail = () => {
  emit('detail', props.hospital)
}

const handleCollect = () => {
  isCollected.value = !isCollected.value
  emit('collect', props.hospital, isCollected.value)
}
</script>

<style scoped lang="scss">
.hospital-list-item {
  position: relative;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .hospital-info {
      flex: 1;

      .hospital-name {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0 0 8px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .location {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: #909399;
      }
    }

    .rating {
      text-align: right;

      .review-count {
        display: block;
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .item-content {
    margin-bottom: 16px;

    .departments {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      font-size: 13px;

      .label {
        color: #909399;
      }

      .el-tag {
        margin-right: 4px;
      }
    }

    .address {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: #606266;
    }
  }

  .item-actions {
    display: flex;
    gap: 8px;
  }

  .insurance-tag {
    position: absolute;
    top: 20px;
    right: 20px;
  }
}
</style>
