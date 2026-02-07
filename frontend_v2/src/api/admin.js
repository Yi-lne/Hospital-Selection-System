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
  }
}
