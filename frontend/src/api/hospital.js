import Request from '@/utils/request'

/**
 * 获取医院列表（分页）
 */
export function getHospitalList(params) {
  return Request.get('/hospital/list', { params })
}

/**
 * 搜索医院
 */
export function searchHospitals(keyword, params) {
  return Request.get('/hospital/search', { params: { keyword, ...params } })
}

/**
 * 获取医院详情
 */
export function getHospitalDetail(id) {
  return Request.get(`/hospital/${id}`)
}

/**
 * 获取医院科室列表
 */
export function getHospitalDepartments(hospitalId) {
  return Request.get(`/hospital/${hospitalId}/departments`)
}

/**
 * 筛选医院（多条件）
 */
export function filterHospitals(params) {
  return Request.post('/hospital/filter', params)
}

/**
 * 获取医院医生列表
 */
export function getHospitalDoctors(hospitalId) {
  return Request.get(`/hospital/${hospitalId}/doctors`)
}
