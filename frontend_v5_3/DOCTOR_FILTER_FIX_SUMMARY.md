# 医生板块筛选功能修复总结

## 修复时间
2026-02-06

## 问题描述

医生列表页面的筛选功能无效，无法按医院、科室、职称进行筛选。

---

## 根本原因

### 原因1：调用了错误的API接口

**问题**：
- 前端调用的是 `GET /api/doctor/list` 接口
- 该接口只支持分页参数（`page`、`pageSize`），不支持筛选参数
- 筛选参数（`hospitalId`、`deptId`、`title`）被后端忽略

**正确的接口**：
- 应该调用 `POST /api/doctor/filter` 接口
- 该接口支持完整筛选参数：`hospitalId`、`deptId`、`title`、`diseaseCode`、`provinceCode`、`cityCode`

### 原因2：科室下拉框没有数据

**问题**：
- `departments` 数组始终为空
- 没有加载科室数据的逻辑
- 科室API（`GET /api/department/hospital/{hospitalId}`）需要医院ID参数

**解决方案**：
- 选择医院时，动态加载该医院的科室列表
- 科室下拉框在未选择医院时禁用

### 原因3：医院列表字段映射问题

**问题**：
- `getHospitalList` 返回的数据字段名不一致
- 有的记录使用 `hospitalName`，有的使用 `name`

**解决方案**：
- 在 `loadHospitals` 中统一映射为 `hospitalName` 字段

---

## 修复内容

### 1. 添加医生筛选API（doctor.js）

**修改前**：
```javascript
export function getDoctorList(params) {
  return Request.get('/doctor/list', { params })
}
```

**修改后**：
```javascript
export function getDoctorList(params) {
  return Request.get('/doctor/list', { params })
}

// ✅ 新增：医生筛选接口
export function filterDoctors(data) {
  return Request.post('/doctor/filter', data)
}
```

---

### 2. 修改DoctorList.vue逻辑

#### 修改导入

**修改前**：
```javascript
import { getDoctorList } from '@/api/doctor'
```

**修改后**：
```javascript
import { filterDoctors } from '@/api/doctor'
import { getHospitalDepartments } from '@/api/department'
```

#### 修改loadDoctors函数

**修改前**：
```javascript
const loadDoctors = async () => {
  try {
    loading.value = true
    const res = await getDoctorList(filterParams)  // ❌ 错误的API
    doctorList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('Failed to load doctors:', error)
  } finally {
    loading.value = false
  }
}
```

**修改后**：
```javascript
const loadDoctors = async () => {
  try {
    loading.value = true
    console.log('=== 加载医生列表 ===')
    console.log('筛选参数:', filterParams)

    // ✅ 使用筛选接口
    const res = await filterDoctors(filterParams)
    doctorList.value = res.data.list || []
    total.value = res.data.total || 0

    console.log('医生列表长度:', doctorList.value.length)
    console.log('总数:', total.value)
  } catch (error) {
    console.error('Failed to load doctors:', error)
  } finally {
    loading.value = false
  }
}
```

#### 新增loadDepartments函数

```javascript
// ✅ 新增：加载选中医院的科室列表
const loadDepartments = async (hospitalId) => {
  if (!hospitalId) {
    departments.value = []
    filterParams.deptId = undefined
    return
  }

  try {
    const res = await getHospitalDepartments(hospitalId)
    departments.value = res.data || []
    console.log('科室列表加载完成，数量:', departments.value.length)

    // 重置科室选择
    filterParams.deptId = undefined
  } catch (error) {
    console.error('Failed to load departments:', error)
    departments.value = []
  }
}
```

#### 修改loadHospitals函数

**修改前**：
```javascript
const loadHospitals = async () => {
  try {
    const res = await getHospitalList({ page: 1, pageSize: 1000 })
    hospitals.value = res.data.list  // ❌ 字段名不一致
  } catch (error) {
    console.error('Failed to load hospitals:', error)
  }
}
```

**修改后**：
```javascript
const loadHospitals = async () => {
  try {
    const res = await getHospitalList({ page: 1, pageSize: 1000 })
    // ✅ 统一字段名映射
    hospitals.value = (res.data.list || []).map(item => ({
      id: item.id,
      hospitalName: item.hospitalName || item.name
    }))
    console.log('医院列表加载完成，数量:', hospitals.value.length)
  } catch (error) {
    console.error('Failed to load hospitals:', error)
  }
}
```

#### 新增handleHospitalChange函数

```javascript
// ✅ 新增：医院选择变化处理
const handleHospitalChange = (hospitalId) => {
  console.log('医院选择变化:', hospitalId)
  filterParams.hospitalId = hospitalId
  filterParams.page = 1

  // 加载该医院的科室
  loadDepartments(hospitalId)

  // 重新加载医生列表
  loadDoctors()
}
```

#### 修改resetFilters函数

**修改前**：
```javascript
const resetFilters = () => {
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  loadDoctors()
}
```

**修改后**：
```javascript
const resetFilters = () => {
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  departments.value = []  // ✅ 清空科室列表
  loadDoctors()
}
```

---

### 3. 修改DoctorList.vue模板

#### 修改医院下拉框

**修改前**：
```vue
<el-select
  v-model="filterParams.hospitalId"
  placeholder="选择医院"
  clearable
  @change="handleFilterChange"  <!-- ❌ 通用处理函数 -->
>
```

**修改后**：
```vue
<el-select
  v-model="filterParams.hospitalId"
  placeholder="选择医院"
  clearable
  @change="handleHospitalChange"  <!-- ✅ 专用处理函数 -->
>
```

#### 修改科室下拉框

**修改前**：
```vue
<el-select
  v-model="filterParams.deptId"
  placeholder="选择科室"
  clearable
  @change="handleFilterChange"
>
```

**修改后**：
```vue
<el-select
  v-model="filterParams.deptId"
  placeholder="选择科室"
  clearable
  :disabled="!filterParams.hospitalId"  <!-- ✅ 未选择医院时禁用 -->
  @change="handleFilterChange"
>
```

---

## 功能改进

### 1. 级联筛选

**实现方式**：
1. 用户选择医院
2. 自动加载该医院的科室列表
3. 科室下拉框从禁用变为启用
4. 用户可以进一步筛选科室

### 2. 调试日志

添加了详细的控制台日志：
- 医院列表加载完成
- 科室列表加载完成
- 医生列表筛选参数
- 医生列表结果统计

### 3. 用户体验改进

- ✅ 科室下拉框在未选择医院时显示禁用状态
- ✅ 切换医院时自动清空科室选择
- ✅ 重置按钮清空所有筛选条件
- ✅ 选择医院后立即加载医生列表

---

## 测试验证

### 测试1：医院筛选

**步骤**：
1. 访问医生列表页：`http://localhost:5173/doctor`
2. 在"选择医院"下拉框中选择"北京协和医院"
3. 观察医生列表

**预期结果**：
- ✅ 科室下拉框从禁用变为启用
- ✅ 科室下拉框显示北京协和医院的科室列表
- ✅ 医生列表只显示北京协和医院的医生
- ✅ 控制台输出：`医院选择变化: 1`（假设医院ID为1）

### 测试2：科室筛选

**步骤**：
1. 选择医院"北京协和医院"
2. 在"选择科室"下拉框中选择"心血管内科"
3. 观察医生列表

**预期结果**：
- ✅ 医生列表只显示北京协和医院心血管内科的医生

### 测试3：职称筛选

**步骤**：
1. 在"选择职称"下拉框中选择"主任医师"
2. 观察医生列表

**预期结果**：
- ✅ 医生列表只显示主任医师职称的医生

### 测试4：组合筛选

**步骤**：
1. 选择医院"北京协和医院"
2. 选择科室"心血管内科"
3. 选择职称"主任医师"
4. 观察医生列表

**预期结果**：
- ✅ 医生列表只显示北京协和医院心血管内科的主任医师

### 测试5：重置筛选

**步骤**：
1. 设置多个筛选条件
2. 点击"重置"按钮
3. 观察页面状态

**预期结果**：
- ✅ 所有下拉框恢复默认状态
- ✅ 科室下拉框恢复禁用状态
- ✅ 医生列表显示所有医生

### 测试6：切换医院

**步骤**：
1. 选择医院"北京协和医院"
2. 选择科室"心血管内科"
3. 切换医院为"中国人民解放军总医院"
4. 观察科室下拉框

**预期结果**：
- ✅ 科室下拉框清空并显示新医院的科室
- ✅ 医生列表更新为新医院的医生

---

## API对比

### 旧接口（不支持筛选）

**请求**：
```http
GET /api/doctor/list?page=1&pageSize=12&hospitalId=1
```

**问题**：
- `hospitalId` 参数被忽略
- 始终返回所有医生（只分页）

### 新接口（支持筛选）

**请求**：
```http
POST /api/doctor/filter
Content-Type: application/json

{
  "page": 1,
  "pageSize": 12,
  "hospitalId": 1,
  "deptId": 10,
  "title": "主任医师"
}
```

**优点**：
- 支持多条件筛选
- 返回符合条件的医生

---

## 文件修改清单

| 文件 | 修改内容 | 状态 |
|------|---------|------|
| `frontend/src/api/doctor.js` | 添加 `filterDoctors` 方法 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 修改导入，添加科室API导入 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 修改 `loadDoctors` 使用筛选API | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 修改 `loadHospitals` 字段映射 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增 `loadDepartments` 函数 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增 `handleHospitalChange` 函数 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 修改 `resetFilters` 清空科室 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 模板：添加禁用状态和事件 | ✅ 已完成 |

---

## 数据流程

### 选择医院的流程

```
用户选择医院
  ↓
handleHospitalChange(hospitalId)
  ↓
filterParams.hospitalId = hospitalId
  ↓
loadDepartments(hospitalId)  // 加载科室列表
  ↓
departments.value = [...]
  ↓
loadDoctors()  // 加载医生列表
  ↓
POST /api/doctor/filter
  ↓
更新医生列表UI
```

### 选择科室的流程

```
用户选择科室
  ↓
handleFilterChange()
  ↓
filterParams.deptId = deptId
filterParams.page = 1
  ↓
loadDoctors()  // 加载医生列表
  ↓
POST /api/doctor/filter
  ↓
更新医生列表UI
```

---

## 总结

✅ **问题已修复**：
1. 使用正确的筛选API（`POST /api/doctor/filter`）
2. 实现医院选择后动态加载科室
3. 统一医院列表字段映射
4. 添加详细的调试日志

✅ **功能改进**：
1. 级联筛选（医院 → 科室）
2. 科室下拉框禁用状态
3. 切换医院时自动清空科室
4. 完整的调试日志

✅ **下一步**：
1. 刷新前端页面测试
2. 验证筛选功能
3. 检查控制台日志

---

**相关文档**：
- 后端API文档：`backend/src/main/java/com/chen/HospitalSelection/controller/DoctorController.java`
- 后端DTO定义：`backend/src/main/java/com/chen/HospitalSelection/dto/DoctorFilterDTO.java`
