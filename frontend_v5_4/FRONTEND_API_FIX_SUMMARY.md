# 前端API对接修复总结

## 修复日期
2026-02-01

## 问题概述

前端代码与后端API响应格式存在多处不匹配，导致所有分页查询功能都会失败。

---

## 关键问题列表

### 问题 1: 分页响应格式不匹配（严重）

**后端 PageResult.java 返回格式:**
```java
{
  total: Long,
  pageNum: Integer,
  pageSize: Integer,
  pages: Integer,
  list: List<T>        // ← 使用 "list"
}
```

**前端原定义:**
```typescript
{
  records: T[],        // ← 错误！后端没有这个字段
  total: number,
  size: number,        // ← 错误！应该是 "pageSize"
  current: number,     // ← 错误！应该是 "pageNum"
  pages: number
}
```

**影响范围:**
- 所有分页查询都会返回 undefined
- 医院列表、医生列表、话题列表全部无法正常显示

---

### 问题 2: 用户登录DTO字段不匹配

**后端 UserLoginDTO.java:**
```java
private String phone;      // ← 使用 "phone"
private String password;
```

**前端原定义:**
```typescript
{
  username: string,        // ← 错误！应该是 "phone"
  password: string
}
```

**影响:**
- 登录功能无法正常工作

---

### 问题 3: 医院等级值格式不匹配

**UI显示值 vs 后端编码:**
- UI显示: "三甲", "三乙", "二甲" 等
- 后端期望: "grade3A", "grade3B", "grade2A" 等

**影响:**
- 医院等级筛选功能无法正常工作

---

## 修复内容

### 1. 修复核心类型定义

#### `frontend/src/types/api.d.ts`

**PageResult 接口修复:**
```typescript
// 修复前
export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 修复后
export interface PageResult<T = any> {
  total: number
  pageNum: number
  pageSize: number
  pages: number
  list: T[]
}
```

**UserLoginDTO 接口修复:**
```typescript
// 修复前
export interface UserLoginDTO {
  username: string
  password: string
}

// 修复后
export interface UserLoginDTO {
  phone: string
  password: string
}
```

**HospitalFilterDTO 接口扩展:**
```typescript
export interface HospitalFilterDTO {
  page: number
  pageSize: number
  areaCode?: string
  hospitalLevel?: string  // 后端编码: "grade3A" 等
  level?: string          // UI显示值: "三甲" 等（新增）
  // ... 其他字段
}
```

#### `frontend/src/utils/request.ts`

同步修复 PageResult 接口定义。

---

### 2. 修复所有访问 `.records` 的代码

**修复的文件列表:**
1. `frontend/src/views/home/index.vue`
2. `frontend/src/views/hospital/HospitalList.vue`
3. `frontend/src/views/hospital/HospitalDetail.vue`
4. `frontend/src/views/doctor/DoctorList.vue`
5. `frontend/src/views/doctor/DoctorDetail.vue`
6. `frontend/src/views/community/TopicList.vue`
7. `frontend/src/views/user/MyTopics.vue`
8. `frontend/src/views/user/MedicalHistory.vue`
9. `frontend/src/views/user/MyCollection.vue`

**修复示例:**
```typescript
// 修复前
hospitalList.value = res.data.records

// 修复后
hospitalList.value = res.data.list
```

---

### 3. 修复医院等级值映射

#### `frontend/src/stores/modules/hospital.ts`

**添加等级映射表和转换逻辑:**
```typescript
const HOSPITAL_LEVEL_MAP: Record<string, string> = {
  '三甲': 'grade3A',
  '三乙': 'grade3B',
  '二甲': 'grade2A',
  '二乙': 'grade2B',
  '一甲': 'grade1A',
  '一乙': 'grade1B'
}

// 新增 computed 属性，自动转换UI值为后端编码
const apiParams = computed<HospitalFilterDTO>(() => {
  const params: HospitalFilterDTO = {
    page: filterParams.value.page,
    pageSize: filterParams.value.pageSize,
    areaCode: filterParams.value.areaCode,
    name: filterParams.value.name
  }

  if (filterParams.value.level) {
    params.hospitalLevel = HOSPITAL_LEVEL_MAP[filterParams.value.level]
  }

  return params
})
```

#### `frontend/src/views/hospital/HospitalList.vue`

**使用 apiParams 替代 filterParams:**
```typescript
// 修复前
const res = await getHospitalList(hospitalStore.filterParams)

// 修复后
const res = await getHospitalList(hospitalStore.apiParams)
```

---

### 4. 修复其他参数问题

**DoctorList.vue 中的分页参数:**
```typescript
// 修复前
const res = await getHospitalList({ current: 1, size: 1000 })

// 修复后
const res = await getHospitalList({ page: 1, pageSize: 1000 })
```

---

## 验证结果

### TypeScript 编译检查
```bash
cd frontend && npx vue-tsc --noEmit
```
✅ 通过 - 无类型错误

### 修复的文件统计
- 类型定义文件: 2 个
- Store 文件: 1 个
- Vue 组件文件: 9 个
- 总计修复: 12 个文件

---

## 遗留问题（需要进一步验证）

### 1. 医院类型筛选
- 后端 HospitalFilterDTO 没有明确的 `type` 字段
- 前端使用了 "综合医院"、"专科医院" 等分类
- **建议**: 确认后端是否支持医院类型筛选，如果不支持，考虑移除该功能

### 2. 其他可能的字段不匹配
建议全面检查以下API的DTO字段：
- DoctorFilterDTO
- TopicPublishDTO
- CommentDTO
- MessageSendDTO

---

## 后续建议

### 1. 添加API响应验证
```typescript
// 在 request.ts 响应拦截器中添加
instance.interceptors.response.use(
  (response) => {
    // 验证响应格式
    if (response.data && typeof response.data === 'object') {
      if ('list' in response.data) {
        console.assert('total' in response.data, '分页响应缺少total字段')
      }
    }
    return response.data
  }
)
```

### 2. 添加单元测试
为核心API添加集成测试，确保前后端数据格式匹配。

### 3. 建立前后端类型同步机制
- 考虑使用 OpenAPI/Swagger 自动生成 TypeScript 类型
- 或定期手动同步后端 DTO 定义

---

## 总结

本次修复解决了前端与后端API对接的关键问题：
1. ✅ 分页响应格式不匹配 - 已修复
2. ✅ 用户登录DTO字段不匹配 - 已修复
3. ✅ 医院等级值映射问题 - 已修复
4. ✅ 所有 `.records` 访问点 - 已修复

**现在前端应该能够正常与后端API进行通信了。**

建议：
1. 启动后端服务 (端口 8088)
2. 启动前端开发服务器
3. 测试核心功能：
   - 用户登录/注册
   - 医院列表和筛选
   - 医生列表和详情
   - 社区话题浏览
   - 用户收藏功能
