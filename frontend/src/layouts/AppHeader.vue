<template>
  <el-header class="app-header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <el-icon :size="28"><OfficeBuilding /></el-icon>
        <span class="title">医院选择系统</span>
      </div>

      <!-- 桌面端导航 -->
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        class="nav-menu desktop-menu"
        router
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/hospital">医院</el-menu-item>
        <el-menu-item index="/community">社区</el-menu-item>
        <el-sub-menu v-if="userStore.isAdmin" index="admin">
          <template #title>管理</template>
          <el-menu-item index="/admin/hospital">医院管理</el-menu-item>
          <el-menu-item index="/admin/user">用户管理</el-menu-item>
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

        <!-- 移动端菜单按钮 -->
        <el-button
          class="mobile-menu-btn"
          :icon="Menu"
          circle
          @click="mobileMenuVisible = true"
        />

        <!-- 分隔线 -->
        <div class="divider"></div>

        <!-- 未登录状态 -->
        <template v-if="!userStore.isLogin">
          <el-button class="desktop-only" type="primary" @click="$router.push('/auth/login')">登录</el-button>
          <el-button class="desktop-only" @click="$router.push('/auth/register')">注册</el-button>
        </template>

        <!-- 已登录状态 -->
        <template v-else>
          <!-- 通知中心 -->
          <el-badge :value="notificationStore.unreadCount" :hidden="notificationStore.unreadCount === 0" class="notification-badge">
            <el-button :icon="Bell" circle @click="$router.push('/user/notifications')" />
          </el-badge>

          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userAvatar || defaultAvatar">
                {{ userStore.userName.charAt(0) }}
              </el-avatar>
              <span class="username desktop-only">{{ userStore.userName }}</span>
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
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>

    <!-- 移动端侧边菜单 -->
    <el-drawer v-model="mobileMenuVisible" direction="rtl" size="280px" class="mobile-drawer">
      <template #header>
        <div class="drawer-header">
          <span>菜单</span>
        </div>
      </template>
      <el-menu
        :default-active="activeMenu"
        class="mobile-nav-menu"
        router
        @select="mobileMenuVisible = false"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>首页
        </el-menu-item>
        <el-menu-item index="/hospital">
          <el-icon><OfficeBuilding /></el-icon>医院
        </el-menu-item>
        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>社区
        </el-menu-item>
        <el-sub-menu v-if="userStore.isAdmin" index="admin">
          <template #title>
            <el-icon><Setting /></el-icon>管理
          </template>
          <el-menu-item index="/admin/hospital">医院管理</el-menu-item>
          <el-menu-item index="/admin/user">用户管理</el-menu-item>
          <el-menu-item index="/admin/report">举报管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-drawer>
  </el-header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore, useNotificationStore } from '@/stores'
import { ElMessage } from 'element-plus'
import { Bell, User, Star, Document, Notebook, SwitchButton, Search, Menu, HomeFilled, ChatDotRound, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const notificationStore = useNotificationStore()

const searchKeyword = ref('')
const mobileMenuVisible = ref(false)
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
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/home')
      break
  }
}

// 初始化时启动轮询
onMounted(() => {
  if (userStore.isLogin) {
    notificationStore.startPolling()
  }
})

// 组件卸载时停止轮询
onUnmounted(() => {
  notificationStore.stopPolling()
})

// 监听登录状态变化，登录后启动轮询，退出后停止轮询
watch(() => userStore.isLogin, (isLoggedIn) => {
  if (isLoggedIn) {
    notificationStore.startPolling()
  } else {
    notificationStore.stopPolling()
  }
})
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
  flex-shrink: 0;

  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    white-space: nowrap;
  }
}

.nav-menu {
  flex: 1;
  border: none;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    padding: 0 20px;
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.search-input {
  width: 280px;

  @media (max-width: 1200px) {
    width: 200px;
  }
}

.mobile-menu-btn {
  display: none;
}

.desktop-menu {
  @media (max-width: 992px) {
    display: none;
  }
}

.desktop-only {
  @media (max-width: 768px) {
    display: none;
  }
}

.divider {
  width: 1px;
  height: 24px;
  background: #e4e7ed;
  margin: 0 8px;

  @media (max-width: 768px) {
    display: none;
  }
}

.notification-badge {
  @media (max-width: 768px) {
    margin-right: 0;
  }
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

// 移动端抽屉样式
.mobile-drawer {
  :deep(.el-drawer__header) {
    margin-bottom: 0;
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
  }

  .drawer-header {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  .mobile-nav-menu {
    border: none;

    .el-menu-item,
    .el-sub-menu__title {
      font-size: 16px;
      padding: 16px 20px;
      height: auto;
      line-height: 1.5;
    }

    .el-icon {
      margin-right: 8px;
    }
  }
}

// 响应式断点
@media (max-width: 992px) {
  .header-content {
    gap: 20px;
  }

  .mobile-menu-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .app-header {
    height: 56px;
  }

  .header-content {
    padding: 0 12px;
    gap: 12px;
  }

  .logo {
    :deep(.el-icon) {
      font-size: 24px !important;
    }

    .title {
      font-size: 16px;
    }
  }

  .search-input {
    width: 140px;

    :deep(.el-input__inner) {
      font-size: 14px;
    }

    :deep(.el-input-group__append) {
      padding: 0 8px;

      .el-button {
        padding: 5px;
      }
    }
  }

  .header-actions {
    gap: 8px;
  }

  .user-info {
    padding: 4px;

    .username {
      display: none;
    }
  }
}

@media (max-width: 480px) {
  .search-input {
    width: 100px;
  }

  .logo .title {
    display: none;
  }
}
</style>
