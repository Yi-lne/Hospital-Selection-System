import Request from '@/utils/request'

/**
 * 获取医院的科室列表
 */
export function getHospitalDepartments(hospitalId) {
  return Request.get(`/department/hospital/${hospitalId}`)
}

/**
 * 获取科室详情
 */
export function getDepartmentDetail(id) {
  return Request.get(`/department/${id}`)
}
