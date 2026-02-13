import Request from '@/utils/request'

/**
 * 添加收藏
 */
export function addCollection(data) {
  return Request.post('/collection/add', data)
}

/**
 * 取消收藏
 * 后端接口: DELETE /collection/cancel
 * 后端期望: { targetType: number, targetId: number }
 */
export function cancelCollection(data) {
  return Request.delete('/collection/cancel', { data })
}

/**
 * 获取收藏列表
 */
export function getCollectionList(page = 1, pageSize = 10) {
  return Request.get('/collection/list', {
    params: { page, pageSize }
  })
}

/**
 * 检查是否已收藏
 * 后端接口: GET /collection/check?targetType={targetType}&targetId={targetId}
 */
export function checkCollected(targetType, targetId) {
  return Request.get('/collection/check', {
    params: { targetType, targetId }
  })
}
