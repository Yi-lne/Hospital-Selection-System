# 医院详情页医生筛选功能说明

## 功能位置

**页面**：医院详情页（`/hospital/:id`）
**位置**：页面下方，医生列表部分
**筛选范围**：仅限当前医院的医生

---

## 功能特性

### 1. 科室筛选

**显示条件**：当医院有科室数据时显示

**功能**：
- 下拉框显示该医院的所有科室
- 选择科室后，医生列表只显示该科室的医生
- 支持清空选择
- 可以与职称筛选组合使用

**数据来源**：
```javascript
// 加载该医院的科室列表
const deptRes = await getHospitalDepartments(hospitalId)
departments.value = deptRes.data || []
```

**筛选逻辑**：
```javascript
if (doctorFilter.deptId !== undefined) {
  result = result.filter(d => d.deptId === doctorFilter.deptId)
}
```

### 2. 职称筛选

**显示条件**：始终显示

**可选项**：
- 主任医师
- 副主任医师
- 主治医师
- 住院医师

**功能**：
- 选择职称后，医生列表只显示该职称的医生
- 支持清空选择
- 可以与科室筛选组合使用

**筛选逻辑**：
```javascript
if (doctorFilter.title) {
  result = result.filter(d => d.title === doctorFilter.title)
}
```

### 3. 重置筛选

**显示条件**：当有任何筛选条件时显示

**功能**：
- 清空所有筛选条件
- 显示该医院的全部医生

---

## 使用流程

### 场景1：只按科室筛选

```
用户进入医院详情页
  ↓
在"医生列表"部分
  ↓
点击"选择科室"下拉框
  ↓
选择"心血管内科"
  ↓
医生列表只显示该医院心血管内科的医生
```

### 场景2：只按职称筛选

```
用户进入医院详情页
  ↓
在"医生列表"部分
  ↓
点击"选择职称"下拉框
  ↓
选择"主任医师"
  ↓
医生列表只显示该医院的主任医师
```

### 场景3：组合筛选（科室 + 职称）

```
用户进入医院详情页
  ↓
选择科室："心血管内科"
  ↓
选择职称："主任医师"
  ↓
医生列表只显示该医院心血管内科的主任医师
```

### 场景4：重置筛选

```
用户设置了筛选条件
  ↓
点击"重置筛选"按钮
  ↓
所有筛选条件清空
  ↓
医生列表显示该医院的全部医生
```

---

## 界面展示

### 筛选框布局

```
┌─────────────────────────────────────────────────┐
│ 医生列表                                        │
├─────────────────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────────┐ ┌──────────┐ │
│ │ 选择科室 ▼   │ │ 选择职称 ▼   │ │ 重置筛选 │ │
│ └──────────────┘ └──────────────┘ └──────────┘ │
├─────────────────────────────────────────────────┤
│ ┌────────┐ ┌────────┐ ┌────────┐               │
│ │ 医生1  │ │ 医生2  │ │ 医生3  │  ...          │
│ └────────┘ └────────┘ └────────┘               │
└─────────────────────────────────────────────────┘
```

### 无科室数据时

```
┌─────────────────────────────────────────────────┐
│ 医生列表                                        │
├─────────────────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────┐                  │
│ │ 选择职称 ▼   │ │ 重置筛选 │                  │
│ └──────────────┘ └──────────┘                  │
├─────────────────────────────────────────────────┤
│ ┌────────┐ ┌────────┐ ┌────────┐               │
│ │ 医生1  │ │ 医生2  │ │ 医生3  │  ...          │
│ └────────┘ └────────┘ └────────┘               │
└─────────────────────────────────────────────────┘
```

---

## 代码实现

### 模板部分

```vue
<div class="doctor-filter">
  <!-- 科室筛选（条件渲染） -->
  <el-select
    v-if="departments && departments.length > 0"
    v-model="doctorFilter.deptId"
    placeholder="选择科室"
    clearable
  >
    <el-option
      v-for="dept in departments"
      :key="dept.id"
      :label="dept.deptName"
      :value="dept.id"
    />
  </el-select>

  <!-- 职称筛选（始终显示） -->
  <el-select
    v-model="doctorFilter.title"
    placeholder="选择职称"
    clearable
  >
    <el-option label="主任医师" value="主任医师" />
    <el-option label="副主任医师" value="副主任医师" />
    <el-option label="主治医师" value="主治医师" />
    <el-option label="住院医师" value="住院医师" />
  </el-select>

  <!-- 重置按钮 -->
  <el-button v-if="hasDoctorFilter" link @click="resetDoctorFilter">
    重置筛选
  </el-button>
</div>
```

### 数据部分

```javascript
// 医生筛选条件
const doctorFilter = ref({
  deptId: undefined,  // 科室ID
  title: undefined    // 职称
})

// 判断是否有筛选条件
const hasDoctorFilter = computed(() => {
  return doctorFilter.value.deptId !== undefined ||
         doctorFilter.value.title !== undefined
})

// 筛选后的医生列表（自动更新）
const filteredDoctors = computed(() => {
  let result = doctors.value  // 该医院的全部医生

  // 科室筛选
  if (doctorFilter.value.deptId !== undefined) {
    result = result.filter(d => d.deptId === doctorFilter.value.deptId)
  }

  // 职称筛选
  if (doctorFilter.value.title) {
    result = result.filter(d => d.title === doctorFilter.value.title)
  }

  return result
})
```

### 数据加载

```javascript
// 加载医院详情时同时加载医生和科室
const loadHospitalDetail = async () => {
  const id = Number(route.params.id)

  // 1. 加载医院信息
  const res = await getHospitalDetail(id)
  hospital.value = { ... }

  // 2. 加载该医院的医生列表
  const doctorRes = await getHospitalDoctors(id)
  doctors.value = doctorRes.data.map(item => ({
    id: item.id,
    doctorName: item.doctorName || item.name,
    title: item.title,
    deptId: item.deptId,  // 关键：用于科室筛选
    deptName: item.deptName || item.departmentName,
    // ...
  }))

  // 3. 加载该医院的科室列表
  const deptRes = await getHospitalDepartments(id)
  departments.value = deptRes.data || []
}
```

---

## 关键点说明

### 1. 筛选范围限制

**重要**：筛选功能只针对当前医院的医生，不会跨医院筛选。

**数据来源**：
```javascript
// 只获取该医院的医生
const doctorRes = await getHospitalDoctors(hospitalId)
```

**筛选逻辑**：
```javascript
// filteredDoctors 基于 doctors.value
// doctors.value 只包含该医院的医生
const filteredDoctors = computed(() => {
  let result = doctors.value  // 只有当前医院的医生
  // 筛选逻辑只在这个范围内进行
  return result
})
```

### 2. 科室筛选的条件渲染

**为什么用 `v-if`**：
- 如果医院没有科室数据，科室下拉框不应该显示
- 避免显示空的下拉框影响用户体验

**条件**：
```vue
v-if="departments && departments.length > 0"
```

### 3. 职称筛选始终显示

**为什么始终显示**：
- 职称是通用的（主任医师、副主任医师等）
- 所有医院都有这些职称
- 用户最常用的筛选条件

### 4. 实时筛选

**使用计算属性**：
```javascript
const filteredDoctors = computed(() => {
  // 当 doctorFilter 变化时，自动重新计算
  // 不需要手动触发
})
```

**优势**：
- 响应式更新
- 代码简洁
- 性能好（Vue 自动缓存）

---

## 样式说明

```scss
.doctor-filter {
  display: flex;
  align-items: center;
  gap: 12px;          // 下拉框之间的间距
  margin-bottom: 20px;
  padding: 16px;       // 内边距
  background: #f5f7fa; // 背景色
  border-radius: 4px;  // 圆角
}
```

---

## 测试验证

### 测试1：科室筛选

**步骤**：
1. 进入某个有科室数据的医院详情页
2. 观察是否显示"选择科室"下拉框
3. 选择一个科室
4. 观察医生列表是否只显示该科室的医生

**预期结果**：
- ✅ 显示科室下拉框
- ✅ 选择科室后，医生列表只显示该科室的医生

### 测试2：职称筛选

**步骤**：
1. 进入任意医院详情页
2. 观察是否显示"选择职称"下拉框
3. 选择一个职称
4. 观察医生列表是否只显示该职称的医生

**预期结果**：
- ✅ 始终显示职称下拉框
- ✅ 选择职称后，医生列表只显示该职称的医生

### 测试3：组合筛选

**步骤**：
1. 同时选择科室和职称
2. 观察医生列表

**预期结果**：
- ✅ 医生列表只显示同时满足两个条件的医生
- ✅ 例如：心血管内科 的主任医师

### 测试4：重置筛选

**步骤**：
1. 设置筛选条件
2. 点击"重置筛选"按钮
3. 观察医生列表

**预期结果**：
- ✅ 筛选条件清空
- ✅ "重置筛选"按钮消失
- ✅ 医生列表显示该医院的全部医生

### 测试5：无科室数据

**步骤**：
1. 进入一个没有科室数据的医院详情页
2. 观察筛选区域

**预期结果**：
- ✅ 不显示科室下拉框
- ✅ 仍然显示职称下拉框
- ✅ 筛选功能正常工作

---

## 常见问题

### Q1：为什么看不到科室筛选？

**A**：可能原因：
1. 该医院没有科室数据
2. 科室数据加载失败

**解决**：
- 仍然可以使用职称筛选
- 职称筛选始终可用

### Q2：筛选后医生列表为空？

**A**：可能原因：
1. 该科室/职称没有医生
2. 组合筛选条件太严格

**解决**：
- 点击"重置筛选"查看全部医生
- 尝试放宽筛选条件

### Q3：筛选功能对整个系统生效吗？

**A**：**不是**。筛选功能只针对当前医院的医生：
- 只筛选您正在查看的这个医院的医生
- 不会跨医院筛选
- 如需查看其他医院的医生，需要返回医院列表重新选择

---

## 总结

✅ **功能完整**：
- 科室筛选（条件显示）
- 职称筛选（始终显示）
- 重置功能
- 实时响应

✅ **范围明确**：
- 只筛选当前医院的医生
- 不会跨医院查询

✅ **用户体验**：
- 界面简洁清晰
- 操作简单直观
- 符合实际就医流程

---

**相关文档**：
- [医生板块融入医院详情页 - 最终方案](./DOCTOR_MERGE_FINAL.md)
- [医院详情页ID验证修复](./HOSPITAL_DETAIL_ID_VALIDATION_FIX.md)
