import Request from '@/utils/request'

/**
 * 获取医院医生列表
 */
export function getHospitalDoctors(hospitalId, params) {
  return Request.get(`/doctor/hospital/${hospitalId}`, { params })
}

/**
 * 根据科室名称筛选医院医生列表
 */
export function getHospitalDoctorsByDeptName(hospitalId, deptName) {
  return Request.get(`/doctor/hospital/${hospitalId}/dept/${deptName}`)
}

/**
 * 获取医生详情
 */
export function getDoctorDetail(doctorId) {
  return Request.get(`/doctor/${doctorId}`)
}

/**
 * 搜索医生
 */
export function searchDoctors(keyword, params) {
  return Request.get('/doctor/search', { params: { keyword, ...params } })
}

/**
 * 医生列表（分页）
 */
export function getDoctorList(params) {
  return Request.get('/doctor/list', { params })
}

/**
 * 医生筛选
 */
export function filterDoctors(dto) {
  return Request.post('/doctor/filter', dto)
}

