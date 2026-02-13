import Request from '@/utils/request'

/**
 * 获取查询历史列表
 */
export function getQueryHistoryList(page = 1, pageSize = 10) {
  return Request.get('/query-history/list', {
    params: { page, pageSize }
  })
}

/**
 * 获取查询历史（带筛选）
 */
export function getQueryHistory(params) {
  return Request.get('/query-history/list', { params })
}

/**
 * 记录查询历史
 */
export function recordQueryHistory(queryType, keyword, targetId, targetName) {
  return Request.post('/query-history/record', {
    queryType, keyword, targetId, targetName
  })
}

/**
 * 删除查询历史
 */
export function deleteQueryHistory(id) {
  return Request.delete(`/query-history/${id}`)
}

/**
 * 删除单条历史记录
 */
export function deleteHistory(id) {
  return Request.delete(`/medical-history/${id}`)
}

/**
 * 清空查询历史
 */
export function clearQueryHistory() {
  return Request.delete('/query-history/clear')
}
