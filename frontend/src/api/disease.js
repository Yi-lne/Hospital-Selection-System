import Request from '@/utils/request'

/**
 * 获取疾病树
 */
export function getDiseaseTree() {
  return Request.get('/disease/tree')
}

/**
  * 获取疾病分类树（一级分类）
  */
export function getLevel1Diseases() {
  return Request.get('/disease/level1')
}
