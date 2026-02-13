<template>
  <el-container class="layout-container">
    <AppHeader />

    <el-main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition :name="transitionName" mode="out-in" @before-leave="onBeforeLeave">
          <component :is="Component" :key="route.path" v-if="Component" />
        </transition>
      </router-view>
    </el-main>

    <AppFooter />
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'

const route = useRoute()
const transitionName = ref('fade')

// 在离开前确保滚动位置重置
const onBeforeLeave = () => {
  // 等待 DOM 更新完成
  return new Promise(resolve => {
    setTimeout(resolve, 0)
  })
}
</script>

<style scoped lang="scss">
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

// 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
