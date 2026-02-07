import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './assets/styles/index.scss'

import App from './App.vue'
import router, { setupRouter } from './router'
import pinia from './stores'

// 自定义中文配置
const customZhCn = {
  ...zhCn,
  el: {
    ...zhCn.el,
    pagination: {
      goto: '前往',
      pagesize: '条/页',
      total: '总共 {total} 条',
      pageClassifier: '页',
    }
  }
}

// 创建应用实例
const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 安装插件
app.use(ElementPlus, {
  locale: customZhCn,
})
app.use(pinia)
setupRouter(app)

// 挂载应用
app.mount('#app')
