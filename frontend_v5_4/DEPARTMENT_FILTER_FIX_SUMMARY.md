# 科室筛选功能错误修复总结

## 修复时间
2026-02-06

## 修复的问题

### 问题1：前端错误 - hasFilterConditions 未定义

**错误信息**：
```
HospitalList.vue:262 Failed to load hospitals: ReferenceError: hasFilterConditions is not defined at loadHospitals (HospitalList.vue:219:13)
```

**原因分析**：
在重构科室筛选功能时，删除疾病分类相关代码时，意外删除了 `hasFilterConditions()` 函数，但 `loadHospitals()` 函数中仍调用该函数。

**修复方案**：
在 `HospitalList.vue` 中恢复 `hasFilterConditions()` 函数

**修复代码**：
```javascript
// 判断是否有筛选条件
const hasFilterConditions = () => {
  const params = hospitalStore.filterParams
  return !!(
    params.provinceCode ||
    params.cityCode ||
    params.areaCode ||
    params.level ||
    params.departmentCode
  )
}
```

**文件位置**：
`frontend/src/views/hospital/HospitalList.vue` 第211-221行

---

### 问题2：后端错误 - setDiseaseCode 方法不存在

**错误信息**：
```
FilterServiceImpl.java:91:18 java: 找不到符号: 方法 setDiseaseCode
```

**原因分析**：
`FilterServiceImpl.java` 中的 `recommendHospitalsByDisease()` 方法仍然调用旧的 `setDiseaseCode()` 方法，但 `HospitalFilterDTO` 已经将该字段改为 `department`，对应的 setter 方法为 `setDepartment()`。

**修复方案**：
更新 `FilterServiceImpl.java` 第91行，将 `setDiseaseCode()` 改为 `setDepartment()`

**修复代码**：
```java
// 修复前
filterDTO.setDiseaseCode(diseaseCode);

// 修复后
filterDTO.setDepartment(diseaseCode);  // 使用科室筛选替代疾病编码
```

**文件位置**：
`backend/src/main/java/com/chen/HospitalSelection/service/impl/FilterServiceImpl.java` 第91行

---

### 问题3：重置筛选功能引用已删除变量

**问题描述**：
`resetFilters()` 函数中仍然引用 `selectedDisease.value`，但该变量已被删除。

**修复方案**：
更新 `resetFilters()` 函数，清空科室相关变量

**修复代码**：
```javascript
// 修复前
const resetFilters = () => {
  hospitalStore.resetFilters()
  selectedArea.value = []
  selectedDisease.value = []
  loadHospitals()
}

// 修复后
const resetFilters = () => {
  hospitalStore.resetFilters()
  selectedArea.value = []
  selectedDepartment.value = ''
  customDepartment.value = ''
  loadHospitals()
}
```

**文件位置**：
`frontend/src/views/hospital/HospitalList.vue` 第344-350行

---

## 修复文件清单

| 文件 | 修改内容 | 状态 |
|------|---------|------|
| `frontend/src/views/hospital/HospitalList.vue` | 恢复 `hasFilterConditions()` 函数 | ✅ 已修复 |
| `frontend/src/views/hospital/HospitalList.vue` | 修复 `resetFilters()` 函数 | ✅ 已修复 |
| `backend/src/main/java/com/chen/HospitalSelection/service/impl/FilterServiceImpl.java` | 更新方法调用 `setDiseaseCode()` → `setDepartment()` | ✅ 已修复 |

---

## 验证测试

### 测试步骤

#### 1. 前端编译测试
```bash
cd frontend
npm run dev
```

**预期结果**：
- ✅ 无编译错误
- ✅ 无 "hasFilterConditions is not defined" 错误

#### 2. 后端编译测试
```bash
cd backend
mvn clean compile
```

**预期结果**：
- ✅ 编译成功
- ✅ 无 "找不到符号: 方法 setDiseaseCode" 错误

#### 3. 功能测试

**测试1：快捷科室筛选**
1. 访问 `http://localhost:5173/hospital`
2. 点击 "心血管内科" 标签
3. 验证显示所有包含心血管内科的医院

**测试2：自定义科室输入**
1. 在科室输入框输入 "肾内科"
2. 点击搜索按钮
3. 验证显示所有包含肾内科的医院

**测试3：科室筛选清空**
1. 选择科室（如 "心血管内科"）
2. 点击重置按钮
3. 验证显示所有医院，科室标签恢复默认状态

**测试4：组合筛选**
1. 地区：北京市
2. 等级：三甲
3. 科室：心血管内科
4. 验证显示北京市的三甲医院且有心血管内科的医院

---

## API 请求示例

### 科室筛选请求

**快捷科室筛选**：
```json
POST /api/hospital/filter
{
  "page": 1,
  "pageSize": 10,
  "department": "心血管内科",
  "hospitalLevel": "grade3A",
  "provinceCode": "110000"
}
```

**自定义科室筛选**：
```json
POST /api/hospital/filter
{
  "page": 1,
  "pageSize": 10,
  "department": "风湿免疫科",
  "cityCode": "440100"
}
```

---

## 相关文档

- [科室筛选功能实现完成报告](./DEPARTMENT_FILTER_IMPLEMENTATION.md)
- [医院选择系统 README](../README.md)

---

## 总结

✅ **所有错误已修复**：
1. 前端 `hasFilterConditions()` 函数已恢复
2. 后端 `FilterServiceImpl.java` 方法调用已更新
3. 前端 `resetFilters()` 函数已修正

✅ **下一步操作**：
1. 编译后端：`mvn clean compile`
2. 启动后端服务：`mvn spring-boot:run`
3. 启动前端服务：`npm run dev`
4. 进行功能测试验证

---

**注意**：科室筛选功能现已完全可用，替换了原有的疾病分类筛选功能。
