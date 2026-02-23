import Request from '@/utils/request'

/**
 * 用户登录
 * 后端返回: { token: string, userInfo: User }
 */
export function login(data) {
  return Request.post('/user/login', data)
}

/**
 * 用户注册
 * 后端返回: { token: string, userInfo: User }
 */
export function register(data) {
  return Request.post('/user/register', data)
}

/**
 * 用户登出
 */
export function logout() {
  return Request.post('/user/logout')
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return Request.get('/user/info')
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return Request.put('/user/info', data)
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return Request.put('/user/password', data)
}

/**
 * 重置密码
 * 后端期望: { phone: string, verificationCode: string, newPassword: string }
 */
export function resetPassword(data) {
  return Request.post('/user/password/reset', data)
}

/**
 * 上传头像
 */
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return Request.upload('/user/avatar', formData)
}

/**
 * 生成图片验证码
 */
export function generateCaptcha() {
  return Request.post('/captcha/generate')
}

/**
 * 验证图片验证码（不删除，用于滑块验证）
 */
export function verifyCaptcha(data) {
  return Request.post('/captcha/verify', data)
}

/**
 * 验证并消耗验证码（用于登录/注册）
 */
export function verifyAndConsumeCaptcha(data) {
  return Request.post('/captcha/verify-consume', data)
}

// 导出验证码API对象
export const captchaApi = {
  generate: generateCaptcha,
  verify: verifyCaptcha,
  verifyAndConsume: verifyAndConsumeCaptcha
}
