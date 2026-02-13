# 科室筛选调试指南

## 问题描述
点击任意科室标签时，所有科室标签都显示为选中状态，且筛选功能不起作用。

## 调试日志说明

### 1. 点击科室标签时会输出以下日志

```
=== 选择科室 ===
科室名称: 心血管内科
更新后 filterParams: {page: 1, pageSize: 10, ...}
更新后 departmentCode: 心血管内科
```

### 2. 加载医院时会输出以下日志

```
=== Loading hospitals ===
filterParams: {page: 1, pageSize: 10, departmentCode: "心血管内科", ...}
apiParams: {
  "page": 1,
  "pageSize": 10,
  "provinceCode": undefined,
  "cityCode": undefined,
  "areaCode": undefined,
  "hospitalLevel": undefined,
  "department": "心血管内科"  // ✅ 关键：这个字段应该存在
}
hasFilterConditions - params: {...}
hasFilterConditions - departmentCode: 心血管内科
hasFilterConditions - result: true  // ✅ 应该返回 true
Using filterHospitals API  // ✅ 应该使用筛选API
```

### 3. 预期行为

**正确的流程**：
1. 点击科室标签 → `selectedDepartment` 更新为科室名称
2. 调用 `hospitalStore.setDepartmentCode(departmentName)` → `filterParams.departmentCode` 更新
3. `apiParams.computed` 自动重新计算 → `apiParams.department` 获得值
4. `hasFilterConditions()` 返回 `true`（因为 `departmentCode` 存在）
5. 使用 `filterHospitals(apiParams)` API 而不是 `getHospitalList(apiParams)`

---

## 可能的原因分析

### 原因1：Pinia store 的响应式更新问题

**现象**：`setDepartmentCode` 调用后，`filterParams.departmentCode` 仍然是 `undefined`

**解决方案**：
- 使用 `nextTick()` 等待响应式更新（已添加）
- 检查 `hospital.js` 中的 `setFilterParams` 实现

### 原因2：apiParams 计算属性未正确计算

**现象**：`filterParams.departmentCode` 有值，但 `apiParams.department` 仍是 `undefined`

**可能原因**：
```javascript
// hospital.js 中的计算属性
const apiParams = computed(() => {
  const params = {
    // ...
    department: filterParams.value.departmentCode  // 如果这里出错，会导致字段丢失
  }
  return params
})
```

**解决方案**：
- 检查计算属性是否正确访问 `filterParams.value.departmentCode`
- 检查是否有其他代码覆盖了 `department` 字段

### 原因3：标签 UI 逻辑问题

**现象**：所有标签都显示为选中状态

**可能原因**：
```vue
<!-- 标签 type 判断 -->
<el-tag
  :type="selectedDepartment === dept.name ? 'primary' : 'info'"
>
```

**问题**：`dept.name` 可能不存在（应该是 `dept.label`）

**解决方案**：
```vue
<!-- 修改前 -->
:type="selectedDepartment === dept.name ? 'primary' : 'info'"

<!-- 修改后 -->
:type="selectedDepartment === dept.label ? 'primary' : 'info'"
@click="handleQuickDepartment(dept.label)"  // 确保传递 label
```

---

## 测试步骤

### 步骤1：刷新页面并清除缓存

1. 打开浏览器开发者工具（F12）
2. 右键点击刷新按钮，选择"清空缓存并硬性重新加载"
3. 或者按 `Ctrl + Shift + R`

### 步骤2：打开控制台查看日志

1. 切换到 Console 标签页
2. 访问 `http://localhost:5173/hospital`
3. 点击任意科室标签（如"心血管内科"）
4. 观察控制台输出

### 步骤3：检查关键日志

**✅ 正确的日志应该显示**：
```
=== 选择科室 ===
科室名称: 心血管内科
更新后 departmentCode: "心血管内科"  // 有值

apiParams: {
  "department": "心血管内科"  // 有值
}

hasFilterConditions - result: true  // 返回 true
Using filterHospitals API  // 使用筛选 API
```

**❌ 错误的日志会显示**：
```
=== 选择科室 ===
科室名称: 心血管内科
更新后 departmentCode: undefined  // ❌ 没有值

apiParams: {
  // 没有 department 字段
}

hasFilterConditions - result: false  // ❌ 返回 false
Using getHospitalList API  // ❌ 使用列表 API（错误）
```

---

## 根据日志输出采取行动

### 场景1：`departmentCode` 是 `undefined`

**问题**：Pinia store 的 `setDepartmentCode` 方法没有正确更新状态

**解决方案**：
检查 `hospital.js` 中的实现：
```javascript
function setDepartmentCode(departmentCode) {
  console.log('setDepartmentCode called with:', departmentCode)  // 添加日志
  selectedDiseaseCode.value = departmentCode
  setFilterParams({ departmentCode })
}
```

### 场景2：`departmentCode` 有值，但 `apiParams.department` 是 `undefined`

**问题**：计算属性没有正确映射字段

**解决方案**：
检查 `hospital.js` 中的 `apiParams` 计算属性：
```javascript
const apiParams = computed(() => {
  console.log('apiParams recomputing, departmentCode:', filterParams.value.departmentCode)  // 添加日志
  const params = {
    page: filterParams.value.page,
    pageSize: filterParams.value.pageSize,
    provinceCode: filterParams.value.provinceCode,
    cityCode: filterParams.value.cityCode,
    areaCode: filterParams.value.areaCode,
    hospitalLevel: filterParams.value.level,
    department: filterParams.value.departmentCode  // ✅ 确保这行存在
  }
  console.log('apiParams computed:', params)  // 添加日志
  return params
})
```

### 场景3：所有标签都显示为选中

**问题**：标签的 `type` 判断条件有问题

**解决方案**：
检查 `HospitalList.vue` 中的标签：
```vue
<el-tag
  v-for="dept in quickDepartments"
  :key="dept.code"
  :type="selectedDepartment === dept.label ? 'primary' : 'info'"  // ✅ 使用 dept.label
  effect="plain"
  @click="handleQuickDepartment(dept.label)"  // ✅ 传递 dept.label
  class="dept-tag"
>
  {{ dept.label }}
</el-tag>
```

---

## 额外的检查点

### 检查1：`quickDepartments` 数据结构

在浏览器控制台输入：
```javascript
// 检查 quickDepartments 的结构
console.log(quickDepartments)
// 应该输出：
// [
//   { code: 'cardiology', label: '心血管内科' },
//   { code: 'respiratory', label: '呼吸内科' },
//   ...
// ]
```

### 检查2：`selectedDepartment` 初始值

在浏览器控制台输入：
```javascript
// 检查 selectedDepartment 的值
console.log(selectedDepartment.value)
// 应该输出：""（空字符串）
```

### 检查3：点击后 `selectedDepartment` 的值

在浏览器控制台输入：
```javascript
// 点击科室标签后，再次检查
console.log(selectedDepartment.value)
// 应该输出："心血管内科"（或你点击的科室名称）
```

---

## 下一步

请按照上述步骤操作，并将控制台的完整日志输出发送给我，我会根据日志进一步分析问题。

**关键日志**：
1. `=== 选择科室 ===` 部分
2. `apiParams:` 的完整 JSON 输出
3. `hasFilterConditions` 的输出
4. 使用的 API 类型（`filterHospitals` 还是 `getHospitalList`）

---

**文件位置**：
- 前端组件：`frontend/src/views/hospital/HospitalList.vue`
- 状态管理：`frontend/src/stores/modules/hospital.js`
