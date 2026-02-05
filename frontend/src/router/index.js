import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { setupRouterGuards } from './guards'
import { nextTick } from 'vue'

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的位置（比如浏览器后退），返回该位置
    if (savedPosition) {
      return savedPosition
    }

    // 返回顶部，使用 nextTick 确保 DOM 已准备好
    return new Promise((resolve) => {
      // 等待 DOM 更新完成
      nextTick(() => {
        setTimeout(() => {
          resolve({ top: 0, behavior: 'smooth' })
        }, 100)
      })
    })
  }
})

/**
 * 设置路由
 */
export function setupRouter(app) {
  // 设置路由守卫
  setupRouterGuards(router)

  app.use(router)
}

export default router
