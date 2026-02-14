<template>
  <el-header class="app-header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <el-icon :size="32"><OfficeBuilding /></el-icon>
        <span class="title">医院选择系统</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu"
        router
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/hospital">医院</el-menu-item>
        <el-menu-item index="/community">社区</el-menu-item>
        <el-sub-menu v-if="userStore.isAdmin" index="admin">
          <template #title>管理</template>
          <el-menu-item index="/admin/hospital">医院管理</el-menu-item>
          <el-menu-item index="/admin/report">举报管理</el-menu-item>
        </el-sub-menu>
      </el-menu>

      <div class="header-actions">
        <!-- 搜索框 -->
        <el-input
          v-model="searchKeyword"
          placeholder="搜索医院/医生"
          prefix-icon="Search"
          class="search-input"
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>

        <!-- 分隔线 -->
        <div class="divider"></div>

        <!-- 未登录状态 -->
        <template v-if="!userStore.isLogin">
          <el-button type="primary" @click="$router.push('/auth/login')">登录</el-button>
          <el-button @click="$router.push('/auth/register')">注册</el-button>
        </template>

        <!-- 已登录状态 -->
        <template v-else>
          <!-- 消息通知 -->
          <el-badge :value="messageStore.unreadCount" :hidden="messageStore.unreadCount === 0" class="message-badge">
            <el-button :icon="Message" circle @click="$router.push('/message/conversations')" />
          </el-badge>

          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userAvatar || defaultAvatar">
                {{ userStore.userName.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人资料
                </el-dropdown-item>
                <el-dropdown-item command="collection">
                  <el-icon><Star /></el-icon>我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="topics">
                  <el-icon><Document /></el-icon>我的话题
                </el-dropdown-item>
                <el-dropdown-item command="medical-history">
                  <el-icon><Notebook /></el-icon>病史管理
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" divided command="admin">
                  <el-icon><OfficeBuilding /></el-icon>管理后台
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, useMessageStore } from '@/stores'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, Message, User, Star, Document, Notebook, SwitchButton, Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const messageStore = useMessageStore()

const searchKeyword = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/hospital')) return '/hospital'
  if (path.startsWith('/community')) return '/community'
  if (path.startsWith('/admin')) return 'admin'
  if (path === '/') return '/home'
  return path
})

const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  // 跳转到搜索结果页，支持搜索医院和医生
  router.push({
    path: '/search',
    query: { keyword: searchKeyword.value }
  })
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'collection':
      router.push('/user/collection')
      break
    case 'topics':
      router.push('/user/topics')
      break
    case 'medical-history':
      router.push('/user/medical-history')
      break
    case 'admin':
      router.push('/admin/hospital')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/home')
      break
  }
}
</script>

<style scoped lang="scss">
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  color: #409eff;

  .title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.nav-menu {
  flex: 1;
  border: none;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 400px;
}

.divider {
  width: 1px;
  height: 24px;
  background: #e4e7ed;
  margin: 0 8px;
}

.message-badge {
  margin-right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.3s;

  &:hover {
    background: #f5f7fa;
  }

  .username {
    font-size: 14px;
    color: #606266;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
