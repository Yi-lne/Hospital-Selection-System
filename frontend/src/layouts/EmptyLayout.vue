<template>
  <div class="empty-layout">
    <router-view v-slot="{ Component, route }">
      <transition name="fade" mode="out-in" @before-leave="onBeforeLeave">
        <component :is="Component" :key="route.path" v-if="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup lang="ts">
// 在离开前确保 DOM 更新完成
const onBeforeLeave = () => {
  return new Promise(resolve => {
    setTimeout(resolve, 0)
  })
}
</script>

<style scoped lang="scss">
.empty-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
