const TOKEN_KEY = 'hospital_token'
const USER_INFO_KEY = 'hospital_user_info'

/**
 * Token管理工具
 */
export class AuthManager {
  /**
   * 获取Token
   */
  static getToken() {
    return localStorage.getItem(TOKEN_KEY)
  }

  /**
   * 设置Token
   */
  static setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
  }

  /**
   * 删除Token
   */
  static removeToken() {
    localStorage.removeItem(TOKEN_KEY)
  }

  /**
   * 检查是否已登录
   */
  static isLoggedIn() {
    const token = this.getToken()
    if (!token) return false

    // 检查Token是否过期
    try {
      const payload = this.parseToken(token)
      const now = Date.now() / 1000
      return payload.exp > now
    } catch {
      return false
    }
  }

  /**
   * 解析JWT Token
   */
  static parseToken(token) {
    try {
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      )
      return JSON.parse(jsonPayload)
    } catch (error) {
      throw new Error('Invalid token')
    }
  }

  /**
   * 获取Token中的用户ID
   */
  static getUserIdFromToken() {
    try {
      const token = this.getToken()
      if (!token) return null
      const payload = this.parseToken(token)
      return payload.userId || null
    } catch {
      return null
    }
  }

  /**
   * 保存用户信息
   */
  static setUserInfo(userInfo) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
  }

  /**
   * 获取用户信息
   */
  static getUserInfo() {
    const userInfoStr = localStorage.getItem(USER_INFO_KEY)
    if (userInfoStr) {
      try {
        return JSON.parse(userInfoStr)
      } catch {
        return null
      }
    }
    return null
  }

  /**
   * 删除用户信息
   */
  static removeUserInfo() {
    localStorage.removeItem(USER_INFO_KEY)
  }

  /**
   * 清除所有认证信息
   */
  static clearAuth() {
    this.removeToken()
    this.removeUserInfo()
  }
}

export default AuthManager
