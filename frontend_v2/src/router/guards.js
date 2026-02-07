import { useUserStore } from '@/stores'
import { AuthManager } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

/**
 * 设置路由守卫
 */
export function setupRouterGuards(router) {
  // 前置守卫
  router.beforeEach((to, from, next) => {
    try {
      NProgress.start()

      // 设置页面标题
      if (to.meta.title) {
        document.title = `${to.meta.title} - 医院选择系统`
      }

      // 检查是否需要登录
      const requiresAuth = to.meta.requiresAuth
      const requiresAdmin = to.meta.requiresAdmin
      const userStore = useUserStore()
      const isLogin = AuthManager.isLoggedIn()

      if (requiresAuth && !isLogin) {
        // 需要登录但未登录，跳转到登录页
        next({
          name: 'Login',
          query: { redirect: to.fullPath }
        })
      } else if (requiresAdmin) {
        // 需要管理员权限
        if (!isLogin) {
          next({
            name: 'Login',
            query: { redirect: to.fullPath }
          })
        } else if (!userStore.isAdmin) {
          // 用户已登录但不是管理员，跳转到首页
          console.warn('用户尝试访问管理员页面，但无管理员权限')
          next({ name: 'Home' })
        } else {
          next()
        }
      } else if ((to.name === 'Login' || to.name === 'Register') && isLogin) {
        // 已登录用户访问登录/注册页，跳转到首页
        next({ name: 'Home' })
      } else {
        next()
      }
    } catch (error) {
      console.error('路由前置守卫错误:', error)
      NProgress.done()
      next() // 确保即使出错也继续导航
    }
  })

  // 后置守卫
  router.afterEach((to, from) => {
    try {
      NProgress.done()
    } catch (error) {
      console.error('路由后置守卫错误:', error)
    }
  })

  // 错误处理
  router.onError((error) => {
    console.error('路由错误:', error)
    NProgress.done()

    // 如果是 DOM 相关错误，通常是因为组件还未挂载，可以忽略
    if (error instanceof Error && error.message.includes('parentNode')) {
      console.warn('忽略DOM未就绪错误（路由导航中）')
      return
    }

    // 其他错误也可以选择显示用户友好的提示
    // 这里只是打印日志，不中断应用
  })
}
