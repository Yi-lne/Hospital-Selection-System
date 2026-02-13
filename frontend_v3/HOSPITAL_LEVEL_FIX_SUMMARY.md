# 前端医院等级中文显示修复

## 修复时间
2026-02-05

## 问题描述
前端页面中医院等级显示为英文代码（如 `grade3A`、`grade2A`），用户期望显示中文（如"三甲"、"二甲"）。

## 解决方案

### 1. 创建工具函数
新建 `frontend/src/utils/hospital.js`，提供等级转换功能：

```javascript
// 等级代码到中文的映射
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

// 主要函数
formatHospitalLevel(levelCode)  // 代码转中文
chineseToLevelCode(chineseLevel) // 中文转代码
getHospitalLevelOptions()        // 获取筛选选项
compareHospitalLevel(level1, level2) // 等级排序
```

### 2. 修改的组件

| 组件 | 文件路径 | 修改内容 |
|------|----------|----------|
| **HospitalListItem** | `components/hospital/HospitalListItem.vue` | 使用 `formatHospitalLevel()` 转换显示 |
| **FilterPanel** | `components/hospital/FilterPanel.vue` | 使用 `getHospitalLevelOptions()` 获取选项，值为英文代码 |
| **HospitalDetail** | `views/hospital/HospitalDetail.vue` | 添加 `displayLevel` 计算属性显示中文 |
| **home/index** | `views/home/index.vue` | 修复三甲医院筛选条件（从'三甲'改为'grade3A'） |

### 3. 修改详情

#### HospitalListItem.vue
```vue
<!-- 修改前 -->
<el-tag>{{ hospital.hospitalLevel || hospital.level }}</el-tag>

<!-- 修改后 -->
<script setup>
import { formatHospitalLevel } from '@/utils/hospital'
const displayLevel = computed(() => {
  return formatHospitalLevel(props.hospital.hospitalLevel || props.hospital.level)
})
</script>
<el-tag>{{ displayLevel }}</el-tag>
```

#### FilterPanel.vue
```vue
<!-- 修改前 -->
<el-option label="三级甲等" value="三甲" />

<!-- 修改后 -->
<script setup>
import { getHospitalLevelOptions } from '@/utils/hospital'
const hospitalLevelOptions = getHospitalLevelOptions()
</script>
<el-option
  v-for="option in hospitalLevelOptions"
  :key="option.value"
  :label="option.label"
  :value="option.value"
/>
```

#### HospitalDetail.vue
```vue
<!-- 添加计算属性 -->
<script setup>
import { formatHospitalLevel } from '@/utils/hospital'
const displayLevel = computed(() => {
  if (!hospital.value) return ''
  return formatHospitalLevel(hospital.value.level)
})
</script>

<!-- 模板使用 -->
<el-tag type="danger" size="large">{{ displayLevel }}</el-tag>
```

#### home/index.vue
```javascript
// 修改前
const grade3AHospitals = allHospitals.filter((hospital) =>
  hospital.level === '三甲' || hospital.hospitalLevel === '三甲'
)

// 修改后
const grade3AHospitals = allHospitals.filter((hospital) =>
  hospital.hospitalLevel === 'grade3A' || hospital.level === 'grade3A'
)
```

## 数据流转

### 显示流程（后端→前端）
```
后端API → grade3A → formatHospitalLevel('grade3A') → "三甲" → 用户界面
```

### 筛选流程（前端→后端）
```
用户选择"三甲" → value="grade3A" → API请求 → 后端筛选
```

## 测试验证

### 1. 医院列表显示
- 访问医院列表页面
- 检查医院卡片上的等级标签显示为中文

### 2. 筛选功能
- 打开筛选面板
- 选择"三级甲等"
- 确认筛选结果正确

### 3. 医院详情页
- 点击任意医院进入详情页
- 检查等级标签显示为中文

## 兼容性说明

- ✅ 不影响后端API（后端仍使用英文代码）
- ✅ 现有组件自动适配（使用HospitalListItem的组件无需修改）
- ✅ Store保持不变（levelCodeMap映射仍有效）

## 扩展性

如需支持更多等级，只需在 `HOSPITAL_LEVEL_MAP` 中添加映射即可：
```javascript
const HOSPITAL_LEVEL_MAP = {
  // ...existing
  'special': '专科医院',
  'community': '社区医院'
}
```
