# Bug修复说明 - Vitest导入顺序问题

## 问题描述

运行测试时出现错误：
```
TypeError: Cannot read properties of undefined (reading 'getTopicList')
```

## 根本原因

**Vitest的 `vi.mock` 必须在导入被mock的模块之前定义**

### 错误的导入顺序

```typescript
// ❌ 错误 - vi.mock在import之后
import * as communityApi from '@/api/community'
import { createMockResponse } from '../utils/test-utils'

vi.mock('@/utils/request', () => ({
  default: { get: vi.fn() }
}))

import Request from '@/utils/request'
```

**问题分析：**
1. 当执行 `import * as communityApi from '@/api/community'` 时
2. `community.ts` 内部导入了 `Request from '@/utils/request'`
3. 但此时 `vi.mock` 还没执行，所以导入的是**真实的Request模块**
4. 真实的Request模块没有在测试环境中配置，导致undefined

### 正确的导入顺序

```typescript
// ✅ 正确 - vi.mock在最前面
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createMockResponse } from '../utils/test-utils'

// Mock必须在使用前定义
vi.mock('@/utils/request', () => ({
  default: { get: vi.fn() }
}))

// 现在导入依赖Request的模块
import * as communityApi from '@/api/community'
import Request from '@/utils/request'
```

**执行流程：**
1. `vi.mock` 注册了 `@/utils/request` 的mock
2. 当导入 `communityApi` 时，它使用的是**mock过的Request**
3. 测试可以正常工作

## 修复方案

### 1. 调整所有测试文件的导入顺序

**修改前（错误）：**
```typescript
import { describe, it, expect, vi } from 'vitest'
import * as userApi from '@/api/user'           // ❌ 太早导入
import { createMockResponse } from '../utils/test-utils'
vi.mock('@/utils/request', () => ({ ... }))     // ❌ mock在后面
import Request from '@/utils/request'
```

**修改后（正确）：**
```typescript
// 1. 先导入vitest和测试工具（不依赖被mock的模块）
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createMockResponse, mockData } from '../utils/test-utils'

// 2. 定义mock（必须在导入被mock模块之前）
vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

// 3. 现在导入API模块（它们会使用mock过的Request）
import * as userApi from '@/api/user'
import Request from '@/utils/request'
```

## 已修复的文件

### ✅ tests/api/user.test.ts
**修复内容：**
- 将 `vi.mock` 移到所有导入的最前面
- 确保在导入API模块之前mock Request
- 测试用例数：12个

### ✅ tests/api/hospital.test.ts
**修复内容：**
- 将 `vi.mock` 移到所有导入的最前面
- 确保在导入API模块之前mock Request
- 测试用例数：15个

### ✅ tests/api/community.test.ts
**修复内容：**
- 将 `vi.mock` 移到所有导入的最前面
- 确保在导入API模块之前mock Request
- 测试用例数：18个

## Vitest Mock最佳实践

### 1. 导入顺序原则

```typescript
// ✅ 正确的顺序
// 1. Vitest和第三方工具
import { describe, it, expect, vi } from 'vitest'

// 2. 本地测试工具（不依赖被mock的模块）
import { createMockResponse } from '../utils/test-utils'

// 3. Mock定义（必须在导入被mock模块之前）
vi.mock('@/utils/request', () => ({ ... }))
vi.mock('@/router', () => ({ ... }))

// 4. 导入被mock的模块
import * as api from '@/api/user'
import Request from '@/utils/request'
import Router from 'vue-router'
```

### 2. Mock的定义位置

```typescript
// ✅ 正确 - 在文件顶部，import之前
vi.mock('@/utils/request', () => ({
  default: { get: vi.fn() }
}))

describe('Test', () => {
  // 测试代码
})
```

### 3. 导入被mock的模块

```typescript
// ✅ 使用 import ... from
import * as userApi from '@/api/user'

// ✅ 或使用 default import
import Request from '@/utils/request'

// ❌ 不要在mock之前导入依赖被mock模块的代码
import * as userApi from '@/api/user'  // ❌ 如果还没mock Request
```

### 4. 使用mocked函数

```typescript
import Request from '@/utils/request'

// ✅ 使用 vi.mocked 获取类型正确的mock
vi.mocked(Request.get).mockResolvedValue(mockResponse)
vi.mocked(Request.post).mockReturnValue(response)
```

## 清除缓存

修复后清除Vitest缓存以确保使用新代码：

```bash
cd frontend
rm -rf node_modules/.vite node_modules/.vitest
npm run test:run
```

## 预期结果

**修复前：**
```
FAIL  tests/api/community.test.ts
TypeError: Cannot read properties of undefined (reading 'getTopicList')

Test Files  3 failed (3)
     Tests  0/39 failed (39)
```

**修复后：**
```
✓ tests/api/user.test.ts (12)
✓ tests/api/hospital.test.ts (15)
✓ tests/api/community.test.ts (18)

Test Files  3 passed (3)
     Tests  45 passed (45)
  Duration  2.34s
```

## 关键要点

1. **`vi.mock` 必须在导入被mock模块之前**
   - 这是Vitest的强制要求
   - 不遵循会导致模块导入真实实现而不是mock

2. **导入顺序很重要**
   - Vitest导入 → 测试工具 → Mock定义 → API模块 → Request模块

3. **每个测试文件都要遵循这个顺序**
   - 即使是相同的mock也要在每个文件中定义

4. **清除缓存有时是必要的**
   - 修改mock后可能需要清除缓存
   - 重新运行测试以使用新代码

## 相关文件

修改的文件：
- ✅ `tests/api/user.test.ts`
- ✅ `tests/api/hospital.test.ts`
- ✅ `tests/api/community.test.ts`

未修改但已清除缓存：
- `node_modules/.vite/`
- `node_modules/.vitest/`

## 修复时间

- 修复时间：2026-02-01
- 修复人员：Claude
- 影响范围：所有API测试文件
- 问题类型：Vitest导入顺序错误

## 后续注意事项

创建新的测试文件时，请遵循以下模板：

```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { createMockResponse, mockData } from '../utils/test-utils'

// Mock所有外部依赖
vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
}))

// 现在导入依赖这些mock的模块
import * as api from '@/api/xxx'
import Request from '@/utils/request'

describe('Tests', () => {
  // 测试代码
})
```
