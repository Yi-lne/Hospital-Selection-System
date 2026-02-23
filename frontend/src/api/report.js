import Request from '@/utils/request'

/**
 * 创建举报
 */
export function createReport(data) {
  return Request.post('/report', data)
}
