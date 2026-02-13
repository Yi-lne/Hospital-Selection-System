import Request from '@/utils/request'

/**
 * 获取医院医生列表
 */
export function getHospitalDoctors(hospitalId) {
  return Request.get(`/hospital/${hospitalId}/doctors`)
}

/**
 * 根据科室名称筛选医院医生列表
 */
export function getHospitalDoctorsByDeptName(hospitalId, deptName) {
  return Request.get(`/hospital/${hospitalId}/doctors/dept/${deptName}`)
}

/**
 * 获取医生详情
 */
export function getDoctorDetail(doctorId) {
  return Request.get(`/doctor/${doctorId}`)
}
