/**
 * 医院等级转换工具函数
 */

/**
 * 医院等级代码到中文的映射
 */
const HOSPITAL_LEVEL_MAP = {
  'grade3A': '三甲',
  'grade3B': '三乙',
  'grade3C': '三丙',
  'grade2A': '二甲',
  'grade2B': '二乙',
  'grade2C': '二丙',
  'grade1A': '一甲',
  'grade1B': '一乙',
  'grade1C': '一丙'
}

/**
 * 中文到医院等级代码的反向映射
 */
const CHINESE_TO_LEVEL_MAP = {
  '三甲': 'grade3A',
  '三乙': 'grade3B',
  '三丙': 'grade3C',
  '二甲': 'grade2A',
  '二乙': 'grade2B',
  '二丙': 'grade2C',
  '一甲': 'grade1A',
  '一乙': 'grade1B',
  '一丙': 'grade1C'
}

/**
 * 将医院等级代码转换为中文显示
 * @param {string} levelCode - 医院等级代码（如：grade3A, grade2A）
 * @returns {string} 中文等级名称（如：三甲、二甲）
 */
export function formatHospitalLevel(levelCode) {
  if (!levelCode) return ''
  return HOSPITAL_LEVEL_MAP[levelCode] || levelCode
}

/**
 * 将中文等级转换为代码
 * @param {string} chineseLevel - 中文等级（如：三甲、二甲）
 * @returns {string} 医院等级代码
 */
export function chineseToLevelCode(chineseLevel) {
  if (!chineseLevel) return ''
  return CHINESE_TO_LEVEL_MAP[chineseLevel] || chineseLevel
}

/**
 * 获取所有医院等级选项（用于筛选器）
 * @returns {Array<{label: string, value: string}>}
 */
export function getHospitalLevelOptions() {
  return [
    { label: '三级甲等', value: 'grade3A' },
    { label: '三级乙等', value: 'grade3B' },
    { label: '三级丙等', value: 'grade3C' },
    { label: '二级甲等', value: 'grade2A' },
    { label: '二级乙等', value: 'grade2B' },
    { label: '二级丙等', value: 'grade2C' },
    { label: '一级甲等', value: 'grade1A' },
    { label: '一级乙等', value: 'grade1B' },
    { label: '一级丙等', value: 'grade1C' }
  ]
}

/**
 * 根据等级代码排序（等级越高排序越靠前）
 * @param {string} level1 - 等级代码1
 * @param {string} level2 - 等级代码2
 * @returns {number} -1, 0, 或 1
 */
export function compareHospitalLevel(level1, level2) {
  const levelOrder = ['grade3A', 'grade3B', 'grade3C', 'grade2A', 'grade2B', 'grade2C', 'grade1A', 'grade1B', 'grade1C']
  const index1 = levelOrder.indexOf(level1)
  const index2 = levelOrder.indexOf(level2)
  if (index1 === -1 && index2 === -1) return 0
  if (index1 === -1) return 1
  if (index2 === -1) return -1
  return index1 - index2
}

export default {
  formatHospitalLevel,
  chineseToLevelCode,
  getHospitalLevelOptions,
  compareHospitalLevel
}
