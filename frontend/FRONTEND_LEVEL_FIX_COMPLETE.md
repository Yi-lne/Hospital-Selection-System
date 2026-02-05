# 前端医院等级中文显示修复 - 完整版

## 修复时间
2026-02-05

## 问题
前端页面显示医院等级为英文代码（如 `grade3A`、`grade2A`），用户期望显示中文（如"三甲"、"二乙"）。

## 根本原因
1. `HospitalCard.vue` 组件直接显示 `hospital.level`，未使用转换函数
2. `HospitalList.vue` 组件的筛选器使用中文值但需要转换为英文代码
3. `hospital.js` store 包含不必要的中文转换逻辑

## 完整修复方案

### 1. 工具函数 ✅
**文件**: `frontend/src/utils/hospital.js`

提供等级转换功能：
```javascript
// 代码 → 中文
formatHospitalLevel('grade3A')  // → "三甲"

// 中文 → 代码
chineseToLevelCode('三甲')       // → "grade3A"

// 获取筛选选项（英文代码）
getHospitalLevelOptions()
// [{ label: "三级甲等", value: "grade3A" }, ...]
```

### 2. 组件修复

#### HospitalCard.vue ✅
**文件**: `frontend/src/components/hospital/HospitalCard.vue`

```vue
<!-- 添加计算属性 -->
<script setup>
import { formatHospitalLevel } from '@/utils/hospital'

const displayLevel = computed(() => {
  return formatHospitalLevel(props.hospital.level)
})
</script>

<!-- 使用转换后的值 -->
<el-tag>{{ displayLevel }}</el-tag>
```

#### HospitalListItem.vue ✅
**文件**: `frontend/src/components/hospital/HospitalListItem.vue`

```vue
<script setup>
import { formatHospitalLevel } from '@/utils/hospital'

const displayLevel = computed(() => {
  const levelCode = props.hospital.hospitalLevel || props.hospital.level
  return formatHospitalLevel(levelCode)
})
</script>

<el-tag>{{ displayLevel }}</el-tag>
```

#### HospitalDetail.vue ✅
**文件**: `frontend/src/views/hospital/HospitalDetail.vue`

```vue
<script setup>
import { formatHospitalLevel } from '@/utils/hospital'

const displayLevel = computed(() => {
  if (!hospital.value) return ''
  return formatHospitalLevel(hospital.value.level)
})
</script>

<el-tag type="danger" size="large">{{ displayLevel }}</el-tag>
```

#### FilterPanel.vue ✅
**文件**: `frontend/src/components/hospital/FilterPanel.vue`

```vue
<script setup>
import { getHospitalLevelOptions } from '@/utils/hospital'
const hospitalLevelOptions = getHospitalLevelOptions()
</script>

<el-select v-model="filterData.hospitalLevel">
  <el-option
    v-for="option in hospitalLevelOptions"
    :key="option.value"
    :label="option.label"
    :value="option.value"
  />
</el-select>
```

#### HospitalList.vue ✅
**文件**: `frontend/src/views/hospital/HospitalList.vue`

```vue
<!-- 使用英文代码作为值，中文作为显示 -->
<el-radio-group v-model="hospitalStore.selectedLevel">
  <el-radio :value="undefined">全部</el-radio>
  <el-radio value="grade3A">三甲</el-radio>
  <el-radio value="grade3B">三乙</el-radio>
  <el-radio value="grade2A">二甲</el-radio>
  <el-radio value="grade2B">二乙</el-radio>
  <el-radio value="grade2C">二丙</el-radio>
  <el-radio value="grade1A">一甲</el-radio>
</el-radio-group>
```

### 3. Store修复 ✅
**文件**: `frontend/src/stores/modules/hospital.js`

**移除不必要的中文转换逻辑**：

```javascript
// 删除
const levelCodeMap = {
  '三甲': 'grade3A',
  '三乙': 'grade3B',
  // ...
}

// 简化
const apiParams = computed(() => {
  // 直接使用英文代码（UI已改为英文代码）
  const hospitalLevel = filterParams.value.level
  return { hospitalLevel, ... }
})
```

### 4. 其他修复 ✅

#### home/index.vue
**文件**: `frontend/src/views/home/index.vue`

```javascript
// 修复三甲筛选条件
const grade3AHospitals = allHospitals.filter((hospital) =>
  hospital.hospitalLevel === 'grade3A' || hospital.level === 'grade3A'
)
```

## 等级映射表

| 英文代码 | 中文显示 | 说明 |
|---------|---------|------|
| grade3A | 三甲 | 三级甲等 |
| grade3B | 三乙 | 三级乙等 |
| grade2A | 二甲 | 二级甲等 |
| grade2B | 二乙 | 二级乙等 |
| grade2C | 二丙 | 二级丙等 |
| grade1A | 一甲 | 一级甲等 |

## 数据流

### 显示流程
```
后端API → grade3A → formatHospitalLevel() → "三甲" → 用户界面
```

### 筛选流程
```
用户点击"三甲" → value="grade3A" → store.apiParams → API请求 → 后端筛选
```

## 测试验证

### 1. 医院列表页
- 访问 `/hospital` 列表页面
- 检查医院卡片上的等级标签显示中文

### 2. 医院详情页
- 点击任意医院进入详情
- 检查等级标签显示中文

### 3. 筛选功能
- 在筛选面板选择"三甲"
- 确认筛选结果正确

### 4. 收藏页面
- 访问"我的收藏"
- 检查医院等级显示中文

## 注意事项

1. **后端兼容性**: 后端API仍使用英文代码（`grade3A`等）
2. **显示统一**: 所有前端显示都通过 `formatHospitalLevel()` 转换
3. **筛选器值**: 筛选器使用英文代码作为 `value`，中文作为 `label`
4. **不再需要转换**: store不再进行中文→英文的转换（直接使用英文代码）

## 文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `utils/hospital.js` | ✅ 新建 | 工具函数 |
| `components/hospital/HospitalCard.vue` | ✅ 修复 | 添加转换 |
| `components/hospital/HospitalListItem.vue` | ✅ 修复 | 添加转换 |
| `components/hospital/FilterPanel.vue` | ✅ 修复 | 使用选项工具 |
| `views/hospital/HospitalDetail.vue` | ✅ 修复 | 添加转换 |
| `views/hospital/HospitalList.vue` | ✅ 修复 | 筛选器改用英文代码 |
| `stores/modules/hospital.js` | ✅ 修复 | 移除转换逻辑 |
| `views/home/index.vue` | ✅ 修复 | 筛选条件改用英文代码 |
| `views/user/MyCollection.vue` | ✅ 无需修改 | 使用HospitalCard组件 |
