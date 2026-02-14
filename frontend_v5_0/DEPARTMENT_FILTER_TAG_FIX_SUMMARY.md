# 科室标签UI问题修复总结

## 修复时间
2026-02-06

## 问题描述
点击任意科室标签时，所有科室标签都显示为选中状态。

## 根本原因

**字段名称不匹配**：

### 数据结构
```javascript
const quickDepartments = ref([
  { code: 'cardiology', label: '心血管内科' },  // ✅ 字段是 label
  { code: 'respiratory', label: '呼吸内科' },    // ✅ 字段是 label
  // ...
])
```

### 模板绑定（错误）
```vue
<el-tag
  :type="selectedDepartment === dept.name ? 'primary' : 'info'"  <!-- ❌ 使用了 dept.name -->
  @click="handleQuickDepartment(dept.name)"                      <!-- ❌ 使用了 dept.name -->
>
  {{ dept.label }}  <!-- 显示的是 label -->
</el-tag>
```

### 问题分析

1. **dept.name 是 `undefined`**：`quickDepartments` 数组中没有 `name` 字段
2. **初始状态**：`selectedDepartment.value = ''`（空字符串）
3. **比较结果**：`'' === undefined` 返回 `false`，标签应该是灰色（info）
4. **但如果** `selectedDepartment.value` 初始是 `undefined`，那么 `undefined === undefined` 返回 `true`，所有标签都会变成蓝色（primary）

---

## 修复方案

### 修改前
```vue
<el-tag
  :type="selectedDepartment === dept.name ? 'primary' : 'info'"
  @click="handleQuickDepartment(dept.name)"
>
  {{ dept.label }}
</el-tag>
```

### 修改后
```vue
<el-tag
  :type="selectedDepartment === dept.label ? 'primary' : 'info'"  <!-- ✅ 使用 dept.label -->
  @click="handleQuickDepartment(dept.label)"                      <!-- ✅ 使用 dept.label -->
>
  {{ dept.label }}
</el-tag>
```

---

## 文件修改

| 文件 | 修改位置 | 修改内容 |
|------|---------|---------|
| `frontend/src/views/hospital/HospitalList.vue` | 第48行 | `dept.name` → `dept.label`（type 属性） |
| `frontend/src/views/hospital/HospitalList.vue` | 第50行 | `dept.name` → `dept.label`（click 事件） |

---

## 其他改进

### 1. 添加调试日志

**handleQuickDepartment 函数**：
```javascript
const handleQuickDepartment = async (departmentName) => {
  console.log('=== 选择科室 ===')
  console.log('科室名称:', departmentName)
  selectedDepartment.value = departmentName
  customDepartment.value = departmentName
  hospitalStore.setDepartmentCode(departmentName)
  hospitalStore.filterParams.page = 1

  await nextTick()  // 等待状态更新

  console.log('更新后 filterParams:', hospitalStore.filterParams)
  console.log('更新后 departmentCode:', hospitalStore.filterParams.departmentCode)

  loadHospitals()
}
```

**hasFilterConditions 函数**：
```javascript
const hasFilterConditions = () => {
  const params = hospitalStore.filterParams
  console.log('hasFilterConditions - params:', params)
  console.log('hasFilterConditions - departmentCode:', params.departmentCode)
  const hasCondition = !!(
    params.provinceCode ||
    params.cityCode ||
    params.areaCode ||
    params.level ||
    params.departmentCode
  )
  console.log('hasFilterConditions - result:', hasCondition)
  return hasCondition
}
```

**loadHospitals 函数**：
```javascript
console.log('apiParams:', JSON.stringify(hospitalStore.apiParams, null, 2))  // 输出完整的 JSON
```

### 2. 添加 nextTick 导入

```javascript
import { ref, onMounted, watch, nextTick } from 'vue'
```

---

## 测试验证

### 测试1：标签显示

**步骤**：
1. 刷新页面（清除缓存）
2. 观察科室标签初始状态

**预期结果**：
- ✅ 所有标签都显示为灰色（info 类型）
- ✅ 没有标签显示为蓝色（primary 类型）

### 测试2：单击标签

**步骤**：
1. 点击"心血管内科"标签
2. 观察标签状态

**预期结果**：
- ✅ 只有"心血管内科"标签变成蓝色
- ✅ 其他标签保持灰色
- ✅ 控制台输出：`selectedDepartment: "心血管内科"`

### 测试3：筛选功能

**步骤**：
1. 点击"心血管内科"标签
2. 观察医院列表

**预期结果**：
- ✅ 控制台输出：`departmentCode: "心血管内科"`
- ✅ 控制台输出：`apiParams.department: "心血管内科"`
- ✅ 控制台输出：`hasFilterConditions - result: true`
- ✅ 控制台输出：`Using filterHospitals API`
- ✅ 医院列表只显示包含心血管内科的医院

### 测试4：切换标签

**步骤**：
1. 点击"心血管内科"→观察标签状态
2. 点击"呼吸内科"→观察标签状态

**预期结果**：
- ✅ 步骤1：只有"心血管内科"是蓝色
- ✅ 步骤2：只有"呼吸内科"是蓝色，"心血管内科"恢复灰色

### 测试5：清空筛选

**步骤**：
1. 点击任意科室标签
2. 点击"重置"按钮

**预期结果**：
- ✅ 所有标签恢复灰色
- ✅ 医院列表显示所有医院
- ✅ 科室输入框清空

---

## 预期的控制台日志

### 点击科室标签后的完整日志

```
=== 选择科室 ===
科室名称: 心血管内科
更新后 filterParams: Proxy(Object) {
  page: 1,
  pageSize: 10,
  areaCode: undefined,
  provinceCode: undefined,
  cityCode: undefined,
  level: undefined,
  departmentCode: "心血管内科"  // ✅ 有值
}
更新后 departmentCode: 心血管内科  // ✅ 有值

=== Loading hospitals ===
filterParams: Proxy(Object) { ... }
apiParams: {
  "page": 1,
  "pageSize": 10,
  "provinceCode": undefined,
  "cityCode": undefined,
  "areaCode": undefined,
  "hospitalLevel": undefined,
  "department": "心血管内科"  // ✅ 有值
}
hasFilterConditions - params: Proxy(Object) { ... }
hasFilterConditions - departmentCode: 心血管内科  // ✅ 有值
hasFilterConditions - result: true  // ✅ 返回 true
Using filterHospitals API  // ✅ 使用筛选 API

Response: {code: 200, message: 'success', data: {...}}
Final hospitalList length: 10
```

---

## 总结

✅ **问题已修复**：
- 将模板中的 `dept.name` 改为 `dept.label`
- 添加了详细的调试日志
- 添加了 `nextTick` 确保状态更新完成

✅ **功能现在应该正常工作**：
- 标签选中状态正确
- 科室筛选功能正常
- API 调用正确（使用 filterHospitals 而不是 getHospitalList）

---

## 下一步

1. **刷新前端页面**（清除缓存）
2. **测试科室筛选功能**
3. **如果仍有问题**，查看控制台日志并参考 `DEPARTMENT_FILTER_DEBUG_GUIDE.md`

---

**相关文档**：
- [科室筛选调试指南](./DEPARTMENT_FILTER_DEBUG_GUIDE.md)
- [科室筛选功能实现完成报告](./DEPARTMENT_FILTER_IMPLEMENTATION.md)
- [科室筛选错误修复总结](./DEPARTMENT_FILTER_FIX_SUMMARY.md)
