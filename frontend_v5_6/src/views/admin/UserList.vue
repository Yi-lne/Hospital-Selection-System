<template>
  <div class="admin-user-list">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">用户管理</span>
      </template>
    </el-page-header>

    <div class="content-wrapper">
      <div class="header">
        <div class="actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索手机号或昵称"
            style="width: 250px; margin-right: 10px"
            @keyup.enter="searchUsers"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="searchUsers">搜索</el-button>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <el-table :data="users" v-loading="loading" border stripe>
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="genderText" label="性别" width="80" align="center" />
        <el-table-column prop="avatar" label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar v-if="row.avatar" :src="row.avatar" :size="50" />
            <el-avatar v-else :size="50">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="封禁信息" width="250">
          <template #default="{ row }">
            <div v-if="row.status === 0" class="ban-info">
              <div style="font-size: 12px; color: #909399">
                开始：{{ row.banStartTime || '-' }}
              </div>
              <div style="font-size: 12px; color: #f56c6c">
                {{ row.banEndTime ? '结束：' + row.banEndTime : '永久封禁' }}
              </div>
              <div v-if="row.banReason" style="font-size: 12px; color: #606266; margin-top: 4px">
                原因：{{ row.banReason }}
              </div>
            </div>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="showBanDialog(row)"
            >
              封禁
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="unbanUser(row)"
            >
              解封
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadUsers"
        @current-change="loadUsers"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 封禁用户对话框 -->
    <el-dialog
      v-model="banDialogVisible"
      :title="`封禁用户 - ${currentUser?.nickname}`"
      width="500px"
    >
      <el-form :model="banForm" label-width="100px">
        <el-form-item label="封禁时长">
          <el-select v-model="banForm.durationType" placeholder="请选择封禁时长" style="width: 100%">
            <el-option label="1分钟（仅用于测试）" value="1_MINUTE" />
            <el-option label="2小时" value="2_HOURS" />
            <el-option label="1天" value="1_DAY" />
            <el-option label="7天" value="7_DAYS" />
            <el-option label="1个月" value="1_MONTH" />
            <el-option label="3个月" value="3_MONTHS" />
            <el-option label="永久封禁" value="PERMANENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="封禁原因">
          <el-input
            v-model="banForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入封禁原因（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="banDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBan">确定封禁</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const users = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 封禁对话框相关
const banDialogVisible = ref(false)
const currentUser = ref(null)
const banForm = ref({
  durationType: '',
  reason: ''
})

// 加载用户列表
const loadUsers = async () => {
  try {
    loading.value = true
    const res = await adminApi.getUsers({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    users.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索用户
const searchUsers = async () => {
  if (!searchKeyword.value.trim()) {
    loadUsers()
    return
  }
  try {
    loading.value = true
    const res = await adminApi.searchUsers(searchKeyword.value)
    users.value = res.data || []
    total.value = res.data?.length || 0
  } catch (error) {
    ElMessage.error('搜索失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 显示封禁对话框
const showBanDialog = (user) => {
  currentUser.value = user
  banForm.value = {
    durationType: '',
    reason: ''
  }
  banDialogVisible.value = true
}

// 确认封禁
const confirmBan = async () => {
  if (!banForm.value.durationType) {
    ElMessage.warning('请选择封禁时长')
    return
  }

  try {
    await adminApi.banUser(currentUser.value.id, {
      durationType: banForm.value.durationType,
      reason: banForm.value.reason || '违反社区规则'
    })
    ElMessage.success('用户已封禁')
    banDialogVisible.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error('封禁失败：' + (error.message || '未知错误'))
  }
}

// 封禁用户（保留此方法以防万一）
const banUser = async (user) => {
  showBanDialog(user)
}

// 解封用户
const unbanUser = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要解封用户"${user.nickname}"吗？`,
      '解封用户',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    await adminApi.unbanUser(user.id)
    ElMessage.success('用户已解封')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('解封失败：' + (error.message || '未知错误'))
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-user-list {
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
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.ban-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
</style>
