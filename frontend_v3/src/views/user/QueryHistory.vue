<template>
  <div class="query-history-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">查询历史</span>
      </template>
    </el-page-header>

    <el-card v-loading="loading" class="history-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <el-select v-model="filterType" placeholder="筛选类型" clearable @change="loadHistory">
          <el-option label="全部" value="" />
          <el-option label="医院" value="1" />
          <el-option label="医生" value="2" />
          <el-option label="话题" value="3" />
        </el-select>
        <el-button type="danger" :icon="Delete" @click="handleClearAll">
          清空历史
        </el-button>
      </div>

      <!-- 历史记录列表 -->
      <div v-if="historyList.length > 0" class="history-list">
        <div
          v-for="item in historyList"
          :key="item.id"
          class="history-item"
          @click="goToDetail(item)"
        >
          <div class="item-icon">
            <el-icon :size="24" :color="getTypeColor(item.queryType)">
              <component :is="getTypeIcon(item.queryType)" />
            </el-icon>
          </div>
          <div class="item-content">
            <div class="item-header">
              <h4>{{ getTypeLabel(item.queryType) }}</h4>
              <span class="time">{{ formatTime(item.createTime) }}</span>
            </div>
            <p v-if="item.queryParams" class="params">
              {{ formatParams(item.queryParams) }}
            </p>
          </div>
          <div class="item-action">
            <el-button
              type="danger"
              :icon="Delete"
              circle
              size="small"
              @click.stop="handleDelete(item.id)"
            />
          </div>
        </div>
      </div>
      <Empty v-else description="暂无查询历史" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadHistory"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, OfficeBuilding, User, ChatDotRound } from '@element-plus/icons-vue'
import { getQueryHistory, deleteQueryHistory, clearQueryHistory } from '@/api/query-history'
import { formatRelativeTime } from '@/utils/date'
import Empty from '@/components/common/Empty.vue'

const router = useRouter()

const loading = ref(false)
const historyList = ref([])
const total = ref(0)
const filterType = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10
})

// 格式化时间
const formatTime = (time) => {
  return formatRelativeTime(time)
}

// 获取类型图标
const getTypeIcon = (type) => {
  const icons = {
    1: OfficeBuilding,
    2: User,
    3: ChatDotRound
  }
  return icons[type] || OfficeBuilding
}

// 获取类型颜色
const getTypeColor = (type) => {
  const colors = {
    1: '#409eff',
    2: '#67c23a',
    3: '#e6a23c'
  }
  return colors[type] || '#909399'
}

// 获取类型标签
const getTypeLabel = (type) => {
  const labels = {
    1: '医院查询',
    2: '医生查询',
    3: '话题查询'
  }
  return labels[type] || '未知类型'
}

// 格式化查询参数
const formatParams = (params) => {
  if (!params) return ''
  try {
    const obj = typeof params === 'string' ? JSON.parse(params) : params
    const keywords = Object.keys(obj).filter(key => obj[key] && key !== 'page' && key !== 'pageSize')
    return keywords.slice(0, 3).map(key => `${key}: ${obj[key]}`).join(' | ')
  } catch {
    return ''
  }
}

// 加载历史记录
const loadHistory = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (filterType.value) {
      params.queryType = filterType.value
    }
    const res = await getQueryHistory(params)
    historyList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to load query history:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到详情
const goToDetail = (item) => {
  const routes = {
    1: `/hospital/${item.targetId}`,
    2: `/doctor/${item.targetId}`,
    3: `/community/topic/${item.targetId}`
  }
  const route = routes[item.queryType]
  if (route && item.targetId) {
    router.push(route)
  }
}

// 删除单条记录
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条历史记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteQueryHistory(id)
    ElMessage.success('删除成功')
    loadHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete history:', error)
    }
  }
}

// 清空所有历史
const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm('确定清空所有查询历史吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearQueryHistory()
    ElMessage.success('清空成功')
    loadHistory()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to clear history:', error)
    }
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.page = 1
  pagination.pageSize = size
  loadHistory()
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped lang="scss">
.query-history-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .history-card {
    margin-top: 20px;
    min-height: 500px;
  }

  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .el-select {
      width: 150px;
    }
  }

  .history-list {
    .history-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 12px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e6f7ff;
        transform: translateX(4px);
      }

      .item-icon {
        flex-shrink: 0;
      }

      .item-content {
        flex: 1;
        min-width: 0;

        .item-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          h4 {
            margin: 0;
            font-size: 15px;
            font-weight: 500;
            color: #303133;
          }

          .time {
            font-size: 12px;
            color: #909399;
          }
        }

        .params {
          margin: 0;
          font-size: 13px;
          color: #606266;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .item-action {
        flex-shrink: 0;
      }
    }
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
