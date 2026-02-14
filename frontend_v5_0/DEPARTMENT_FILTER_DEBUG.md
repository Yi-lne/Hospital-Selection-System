# 医院详情页科室筛选缺失 - 调试指南

## 问题描述

医院详情页的医生筛选功能中，科室筛选不显示或没有数据。

---

## 可能的原因

### 原因1：数据库中没有科室数据

**检查方法**：
```sql
-- 查看科室表是否有数据
SELECT * FROM hospital_department LIMIT 10;

-- 查看某个医院的科室
SELECT * FROM hospital_department WHERE hospital_id = 1;
```

**解决方法**：
如果 `hospital_department` 表为空，需要执行数据初始化脚本：
```bash
cd sql
mysql -u root -p hospital_selection_db < init_data_part2.sql
```

### 原因2：医生表中的 dept_id 字段没有正确关联科室表

**检查方法**：
```sql
-- 查看医生的科室ID
SELECT id, doctor_name, dept_id, dept_name
FROM doctor_info
WHERE hospital_id = 1
LIMIT 10;

-- 查看科室表的ID
SELECT id, hospital_id, dept_name
FROM hospital_department
WHERE hospital_id = 1;
```

**问题**：
- 如果 `doctor_info.dept_id` 是科室名称（如"心血管内科"）而不是科室ID
- 那么筛选逻辑 `d.deptId === doctorFilter.deptId` 就不会生效

**解决方法**：
需要确保医生表的 `dept_id` 字段存储的是科室表的ID，而不是科室名称。

### 原因3：前端API调用失败

**检查方法**：
打开浏览器控制台（F12），查看网络请求：

1. 访问某个医院详情页（如 `/hospital/1`）
2. 在控制台中查找以下日志：
   ```
   === 加载科室列表 ===
   医院ID: 1
   科室API响应: {...}
   科室数据: [...]
   科室列表加载完成，数量: X
   ```

**可能的错误**：
- `404 Not Found`：API路径错误
- `500 Internal Server Error`：后端错误
- 返回空数组 `[]`：数据库中没有数据

---

## 前端调试步骤

### 步骤1：检查科室数据加载

1. 打开浏览器控制台（F12）
2. 访问某个医院详情页（如 `/hospital/1`）
3. 查看控制台输出

**预期输出**：
```
=== 加载科室列表 ===
医院ID: 1
科室API响应: {code: 200, message: "success", data: [...]}
科室数据: [{id: 1, hospitalId: 1, deptName: "心血管内科", ...}, ...]
科室列表加载完成，数量: 10
```

**如果输出为空或报错**：
- 检查后端API是否正常
- 检查数据库是否有数据

### 步骤2：检查医生数据

**预期输出**：
```
=== 医生列表加载完成 ===
医生数据: [{id: 1, doctorName: "张三", deptId: 1, ...}, ...]
医生数量: 20

医生科室ID示例: [
  {name: "张三", deptId: 1, deptName: "心血管内科"},
  {name: "李四", deptId: 2, deptName: "呼吸内科"},
  {name: "王五", deptId: 1, deptName: "心血管内科"}
]
```

**关键检查**：
- `deptId` 是否为数字（科室ID）
- `deptId` 是否与科室表的 `id` 匹配
- `deptName` 是否正确显示

### 步骤3：测试筛选功能

1. 选择科室筛选（如果有）
2. 选择职称筛选
3. 观察医生列表是否更新

**预期结果**：
- 选择科室后，医生列表只显示该科室的医生
- 选择职称后，医生列表只显示该职称的医生

---

## 后端数据结构检查

### 科室表（hospital_department）

```sql
-- 表结构
CREATE TABLE `hospital_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hospital_id` BIGINT NOT NULL COMMENT '医院ID',
  `dept_name` VARCHAR(50) NOT NULL COMMENT '科室名称',
  `dept_intro` TEXT DEFAULT NULL COMMENT '科室简介',
  `is_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院科室表';
```

**示例数据**：
```sql
INSERT INTO `hospital_department` (`hospital_id`, `dept_name`, `dept_intro`) VALUES
(1, '心血管内科', '心血管内科擅长诊治高血压、冠心病等疾病'),
(1, '呼吸内科', '呼吸内科擅长诊治肺炎、哮喘等疾病'),
(1, '消化内科', '消化内科擅长诊治胃炎、胃溃疡等疾病');
```

### 医生表（doctor_info）

```sql
-- 关键字段
dept_id BIGINT COMMENT '科室ID（关联hospital_department表）',
dept_name VARCHAR(50) COMMENT '科室名称（冗余字段）',
```

**正确的关联**：
- `doctor_info.dept_id` = `hospital_department.id`
- `doctor_info.dept_name` = `hospital_department.dept_name`

---

## 常见问题和解决方案

### 问题1：科室筛选下拉框不显示

**原因**：`departments.value` 为空数组

**检查**：
```javascript
// 在控制台中执行
console.log(departments.value)
```

**解决**：
- 如果数据库中没有数据，需要执行数据初始化脚本
- 如果API调用失败，检查后端服务是否正常运行

### 问题2：科室筛选下拉框显示，但选择后医生列表没变化

**原因**：医生的 `deptId` 和科室的 `id` 不匹配

**检查**：
```javascript
// 在控制台中执行
console.log('科室ID列表:', departments.value.map(d => d.id))
console.log('医生科室ID:', doctors.value.map(d => d.deptId))
```

**解决**：
- 如果医生的 `deptId` 是 `null` 或不匹配，需要更新数据库
- 确保医生表的 `dept_id` 字段正确关联到科室表

### 问题3：数据库有科室数据，但前端加载失败

**原因**：API路径错误或后端服务未启动

**检查**：
1. 确认后端服务是否启动
2. 确认API路径：`GET /api/department/hospital/{hospitalId}`
3. 查看浏览器Network标签页的请求详情

---

## 修复步骤

### 步骤1：检查数据库

```bash
# 连接数据库
mysql -u root -p

# 使用数据库
USE hospital_selection_db;

# 查看科室数据
SELECT * FROM hospital_department LIMIT 10;

# 查看医生数据的科室关联
SELECT d.id, d.doctor_name, d.dept_id, d.dept_name, hd.id as dept_table_id
FROM doctor_info d
LEFT JOIN hospital_department hd ON d.dept_id = hd.id
WHERE d.hospital_id = 1
LIMIT 10;
```

### 步骤2：如果数据库为空，执行数据初始化

```bash
cd sql
mysql -u root -p hospital_selection_db < init_data_part2.sql
```

### 步骤3：刷新前端页面测试

1. 刷新医院详情页
2. 打开浏览器控制台
3. 查看调试日志
4. 测试科室筛选功能

---

## 临时解决方案

如果科室数据暂时无法获取，可以考虑：

### 方案A：从医生数据中提取科室

```javascript
// 从医生列表中提取不重复的科室
const uniqueDepartments = computed(() => {
  const deptMap = new Map()
  doctors.value.forEach(doctor => {
    if (doctor.deptName && !deptMap.has(doctor.deptName)) {
      deptMap.set(doctor.deptName, {
        id: doctor.deptId,  // 可能为undefined
        deptName: doctor.deptName,
        // 暂时使用deptName作为标识
        _tempId: doctor.deptName
      })
    }
  })
  return Array.from(deptMap.values())
})
```

**然后修改筛选逻辑**：
```javascript
const filteredDoctors = computed(() => {
  let result = doctors.value

  // 如果有科室ID筛选，使用ID匹配
  if (doctorFilter.value.deptId !== undefined) {
    result = result.filter(d => d.deptId === doctorFilter.value.deptId)
  }
  // 如果没有科室ID但有科室名称，使用名称匹配
  else if (doctorFilter.value.deptName) {
    result = result.filter(d => d.deptName === doctorFilter.value.deptName)
  }

  // 职称筛选
  if (doctorFilter.value.title) {
    result = result.filter(d => d.title === doctorFilter.value.title)
  }

  return result
})
```

### 方案B：使用科室名称而不是科室ID

```vue
<!-- 修改为使用科室名称 -->
<el-select
  v-model="doctorFilter.deptName"
  placeholder="选择科室"
  clearable
>
  <el-option
    v-for="dept in uniqueDepartments"
    :key="dept._tempId"
    :label="dept.deptName"
    :value="dept.deptName"
  />
</el-select>
```

---

## 总结

✅ **调试已完成**：
- 添加了详细的控制台日志
- 可以清楚地看到科室数据和医生数据

✅ **下一步操作**：
1. 刷新医院详情页
2. 打开浏览器控制台（F12）
3. 查看日志输出
4. 根据日志判断问题所在
5. 应用相应的解决方案

---

**相关文档**：
- [医院详情页医生筛选功能说明](./HOSPITAL_DETAIL_DOCTOR_FILTER.md)
