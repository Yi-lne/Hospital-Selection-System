<template>
  <div class="admin-report-manage">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">举报管理</span>
      </template>
    </el-page-header>

    <div class="content-wrapper">
      <div class="header">
      <div class="actions">
        <el-select
          v-model="filterStatus"
          placeholder="筛选状态"
          style="width: 150px; margin-right: 10px"
          @change="loadReports"
          clearable
        >
          <el-option label="全部" :value="null" />
          <el-option label="待处理" :value="0" />
          <el-option label="已处理" :value="1" />
        </el-select>
        <el-button @click="resetFilter">重置筛选</el-button>
      </div>
    </div>
    </div>

    <el-card class="table-card">
      <el-table
        :data="reports"
        v-loading="loading"
        border
        stripe
        @row-click="handleRowClick"
        style="cursor: pointer"
      >
      <el-table-column prop="id" label="编号" width="60" />
      <el-table-column prop="targetType" label="举报类型" width="90">
        <template #default="{ row }">
          <el-tag :type="getTargetTypeTagType(row.targetType)">
            {{ getTargetTypeName(row.targetType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="targetId" label="目标ID" width="70" />
      <el-table-column prop="reason" label="举报原因" min-width="150" />
      <el-table-column prop="nickname" label="举报人" width="120" />
      <el-table-column prop="createTime" label="举报时间" width="160">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusName(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handlerName" label="处理人" width="120" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button
            type="success"
            size="small"
            :disabled="row.status !== 0"
            @click.stop="showHandleDialog(row, 1)"
          >
            通过
          </el-button>
          <el-button
            type="warning"
            size="small"
            :disabled="row.status !== 0"
            @click.stop="showHandleDialog(row, 2)"
          >
            驳回
          </el-button>
          <el-button
            type="info"
            size="small"
            @click.stop="viewDetail(row)"
          >
            查看详情
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click.stop="deleteReport(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      style="margin-top: 20px; justify-content: center"
    />
    </el-card>

    <!-- 处理举报对话框 -->
    <el-dialog
      v-model="handleDialogVisible"
      :title="handleForm.handleType === 1 ? '通过举报' : '驳回举报'"
      width="500px"
    >
      <el-alert
        v-if="handleForm.handleType === 1"
        title="通过举报将自动删除违规内容"
        type="warning"
        :closable="false"
        style="margin-bottom: 16px"
      />
      <el-form :model="handleForm" label-width="100px">
        <el-form-item :label="handleForm.handleType === 1 ? '通过原因' : '驳回原因'">
          <el-input
            v-model="handleForm.handleResult"
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="举报详情"
      width="700px"
    >
      <el-descriptions :column="1" border v-if="currentReport">
        <el-descriptions-item label="举报ID">{{ currentReport.id }}</el-descriptions-item>
        <el-descriptions-item label="举报类型">
          <el-tag :type="getTargetTypeTagType(currentReport.targetType)">
            {{ getTargetTypeName(currentReport.targetType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标ID">{{ currentReport.targetId }}</el-descriptions-item>
        <el-descriptions-item label="举报原因">{{ currentReport.reason }}</el-descriptions-item>
        <el-descriptions-item label="举报人">{{ currentReport.nickname }}</el-descriptions-item>
        <el-descriptions-item label="举报时间">{{ formatTime(currentReport.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentReport.status)">
            {{ getStatusName(currentReport.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentReport.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ currentReport.handleResult || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'
import { getTopicDetail } from '@/api/community'

const router = useRouter()

const loading = ref(false)
const reports = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filterStatus = ref(null)

const handleDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const submitting = ref(false)
const currentReport = ref(null)

const handleForm = reactive({
  handleType: 1,
  handleResult: ''
})

const loadReports = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (filterStatus.value !== null) {
      params.status = filterStatus.value
    }
    const res = await adminApi.getReportList(params)
    reports.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载举报列表失败:', error)
    ElMessage.error('加载举报列表失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterStatus.value = null
  currentPage.value = 1
  loadReports()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadReports()
}

const handleSizeChange = (size) => {
  currentPage.value = 1
  pageSize.value = size
  loadReports()
}

const viewDetail = (row) => {
  currentReport.value = row
  detailDialogVisible.value = true
}

const showHandleDialog = (row, type) => {
  currentReport.value = row
  handleForm.handleType = type || 1
  handleForm.handleResult = ''
  handleDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.handleResult.trim()) {
    ElMessage.warning('请输入处理结果')
    return
  }

  try {
    submitting.value = true
    await adminApi.handleReport(currentReport.value.id, {
      status: handleForm.handleType, // 1=通过，2=驳回
      handleResult: handleForm.handleResult
    })

    if (handleForm.handleType === 1) {
      ElMessage.success('已通过举报并删除违规内容')
    } else {
      ElMessage.success('已驳回举报')
    }

    handleDialogVisible.value = false
    loadReports()
  } catch (error) {
    console.error('处理举报失败:', error)
    ElMessage.error(error.response?.data?.message || '处理举报失败')
  } finally {
    submitting.value = false
  }
}

const deleteReport = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条举报记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await adminApi.deleteReport(row.id)
    ElMessage.success('删除成功')
    loadReports()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除举报失败:', error)
      ElMessage.error('删除举报失败')
    }
  }
}

const getTargetTypeName = (type) => {
  const map = {
    1: '话题',
    2: '评论'
  }
  return map[type] || '未知'
}

const getTargetTypeTagType = (type) => {
  const map = {
    1: 'warning',
    2: 'info'
  }
  return map[type] || ''
}

const getStatusName = (status) => {
  const map = {
    0: '待处理',
    1: '已通过',
    2: '已驳回'
  }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return map[status] || ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 处理表格行点击
const handleRowClick = async (row) => {
  try {
    if (row.targetType === 1) {
      // 话题举报 - targetId 就是话题ID
      await getTopicDetail(row.targetId)
      // 跳转到话题详情页
      router.push(`/community/topic/${row.targetId}`)
    } else if (row.targetType === 2) {
      // 评论举报 - targetId 是评论ID，topicId 是话题ID
      if (!row.topicId) {
        ElMessage.warning('无法定位到所属话题')
        return
      }

      // 先验证话题是否存在
      await getTopicDetail(row.topicId)

      // 跳转到话题详情页，并传递评论ID用于定位
      router.push({
        path: `/community/topic/${row.topicId}`,
        query: { commentId: row.targetId }
      })
    }
  } catch (error) {
    console.error('内容不存在或已被删除:', error)
    ElMessage.warning('该内容已被删除，无法查看')
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped lang="scss">
.admin-report-manage {
  padding: 20px;

  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-wrapper {
    margin-top: 20px;
  }

  .table-card {
    margin-bottom: 20px;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .actions {
      display: flex;
      gap: 10px;
    }
  }

  :deep(.el-pagination) {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
