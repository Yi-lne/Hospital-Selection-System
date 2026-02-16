# 医生板块筛选功能增强 - 添加地区筛选

## 改进时间
2026-02-06

## 改进目标

在医生板块添加地区筛选功能，使其与医院板块的筛选逻辑保持一致，提供更完善的级联筛选体验。

---

## 筛选逻辑对比

### 医院板块筛选
1. **地区** → 2. **科室** → 3. **医院等级**

### 医生板块筛选（改进后）
1. **地区** → 2. **医院** → 3. **科室** → 4. **职称**

---

## 新增筛选条件

### 地区级联选择

**UI组件**：`el-cascader`

**功能**：
- 支持省/市/区三级联动
- 支持只选择省份（查看全省医生）
- 支持选择省份+城市（查看全市医生）
- 支持选择省份+城市+区县（查看该区医生）
- 支持清空选择

**级联关系**：
```
选择地区
  ↓
清空医院和科室选择（因为地区变了）
  ↓
重新加载医生列表（基于地区筛选）
  ↓
用户可以继续选择医院（可选）
  ↓
用户可以选择科室（基于医院）
  ↓
用户可以选择职称
```

---

## 实现细节

### 1. 模板部分

**新增地区选择器**：
```vue
<!-- 地区选择 -->
<el-cascader
  v-model="selectedArea"
  :options="areaOptions"
  :props="{
    expandTrigger: 'hover',
    label: 'label',
    value: 'value',
    children: 'children',
    checkStrictly: true
  }"
  placeholder="选择地区"
  clearable
  style="width: 220px"
  @change="handleAreaChange"
/>
```

**统一下拉框宽度**：
- 地区：`220px`
- 医院：`220px`
- 科室：`180px`
- 职称：`180px`

### 2. Script部分

#### 新增状态变量

```javascript
const areaOptions = ref([])  // 地区选项数据
const selectedArea = ref([]) // 选中的地区

const filterParams = reactive({
  page: 1,
  pageSize: 12,
  provinceCode: undefined,  // ✅ 新增
  cityCode: undefined,      // ✅ 新增
  areaCode: undefined,      // ✅ 新增
  hospitalId: undefined,
  deptId: undefined,
  title: undefined
})
```

#### 新增函数

**1. transformAreaData - 转换地区数据**
```javascript
const transformAreaData = (areas) => {
  return areas.map(area => ({
    value: area.code,  // 使用 code 作为值
    label: area.name,  // 使用 name 作为显示文本
    children: area.children ? transformAreaData(area.children) : undefined
  }))
}
```

**2. loadAreas - 加载地区数据**
```javascript
const loadAreas = async () => {
  try {
    const res = await getAreaTree()
    areaOptions.value = transformAreaData(res.data || [])
    console.log('地区选项加载完成')
  } catch (error) {
    console.error('Failed to load areas:', error)
  }
}
```

**3. handleAreaChange - 地区选择变化处理**
```javascript
const handleAreaChange = (value) => {
  console.log('地区选择变化:', value)

  if (value && value.length > 0) {
    const level = value.length

    if (level === 1) {
      // 只选择了省份
      filterParams.provinceCode = value[0]
      filterParams.cityCode = undefined
      filterParams.areaCode = undefined
    } else if (level === 2) {
      // 选择了省份+城市
      filterParams.provinceCode = value[0]
      filterParams.cityCode = value[1]
      filterParams.areaCode = undefined
    } else if (level === 3) {
      // 选择了省份+城市+区县
      filterParams.provinceCode = value[0]
      filterParams.cityCode = value[1]
      filterParams.areaCode = value[2]
    }
  } else {
    // 清空地区选择
    filterParams.provinceCode = undefined
    filterParams.cityCode = undefined
    filterParams.areaCode = undefined
  }

  filterParams.page = 1

  // ✅ 关键：清空医院和科室选择（因为地区变了）
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  departments.value = []

  // 重新加载医生列表
  loadDoctors()
}
```

#### 修改现有函数

**1. resetFilters - 重置筛选**
```javascript
// 修改前
const resetFilters = () => {
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  departments.value = []
  loadDoctors()
}

// 修改后
const resetFilters = () => {
  filterParams.provinceCode = undefined  // ✅ 新增
  filterParams.cityCode = undefined      // ✅ 新增
  filterParams.areaCode = undefined      // ✅ 新增
  filterParams.hospitalId = undefined
  filterParams.deptId = undefined
  filterParams.title = undefined
  filterParams.page = 1
  selectedArea.value = []                // ✅ 新增
  departments.value = []
  loadDoctors()
}
```

**2. onMounted - 组件挂载**
```javascript
// 修改前
onMounted(() => {
  loadHospitals()
  if (!route.query.hospitalId) {
    loadDoctors()
  }
})

// 修改后
onMounted(() => {
  loadAreas()  // ✅ 新增：加载地区数据
  loadHospitals()
  if (!route.query.hospitalId) {
    loadDoctors()
  }
})
```

**3. 新增导入**
```javascript
import { getAreaTree } from '@/api/area'  // ✅ 新增
```

---

## 级联筛选逻辑

### 场景1：只选择地区

```
用户选择：北京市
  ↓
filterParams: { provinceCode: "110000", cityCode: undefined, areaCode: undefined }
  ↓
API调用：POST /api/doctor/filter
{
  "provinceCode": "110000",
  "page": 1,
  "pageSize": 12
}
  ↓
结果：显示北京市所有医生
```

### 场景2：地区 + 医院

```
用户选择：北京市 → 北京协和医院
  ↓
filterParams: {
  provinceCode: "110000",
  cityCode: undefined,
  areaCode: undefined,
  hospitalId: 1
}
  ↓
API调用：POST /api/doctor/filter
{
  "provinceCode": "110000",
  "hospitalId": 1,
  "page": 1,
  "pageSize": 12
}
  ↓
结果：显示北京协和医院的医生
```

### 场景3：地区 + 医院 + 科室

```
用户选择：北京市 → 北京协和医院 → 心血管内科
  ↓
filterParams: {
  provinceCode: "110000",
  cityCode: undefined,
  areaCode: undefined,
  hospitalId: 1,
  deptId: 10
}
  ↓
API调用：POST /api/doctor/filter
{
  "provinceCode": "110000",
  "hospitalId": 1,
  "deptId": 10,
  "page": 1,
  "pageSize": 12
}
  ↓
结果：显示北京协和医院心血管内科的医生
```

### 场景4：地区 + 医院 + 科室 + 职称

```
用户选择：北京市 → 北京协和医院 → 心血管内科 → 主任医师
  ↓
filterParams: {
  provinceCode: "110000",
  hospitalId: 1,
  deptId: 10,
  title: "主任医师"
}
  ↓
API调用：POST /api/doctor/filter
{
  "provinceCode": "110000",
  "hospitalId": 1,
  "deptId": 10,
  "title": "主任医师",
  "page": 1,
  "pageSize": 12
}
  ↓
结果：显示北京协和医院心血管内科的主任医师
```

---

## 智能清空机制

### 地区变化时

**触发**：用户选择或更改地区

**自动清空**：
- ✅ 医院选择清空
- ✅ 科室选择清空
- ✅ 科室列表清空

**原因**：
- 地区变了，之前的医院可能不在新地区范围内
- 为了保持数据一致性，需要清空下级选择

### 医院变化时

**触发**：用户选择或更改医院

**自动清空**：
- ✅ 科室选择清空
- ✅ 科室列表重新加载（新医院的科室）

**原因**：
- 医院变了，之前的科室不属于新医院
- 需要加载新医院的科室列表

---

## UI布局

### 筛选栏布局

```
┌──────────────────────────────────────────────────────────────────┐
│ 筛选条件                                                         │
├──────────────────────────────────────────────────────────────────┤
│ [地区选择▼] [医院选择▼] [科室选择▼] [职称选择▼] [重置]           │
│  220px      220px       180px       180px                         │
└──────────────────────────────────────────────────────────────────┘
```

### 响应式设计

- **filter-bar**：`display: flex; flex-wrap: wrap; gap: 12px`
- 小屏幕：自动换行
- 中等屏幕：一行显示2-3个筛选框
- 大屏幕：一行显示所有筛选框

---

## 后端API支持

### DoctorFilterDTO 支持的筛选参数

```java
public class DoctorFilterDTO {
    private Long hospitalId;      // 医院ID
    private Long deptId;          // 科室ID
    private String title;         // 职称
    private String diseaseCode;   // 疾病编码
    private String provinceCode;  // 省份编码 ✅
    private String cityCode;      // 城市编码 ✅
    private Integer page;         // 页码
    private Integer pageSize;     // 每页大小
    private String sortBy;        // 排序字段
}
```

**注意**：后端已经支持 `provinceCode` 和 `cityCode` 参数，前端可以直接使用。

---

## 测试验证

### 测试1：只选择地区

**步骤**：
1. 访问医生列表页
2. 在地区选择器中选择"北京市"
3. 观察医生列表

**预期结果**：
- ✅ 医院下拉框清空（如果之前有选择）
- ✅ 科室下拉框清空并禁用
- ✅ 医生列表只显示北京市的医生
- ✅ 控制台输出：`地区选择变化: ["110000"]`

### 测试2：地区 + 城市

**步骤**：
1. 在地区选择器中选择"北京市" → "北京市"
2. 观察医生列表

**预期结果**：
- ✅ 医生列表只显示北京市市辖区的医生

### 测试3：地区 + 医院

**步骤**：
1. 选择地区"广东省"
2. 选择医院"广东省人民医院"
3. 观察医生列表

**预期结果**：
- ✅ 科室下拉框加载并显示广东省人民医院的科室
- ✅ 医生列表只显示广东省人民医院的医生

### 测试4：切换地区

**步骤**：
1. 选择地区"北京市"
2. 选择医院"北京协和医院"
3. 选择科室"心血管内科"
4. 切换地区为"广东省"
5. 观察筛选框状态

**预期结果**：
- ✅ 医院选择清空
- ✅ 科室选择清空并禁用
- ✅ 医生列表更新为广东省的医生

### 测试5：重置筛选

**步骤**：
1. 选择地区、医院、科室、职称
2. 点击"重置"按钮
3. 观察页面状态

**预期结果**：
- ✅ 所有筛选条件清空
- ✅ 地区选择器清空
- ✅ 医院下拉框清空
- ✅ 科室下拉框清空并禁用
- ✅ 职称下拉框清空
- ✅ 医生列表显示所有医生

### 测试6：级联选择完整性

**步骤**：
1. 只选择地区 → 观察医生数量
2. 再选择医院 → 观察医生数量（应该减少）
3. 再选择科室 → 观察医生数量（应该进一步减少）
4. 再选择职称 → 观察医生数量（应该再次减少）

**预期结果**：
- ✅ 每增加一个筛选条件，医生数量递减
- ✅ 筛选逻辑正确

---

## 用户体验优化

### 1. 智能提示

**科室下拉框禁用时**：
- 显示为灰色状态
- 用户无法点击
- 清楚表明需要先选择医院

### 2. 自动清空

**地区变化时**：
- 自动清空医院选择
- 自动清空科室选择
- 用户无需手动重置

**医院变化时**：
- 自动清空科室选择
- 自动加载新医院的科室

### 3. 宽度统一

**下拉框宽度**：
- 地区：220px（较宽，显示完整地区名称）
- 医院：220px（较宽，显示完整医院名称）
- 科室：180px（中等）
- 职称：180px（中等）

---

## 文件修改清单

| 文件 | 修改内容 | 状态 |
|------|---------|------|
| `frontend/src/views/doctor/DoctorList.vue` | 模板：新增地区级联选择器 | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增状态：areaOptions, selectedArea | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增筛选参数：provinceCode, cityCode, areaCode | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增函数：transformAreaData, loadAreas, handleAreaChange | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 修改函数：resetFilters, onMounted | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 新增导入：getAreaTree | ✅ 已完成 |
| `frontend/src/views/doctor/DoctorList.vue` | 统一下拉框宽度 | ✅ 已完成 |

---

## 总结

✅ **功能增强**：
1. 添加地区级联选择（省/市/区）
2. 支持多级地区筛选（全省/全市/全区）
3. 实现智能清空机制（地区变化时清空下级选择）
4. 统一下拉框宽度，界面更整齐

✅ **用户体验**：
1. 筛选逻辑清晰：地区 → 医院 → 科室 → 职称
2. 自动级联清空，减少用户手动操作
3. 科室下拉框智能禁用，避免误操作
4. 与医院板块筛选逻辑一致

✅ **下一步**：
1. 刷新前端页面测试
2. 验证级联筛选功能
3. 测试智能清空机制

---

**相关文档**：
- [医生筛选功能修复总结](./DOCTOR_FILTER_FIX_SUMMARY.md)
- 后端DTO定义：`backend/src/main/java/com/chen/HospitalSelection/dto/DoctorFilterDTO.java`
