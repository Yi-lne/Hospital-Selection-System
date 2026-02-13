# 科室筛选优化 - 从医生数据中动态提取科室

## 问题描述

**原问题**：科室筛选下拉框显示了该医院的**所有科室**，但其中某些科室可能没有医生。当用户选择没有医生的科室时，医生列表显示为空，用户体验不佳。

## 解决方案

**新逻辑**：科室筛选下拉框**只显示有医生的科室**

从医生列表中动态提取科室，确保每个显示的科室都有对应的医生。

---

## 代码修改

### 1. 删除科室API调用

**修改前**：
```javascript
import { getHospitalDepartments } from '@/api/department'
const departments = ref<any[]>([])

// 加载科室列表
const deptRes = await getHospitalDepartments(id)
departments.value = deptRes.data || []
```

**修改后**：
```javascript
// 删除了 getHospitalDepartments 的导入
// 删除了 departments ref
// 删除了加载科室列表的代码
```

### 2. 添加计算属性动态提取科室

```javascript
// 从医生列表中动态提取有医生的科室（用于科室筛选下拉框）
const departmentsWithDoctors = computed(() => {
  const deptMap = new Map<number, { id: number; deptName: string }>()

  doctors.value.forEach(doctor => {
    // 只添加有有效 deptId 和 deptName 的科室
    if (doctor.deptId && doctor.deptName && !deptMap.has(doctor.deptId)) {
      deptMap.set(doctor.deptId, {
        id: doctor.deptId,
        deptName: doctor.deptName
      })
    }
  })

  // 转换为数组并按科室名称排序
  return Array.from(deptMap.values()).sort((a, b) =>
    a.deptName.localeCompare(b.deptName, 'zh-CN')
  )
})
```

**工作原理**：
1. 遍历该医院的所有医生
2. 提取每个医生的 `deptId` 和 `deptName`
3. 使用 `Map` 去重（每个科室只添加一次）
4. 按科室名称拼音排序

### 3. 添加调试日志

```javascript
// 调试：输出科室列表
const logDepartments = () => {
  console.log('=== 有医生的科室列表 ===')
  console.log('科室数量:', departmentsWithDoctors.value.length)
  console.log('科室列表:', departmentsWithDoctors.value.map(d => ({
    id: d.id,
    name: d.deptName
  })))
}

// 在医生列表加载后调用
logDepartments()
```

### 4. 更新模板使用计算属性

**修改前**：
```vue
<el-select
  v-if="departments && departments.length > 0"
  v-model="doctorFilter.deptId"
  placeholder="选择科室"
>
  <el-option
    v-for="dept in departments"
    :key="dept.id"
    :label="dept.deptName"
    :value="dept.id"
  />
</el-select>
```

**修改后**：
```vue
<el-select
  v-if="departmentsWithDoctors && departmentsWithDoctors.length > 0"
  v-model="doctorFilter.deptId"
  placeholder="选择科室"
>
  <el-option
    v-for="dept in departmentsWithDoctors"
    :key="dept.id"
    :label="dept.deptName"
    :value="dept.id"
  />
</el-select>
```

---

## 对比说明

### 修改前的问题

```
医院：四川大学华西医院
├── 科室表数据：
│   ├── 心血管内科 (ID=1001)
│   ├── 肿瘤科 (ID=1002)
│   ├── 神经内科 (ID=1003)
│   ├── 骨科 (ID=1004)
│   └── 疼痛科 (ID=1005) ← 没有医生
│
└── 医生表数据：
    ├── 张医生 (deptId=1001, 心血管内科)
    ├── 李医生 (deptId=1001, 心血管内科)
    ├── 王医生 (deptId=1002, 肿瘤科)
    └── 赵医生 (deptId=1003, 神经内科)

问题：科室筛选显示5个科室，但"疼痛科"没有医生
结果：用户选择"疼痛科"后，医生列表为空 ❌
```

### 修改后的效果

```
医院：四川大学华西医院
└── 医生表数据：
    ├── 张医生 (deptId=1001, 心血管内科)
    ├── 李医生 (deptId=1001, 心血管内科)
    ├── 王医生 (deptId=1002, 肿瘤科)
    └── 赵医生 (deptId=1003, 神经内科)

从医生数据中提取科室：
├── 心血管内科 (ID=1001) - 2名医生 ✅
├── 肿瘤科 (ID=1002) - 1名医生 ✅
└── 神经内科 (ID=1003) - 1名医生 ✅

结果：科室筛选只显示3个有医生的科室 ✅
```

---

## 优势

1. **避免空列表**：用户不会选择没有医生的科室
2. **更好的用户体验**：每个可选科室都有对应的医生
3. **数据驱动**：科室列表自动根据医生数据变化
4. **减少API调用**：不再需要单独调用科室接口
5. **性能优化**：使用计算属性，自动响应医生数据变化

---

## 验证测试

### 测试步骤

1. 访问任意医院详情页
2. 打开浏览器控制台（F12）
3. 查看科室筛选下拉框

### 预期控制台输出

```
=== 有医生的科室列表 ===
科室数量: 8
科室列表: [
  { id: 1001, name: "儿科" },
  { id: 1002, name: "妇产科" },
  { id: 1003, name: "呼吸内科" },
  { id: 1004, name: "骨科" },
  { id: 1005, name: "泌尿外科" },
  { id: 1006, name: "神经内科" },
  { id: 1007, name: "心血管内科" },
  { id: 1008, name: "肿瘤科" }
]
```

### 预期界面效果

- ✅ 科室筛选下拉框只显示有医生的科室
- ✅ 选择任意科室后，医生列表至少显示1名医生
- ✅ 不会出现"选择科室后医生列表为空"的情况

---

## 技术要点

### 1. 计算属性 vs Ref

使用 `computed` 而不是 `ref`：
- 自动响应 `doctors` 数组的变化
- 当医生数据更新时，科室列表自动重新计算
- 性能更好（Vue 自动缓存）

### 2. Map 去重

使用 `Map` 数据结构去重：
- `deptMap.has(doctor.deptId)` - 检查是否已存在
- `deptMap.set(doctor.deptId, {...})` - 添加科室
- 确保每个科室只添加一次

### 3. 中文排序

使用 `localeCompare` 进行中文拼音排序：
```javascript
.sort((a, b) => a.deptName.localeCompare(b.deptName, 'zh-CN'))
```

这样"儿科"会排在"肿瘤科"前面。

---

## 相关文件

- `frontend/src/views/hospital/HospitalDetail.vue` - 医院详情页
- `frontend/src/api/department.ts` - 科室API（不再使用）
- `frontend/src/api/doctor.ts` - 医生API

---

## 后续优化建议

1. **添加科室医生数量显示**
   ```vue
   <el-option
     :label="`${dept.deptName} (${getDoctorCount(dept.id)})`"
   />
   ```

2. **添加热门科室排序**
   - 将医生数量多的科室排在前面
   - 或根据医院的重点科室排序

3. **添加科室图标**
   - 为每个科室配置对应的图标
   - 提升视觉效果

---

**最后更新**: 2026-02-06
**修改文件**: `frontend/src/views/hospital/HospitalDetail.vue`
**状态**: ✅ 已完成并测试通过
