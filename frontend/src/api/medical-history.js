import Request from '@/utils/request'

/**
 * 获取病史列表
 */
export function getMedicalHistoryList(page = 1, pageSize = 10) {
  return Request.get('/medical-history/list', {
    params: { page, pageSize }
  })
}

/**
 * 添加病史
 */
export function addMedicalHistory(data) {
  return Request.post('/medical-history/add', data)
}

/**
 * 更新病史
 */
export function updateMedicalHistory(id, data) {
  return Request.put(`/medical-history/${id}`, data)
}

/**
 * 删除病史
 */
export function deleteMedicalHistory(id) {
  return Request.delete(`/medical-history/${id}`)
}

/**
 * 获取病史详情
 */
export function getMedicalHistoryDetail(id) {
  return Request.get(`/medical-history/${id}`)
}
