/**
 * 医院相关类型定义
 */

/**
 * 医院简要信息
 */
export class HospitalSimple {
  constructor(data = {}) {
    this.id = data.id
    this.name = data.hospitalName || data.name
    this.level = data.hospitalLevel || data.level
    this.province = data.provinceName || data.province
    this.city = data.cityName || data.city
    this.address = data.address
    this.rating = data.rating
    this.isMedicalInsurance = data.isMedicalInsurance
    this.avatar = data.avatar
  }
}

export default HospitalSimple
