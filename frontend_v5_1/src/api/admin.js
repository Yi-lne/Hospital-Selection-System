/**
 * 管理员API
 */
import Request from '@/utils/request'

export const adminApi = {
  // ==================== 医院管理 ====================

  /**
   * 获取所有医院（分页）
   */
  getHospitals(params) {
    return Request.get('/admin/hospitals', { params })
  },

  /**
   * 获取医院详情
   */
  getHospitalDetail(id) {
    return Request.get(`/hospital/${id}`)
  },

  /**
   * 搜索医院
   */
  searchHospitals(keyword) {
    return Request.get('/admin/hospitals/search', { params: { keyword } })
  },

  /**
   * 创建医院
   */
  createHospital(data) {
    return Request.post('/admin/hospital', data)
  },

  /**
   * 更新医院
   */
  updateHospital(id, data) {
    return Request.put(`/admin/hospital/${id}`, data)
  },

  /**
   * 删除医院
   */
  deleteHospital(id) {
    return Request.delete(`/admin/hospital/${id}`)
  },

  /**
   * 恢复医院
   */
  restoreHospital(id) {
    return Request.post(`/admin/hospital/${id}/restore`)
  },

  // ==================== 科室管理 ====================

  /**
   * 创建科室
   */
  createDepartment(data) {
    return Request.post('/admin/department', data)
  },

  /**
   * 更新科室
   */
  updateDepartment(id, data) {
    return Request.put(`/admin/department/${id}`, data)
  },

  /**
   * 删除科室
   */
  deleteDepartment(id) {
    return Request.delete(`/admin/department/${id}`)
  },

  /**
   * 批量创建科室
   */
  batchCreateDepartments(hospitalId, deptNames) {
    return Request.post('/admin/department/batch', deptNames, {
      params: { hospitalId }
    })
  },

  // ==================== 医生管理 ====================

  /**
   * 创建医生
   */
  createDoctor(data) {
    return Request.post('/admin/doctor', data)
  },

  /**
   * 更新医生
   */
  updateDoctor(id, data) {
    return Request.put(`/admin/doctor/${id}`, data)
  },

  /**
   * 删除医生
   */
  deleteDoctor(id) {
    return Request.delete(`/admin/doctor/${id}`)
  },

  /**
   * 批量导入医生
   */
  importDoctors(file) {
    const formData = new FormData()
    formData.append('file', file)
    return Request.upload('/admin/doctor/import', formData)
  },

  // ==================== 社区管理 ====================

  /**
   * 获取所有话题（管理员视角）
   */
  getAllTopics(params) {
    return Request.get('/community/admin/topics/all', { params })
  },

  /**
   * 获取待审核话题列表
   */
  getPendingTopics(params) {
    return Request.get('/community/admin/topics/pending', { params })
  },

  /**
   * 管理员删除话题
   */
  deleteTopic(id, reason) {
    return Request.delete(`/community/admin/topic/${id}`, {
      params: { reason }
    })
  },

  /**
   * 管理员审核话题
   */
  moderateTopic(id, status, reason) {
    return Request.put(`/community/admin/topic/${id}/moderate`, null, {
      params: { status, reason }
    })
  },

  /**
   * 管理员删除评论
   */
  deleteComment(id, reason) {
    return Request.delete(`/community/admin/comment/${id}`, {
      params: { reason }
    })
  },

  // ==================== 举报管理 ====================

  /**
   * 获取举报列表
   */
  getReportList(params) {
    return Request.get('/report/list', { params })
  },

  /**
   * 处理举报
   */
  handleReport(id, data) {
    return Request.put(`/report/${id}/handle`, data)
  },

  /**
   * 删除举报记录
   */
  deleteReport(id) {
    return Request.delete(`/report/${id}`)
  }
}
