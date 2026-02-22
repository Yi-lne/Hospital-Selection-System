import axios from 'axios'
import { ElMessage } from 'element-plus'
import { AuthManager } from './auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({
  showSpinner: false,
  trickleSpeed: 200
})

/**
 * Axios请求配置
 */
const config = {
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  withCredentials: false
}

/**
 * 创建Axios实例
 */
const instance = axios.create(config)

/**
 * 请求拦截器
 */
instance.interceptors.request.use(
  (config) => {
    // 显示进度条
    NProgress.start()

    // 添加Token
    const token = AuthManager.getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
instance.interceptors.response.use(
  (response) => {
    NProgress.done()

    const { code, message, data } = response.data

    // 成功响应
    if (code === 200 || code === 0) {
      return response.data
    }

    // 业务错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    NProgress.done()

    const { response } = error

    if (response) {
      const { status, data } = response

      switch (status) {
        case 401:
          // 未授权，清除Token并跳转登录
          ElMessage.error('登录已过期，请重新登录')
          AuthManager.clearAuth()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

/**
 * 封装请求方法
 */
class Request {
  /**
   * GET请求
   */
  static get(url, config) {
    return instance.get(url, config)
  }

  /**
   * POST请求
   */
  static post(url, data, config) {
    return instance.post(url, data, config)
  }

  /**
   * PUT请求
   */
  static put(url, data, config) {
    return instance.put(url, data, config)
  }

  /**
   * DELETE请求
   */
  static delete(url, config) {
    return instance.delete(url, config)
  }

  /**
   * 文件上传
   */
  static upload(url, formData, config) {
    return instance.post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers
      }
    })
  }

  /**
   * 文件下载
   */
  static download(url, config) {
    return instance.get(url, {
      ...config,
      responseType: 'blob'
    })
  }
}

export default Request
export { instance as axios }
