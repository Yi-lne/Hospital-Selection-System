# API 参数名称不匹配错误修复

## 问题描述

前端请求后端API时出现 400 Bad Request 错误：

```
GET http://localhost:8088/api/hospital/list?current=1&size=6 400 (Bad Request)
```

## 根本原因

**前端和后端的分页参数名称不一致**：

| 位置 | 页码参数 | 每页大小参数 |
|------|---------|------------|
| **前端（旧）** | `current` | `size` |
| **后端** | `page` | `pageSize` |

这导致后端无法正确接收参数，触发了 `@NotNull` 验证失败。

## 后端DTO定义

### PageQueryDTO（后端）
```java
public class PageQueryDTO {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page;

    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize;
}
```

### HospitalFilterDTO（后端）
```java
public class HospitalFilterDTO {
    private String diseaseCode;
    private String hospitalLevel;
    private String provinceCode;
    private String cityCode;    // 不是 areaId
    private String areaCode;
    private Integer isMedicalInsurance;
    private String keyDepartments;

    @NotNull
    private Integer page;
    @NotNull
    private Integer pageSize;
    private String sortBy;
}
```

## 修复方案

将前端的分页参数统一改为 `page` 和 `pageSize`，并调整其他相关参数。

---

## 修改的文件

### 1. 类型定义文件

#### `src/types/api.d.ts`
```typescript
// 修改前
export interface PageQueryDTO {
  current: number
  size: number
}

export interface HospitalFilterDTO {
  current: number
  size: number
  areaId?: number      // 错误：后端是 areaCode
  level?: string       // 错误：后端是 hospitalLevel
  type?: string
  name?: string
}

// 修改后
export interface PageQueryDTO {
  page: number
  pageSize: number
}

export interface HospitalFilterDTO {
  page: number
  pageSize: number
  areaCode?: string
  hospitalLevel?: string
  diseaseCode?: string
  provinceCode?: string
  cityCode?: string
  isMedicalInsurance?: number
  keyDepartments?: string
  sortBy?: string
}
```

#### `src/types/hospital.d.ts`
```typescript
// current → page, size → pageSize
```

#### `src/types/doctor.d.ts`
```typescript
// current → page, size → pageSize
```

### 2. API 接口文件

所有 `src/api/*.ts` 文件中的函数签名已更新：

```typescript
// community.ts
export function getTopicList(page = 1, pageSize = 10)
export function getMyTopics(page = 1, pageSize = 10)

// doctor.ts
export function getDoctorList(params: DoctorFilterDTO)
export function getHospitalDoctors(hospitalId, page = 1, pageSize = 10)
export function getDepartmentDoctors(departmentId, page = 1, pageSize = 10)
export function searchDoctors(keyword, page = 1, pageSize = 10)

// message.ts
export function getMessageHistory(userId, page = 1, pageSize = 50)

// collection.ts
export function getCollectionList(page = 1, pageSize = 10)

// medical-history.ts
export function getMedicalHistoryList(page = 1, pageSize = 10)

// query-history.ts
export function getQueryHistoryList(page = 1, pageSize = 10)
```

### 3. Store 状态管理

#### `src/stores/modules/hospital.ts`
```typescript
// 更新筛选参数
const filterParams = ref<HospitalFilterDTO>({
  page: 1,
  pageSize: 10,
  areaCode: undefined,
  hospitalLevel: undefined,
  // ...
})
```

### 4. Composable

#### `src/composables/usePagination.ts`
```typescript
// 修改前
export function usePagination(initialSize = 10) {
  const current = ref(1)
  const size = ref(initialSize)
  return { current, size, ... }
}

// 修改后
export function usePagination(initialPageSize = 10) {
  const page = ref(1)
  const pageSize = ref(initialPageSize)
  return { page, pageSize, ... }
}
```

### 5. Vue 组件

以下组件中的分页变量已更新：

- `src/views/home/index.vue`
  ```typescript
  const hospitalRes = await getHospitalList({ page: 1, pageSize: 6 })
  ```

- `src/views/community/TopicList.vue`
  ```vue
  <el-pagination v-model:current-page="page" v-model:page-size="pageSize" />
  ```

- `src/views/doctor/DoctorList.vue`
  ```vue
  <el-pagination v-model:current-page="filterParams.page" v-model:page-size="filterParams.pageSize" />
  ```

- `src/views/hospital/HospitalList.vue`
  ```vue
  <el-pagination v-model:current-page="filterParams.page" v-model:page-size="filterParams.pageSize" />
  ```

- `src/views/user/MyCollection.vue`
  ```vue
  <el-pagination v-model:current-page="page" v-model:page-size="pageSize" />
  ```

- `src/views/user/MyTopics.vue`
  ```vue
  <el-pagination v-model:current-page="page" v-model:page-size="pageSize" />
  ```

- `src/views/user/MedicalHistory.vue`
  ```vue
  <el-pagination v-model:current-page="page" v-model:page-size="pageSize" />
  ```

---

## Element Plus 分页组件说明

Element Plus 的 `el-pagination` 组件使用以下属性：

- `v-model:current-page` - 绑定当前页码
- `v-model:page-size` - 绑定每页大小
- `:total` - 总条目数
- `:page-sizes` - 每页大小选项

```vue
<el-pagination
  v-model:current-page="page"
  v-model:page-size="pageSize"
  :total="total"
  :page-sizes="[10, 20, 50]"
  layout="total, sizes, prev, pager, next"
  @current-change="loadData"
  @size-change="handleSizeChange"
/>
```

---

## 验证修复

修复后，API请求应该使用正确的参数：

```bash
# 修复前（400错误）
GET /api/hospital/list?current=1&size=6

# 修复后（正确）
GET /api/hospital/list?page=1&pageSize=6
```

重新启动开发服务器测试：

```bash
npm run dev
```

访问 http://localhost:3000，应该能够正常加载医院列表数据。

---

## 总结

此次修复涉及：

- ✅ **3个类型定义文件** - 更新DTO接口
- ✅ **6个API文件** - 更新函数签名
- ✅ **1个Store文件** - 更新状态管理
- ✅ **1个Composable** - 更新分页逻辑
- ✅ **8个Vue组件** - 更新分页变量绑定

**关键要点**：前后端的API参数名称必须保持一致，这是导致400错误的常见原因。

---

修复完成后，所有分页API请求应该正常工作！🎉
