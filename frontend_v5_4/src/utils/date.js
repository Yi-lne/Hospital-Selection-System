import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

/**
 * 格式化相对时间
 */
export function formatRelativeTime(date) {
  return dayjs(date).fromNow()
}

/**
 * 格式化日期时间
 */
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(date).format(format)
}

/**
 * 格式化日期
 */
export function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 格式化时间
 */
export function formatTime(date) {
  return dayjs(date).format('HH:mm:ss')
}

/**
 * 获取当前日期
 */
export function getCurrentDate() {
  return dayjs().format('YYYY-MM-DD')
}

/**
 * 获取当前时间戳
 */
export function getCurrentTimestamp() {
  return dayjs().valueOf()
}
