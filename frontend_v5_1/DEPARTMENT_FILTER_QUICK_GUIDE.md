# 科室筛选优化 - 快速指南

## 问题与解决

### ❌ 原问题
科室筛选显示医院的所有科室，但某些科室没有医生，导致选择后医生列表为空。

### ✅ 解决方案
科室筛选**只显示有医生的科室**，从医生数据中动态提取。

---

## 修改内容

### 1. 删除了科室API调用
- ❌ 不再调用 `getHospitalDepartments(id)`
- ❌ 删除了 `departments` ref
- ✅ 不需要单独加载科室数据

### 2. 新增计算属性
```javascript
const departmentsWithDoctors = computed(() => {
  // 从医生列表中动态提取有医生的科室
  // 自动去重和排序
})
```

### 3. 更新模板
```vue
<!-- 修改前 -->
<el-select v-for="dept in departments">

<!-- 修改后 -->
<el-select v-for="dept in departmentsWithDoctors">
```

---

## 效果对比

| 项目 | 修改前 | 修改后 |
|------|--------|--------|
| 数据源 | 科室表 | 医生表 |
| 显示科室 | 所有科室 | 有医生的科室 |
| 空列表问题 | ❌ 存在 | ✅ 不存在 |
| API调用 | 2次（医生+科室） | 1次（仅医生） |

---

## 验证方法

1. 刷新前端页面（Ctrl+F5）
2. 访问任意医院详情页
3. 查看科室筛选下拉框
4. 选择任意科室，验证医生列表不为空

**预期控制台输出**：
```
=== 有医生的科室列表 ===
科室数量: 8
科室列表: [...]
```

---

## 文件修改

**修改文件**: `frontend/src/views/hospital/HospitalDetail.vue`

**主要变更**：
- 删除 `getHospitalDepartments` 导入
- 删除 `departments` ref
- 添加 `departmentsWithDoctors` 计算属性
- 添加 `logDepartments` 调试函数
- 更新模板使用 `departmentsWithDoctors`

---

**详细说明**: 见 `DEPARTMENT_FILTER_OPTIMIZATION.md`

---

**状态**: ✅ 已完成
**影响**: 所有医院详情页的科室筛选功能
