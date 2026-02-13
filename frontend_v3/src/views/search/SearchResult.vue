<template>
  <div class="search-result-page">
    <div class="page-container">
      <el-page-header @back="$router.back()">
        <template #content>
          <span class="page-title">搜索结果</span>
        </template>
      </el-page-header>

      <div class="search-keyword">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="keyword-text">搜索关键词：</span>
        <span class="keyword">{{ keyword }}</span>
      </div>

      <!-- Tabs切换 -->
      <el-card class="tabs-card" shadow="never">
        <el-tabs v-model="activeTab" class="search-tabs">
          <el-tab-pane :name="hospitalTabName">
            <template #label>
              <span class="tab-label">
                <el-icon><OfficeBuilding /></el-icon>
                <span>医院</span>
                <span v-if="hospitalTotal > 0" class="tab-count">({{ hospitalTotal }})</span>
              </span>
            </template>
            <HospitalSearchResult :keyword="keyword" @update-total="hospitalTotal = $event" />
          </el-tab-pane>
          <el-tab-pane :name="doctorTabName">
            <template #label>
              <span class="tab-label">
                <el-icon><User /></el-icon>
                <span>医生</span>
                <span v-if="doctorTotal > 0" class="tab-count">({{ doctorTotal }})</span>
              </span>
            </template>
            <DoctorSearchResult :keyword="keyword" @update-total="doctorTotal = $event" />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Search, OfficeBuilding, User } from '@element-plus/icons-vue'
import HospitalSearchResult from './components/HospitalSearchResult.vue'
import DoctorSearchResult from './components/DoctorSearchResult.vue'

const route = useRoute()
const activeTab = ref('hospital')
const hospitalTotal = ref(0)
const doctorTotal = ref(0)
const hospitalTabName = 'hospital'
const doctorTabName = 'doctor'

// 使用computed直接从route获取keyword，确保组件立即获取到值
const keyword = computed(() => route.query.keyword || '')
</script>

<style scoped lang="scss">
.search-result-page {
  min-height: calc(100vh - 120px);
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.search-keyword {
  margin: 24px 0;
  padding: 18px 24px;
  background: #fff;
  border-radius: 8px;
  border-left: 4px solid #409eff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 8px;

  .search-icon {
    font-size: 20px;
    color: #409eff;
  }

  .keyword-text {
    font-weight: 500;
  }

  .keyword {
    color: #409eff;
    font-weight: 600;
    font-size: 16px;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.tabs-card {
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-card__body) {
    padding: 0;
  }
}

.search-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 24px;
    background: #fafbfc;
    border-bottom: 2px solid #e4e7ed;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    padding: 0 24px;
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    color: #606266;
    border: none;
    transition: all 0.3s;

    &:hover {
      color: #409eff;
    }

    &.is-active {
      color: #409eff;
      font-weight: 600;
      border-bottom: 2px solid #409eff;
    }
  }

  :deep(.el-tabs__content) {
    padding: 24px 0 0 0;
  }

  .tab-label {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 15px;

    .el-icon {
      font-size: 18px;
    }
  }

  .tab-count {
    color: #909399;
    font-size: 13px;
    font-weight: normal;
  }
}
</style>
