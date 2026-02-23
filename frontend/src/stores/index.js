import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia

// 导出所有store
export * from './modules/user'
export * from './modules/app'
export * from './modules/hospital'
export * from './modules/community'
export * from './modules/notification'
