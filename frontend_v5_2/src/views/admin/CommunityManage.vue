<template>
  <div class="community-manage-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">举报管理</span>
      </template>
    </el-page-header>

    <el-card class="manage-card" v-loading="loading">
      <div class="tab-header">
        <el-select v-model="reportStatus" placeholder="处理状态" style="width: 150px" @change="loadReports">
          <el-option label="全部" :value="null" />
          <el-option label="待处理" :value="0" />
          <el-option label="已处理" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </div>

      <el-table :data="reports" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column label="举报对象" width="100">
          <template #default="{ row }">
            <el-tag :type="row.targetType === 1 ? 'primary' : 'success'" size="small">
              {{ row.targetTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="内容" width="250">
          <template #default="{ row }">
            <div class="content-cell">
              <span class="content-text">{{ getDisplayContent(row) }}</span>
              <el-icon
                v-if="row.targetTitle && row.targetTitle.length > 50"
                class="expand-icon"
                @click.stop="toggleExpand(row.id)"
              >
                <ArrowDown v-if="!expandedRows[row.id]" />
                <ArrowUp v-else />
              </el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="举报人" width="120" />
        <el-table-column prop="reasonTypeDesc" label="举报原因" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="举报时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" :disabled="row.status !== 0" @click="handleReport(row, 1)">通过</el-button>
              <el-button type="warning" size="small" :disabled="row.status !== 0" @click="handleReport(row, 2)">驳回</el-button>
              <el-button type="success" size="small" @click="navigateToTarget(row)">跳转查看</el-button>
              <el-button type="info" size="small" @click="viewReportDetail(row)">详情</el-button>
              <el-button type="danger" size="small" @click="deleteReport(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadReports"
        @size-change="loadReports"
      />
    </el-card>

    <!-- 举报处理对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理举报" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.status">
            <el-radio :value="1">通过（已处理）</el-radio>
            <el-radio :value="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input
            v-model="handleForm.handleResult"
            type="textarea"
            :rows="4"
            placeholder="请输入处理说明（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const reportStatus = ref(null)
const reports = ref([])
const total = ref(0)
const expandedRows = ref({})

const pagination = reactive({
  page: 1,
  pageSize: 20
})

const handleDialogVisible = ref(false)
const handleForm = reactive({
  reportId: null,
  status: 1,
  handleResult: ''
})

// 获取显示的内容（截断或完整）
const getDisplayContent = (row) => {
  const content = row.targetTitle || ''
  // 如果已展开，显示完整内容
  if (expandedRows.value[row.id]) {
    return content
  }
  // 如果未展开，截断显示
  const maxLength = 50
  return content.length > maxLength ? content.substring(0, maxLength) + '...' : content
}

// 切换展开/收起状态
const toggleExpand = (rowId) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId]
}

// 加载举报列表
const loadReports = async () => {
  try {
    loading.value = true
    const params = {
      status: reportStatus.value,
      page: pagination.page,
      pageSize: pagination.pageSize
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

// 跳转到被举报的内容
const navigateToTarget = (report) => {
  if (report.targetType === 1) {
    // 话题：跳转到话题详情页
    router.push(`/community/topic/${report.targetId}`)
  } else if (report.targetType === 2) {
    // 评论：跳转到话题详情页
    if (report.topicId) {
      // 后端已返回 topicId，直接跳转并定位到该评论
      router.push(`/community/topic/${report.topicId}?commentId=${report.targetId}`)
    } else {
      // 如果后端未返回 topicId，跳转到社区首页
      ElMessage.warning('无法跳转到该评论所在的话题')
      router.push('/community')
    }
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'success'
    case 2:
      return 'info'
    default:
      return ''
  }
}

// 处理举报
const handleReport = (report, status) => {
  handleForm.reportId = report.id
  handleForm.status = status
  handleForm.handleResult = ''
  handleDialogVisible.value = true
}

// 查看举报详情
const viewReportDetail = (report) => {
  const content = report.targetType === 1 ? '话题' : '评论'
  ElMessageBox.alert(
    `
    <div style="line-height: 1.8;">
      <p><strong>举报类型：</strong>${content}</p>
      <p><strong>举报对象：</strong>${report.targetTitle}</p>
      <p><strong>举报人：</strong>${report.nickname}</p>
      <p><strong>举报原因：</strong>${report.reasonTypeDesc}</p>
      <p><strong>详细说明：</strong>${report.reason || '无'}</p>
      <p><strong>当前状态：</strong>${report.statusDesc}</p>
      ${report.handleResult ? `<p><strong>处理结果：</strong>${report.handleResult}</p>` : ''}
      <p><strong>举报时间：</strong>${report.createTime}</p>
    </div>
    `,
    '举报详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 删除举报记录
const deleteReport = (report) => {
  ElMessageBox.confirm(
    '确定要删除这条举报记录吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await adminApi.deleteReport(report.id)
      ElMessage.success('删除成功')
      loadReports()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 确认处理举报
const confirmHandle = async () => {
  try {
    await adminApi.handleReport(handleForm.reportId, {
      status: handleForm.status,
      handleResult: handleForm.handleResult
    })
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    loadReports()
  } catch (error) {
    console.error('处理失败:', error)
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped lang="scss">
.community-manage-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .manage-card {
    margin-top: 20px;
  }

  .tab-header {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .content-cell {
    display: flex;
    align-items: center;
    gap: 8px;

    .content-text {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .expand-icon {
      flex-shrink: 0;
      cursor: pointer;
      font-size: 14px;
      color: #409eff;
      transition: transform 0.3s;

      &:hover {
        color: #66b1ff;
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 8px;
    flex-wrap: nowrap;
    justify-content: flex-end;

    .el-button {
      margin-left: 0 !important;
      margin-right: 0 !important;
    }
  }
}
</style>
