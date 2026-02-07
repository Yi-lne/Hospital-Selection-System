import Request from '@/utils/request'

/**
 * 获取医生列表（分页）
 */
export function getDoctorList(params) {
  return Request.get('/doctor/list', { params })
}

/**
 * 医生筛选（支持医院、科室、职称等筛选）
 */
export function filterDoctors(data) {
  return Request.post('/doctor/filter', data)
}

/**
 * 获取医生详情
 */
export function getDoctorDetail(id) {
  return Request.get(`/doctor/${id}`)
}

/**
 * 获取医院的医生列表（返回该医院所有医生，不分页）
 */
export function getHospitalDoctors(hospitalId) {
  return Request.get(`/doctor/hospital/${hospitalId}`)
}

/**
 * 获取科室的医生列表
 */
export function getDepartmentDoctors(params) {
  return Request.get('/doctor/department', { params })
}

/**
 * 搜索医生
 */
export function searchDoctors(keyword, params) {
  return Request.get('/doctor/search', { params: { keyword, ...params } })
}

/**
 * 获取医生搜索建议
 */
export function getDoctorSearchSuggestions(keyword) {
  return Request.get('/doctor/suggestions', { params: { keyword } })
}
