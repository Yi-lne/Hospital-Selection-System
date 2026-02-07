import Request from '@/utils/request'

/**
 * 获取地区树
 */
export function getAreaTree() {
  return Request.get('/area/tree')
}

/**
 * 获取省份列表
 */
export function getProvinces() {
  return Request.get('/area/province')
}

/**
 * 获取城市列表
 */
export function getCities(parentId) {
  return Request.get(`/area/city/${parentId}`)
}

/**
 * 获取区县列表
 */
export function getDistricts(parentId) {
  return Request.get(`/area/area/${parentId}`)
}
