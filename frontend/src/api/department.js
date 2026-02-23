import Request from '@/utils/request'

/**
 * 获取所有科室列表
 */
export function getAllDepartments() {
  return Request.get('/department/all')
}

/**
 * 获取医院的科室列表
 */
export function getHospitalDepartments(hospitalId) {
  return Request.get(`/department/hospital/${hospitalId}`)
}

/**
 * 获取医院所有医生的所属科室（去重）
 */
export function getHospitalDoctorDepartments(hospitalId) {
  return Request.get(`/department/hospital/${hospitalId}/doctors`)
}

/**
 * 获取科室详情
 */
export function getDepartmentDetail(id) {
  return Request.get(`/department/${id}`)
}
