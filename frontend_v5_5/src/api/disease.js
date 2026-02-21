import Request from '@/utils/request'

/**
 * 获取疾病树
 */
export function getDiseaseTree() {
  return Request.get('/disease/tree')
}

/**
 * 获取一级分类
 */
export function getLevel1Categories() {
  return Request.get('/disease/level1')
}

/**
 * 获取二级分类
 */
export function getLevel2Categories(parentId) {
  return Request.get(`/disease/level2/${parentId}`)
}

/**
 * 根据名称搜索疾病
 */
export function searchDiseases(keyword) {
  return Request.get('/disease/search', {
    params: { keyword }
  })
}
