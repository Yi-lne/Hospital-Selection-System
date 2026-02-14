# 前端测试说明文档

## 测试框架

- **测试运行器**: Vitest
- **组件测试**: @vue/test-utils
- **覆盖率工具**: vitest/coverage-v8

## 安装依赖

首先需要安装测试相关的依赖：

```bash
cd frontend
npm install -D vitest @vue/test-utils @vitest/ui @vitest/coverage-v8 jsdom
```

## 更新 package.json

在 `package.json` 中添加以下测试脚本和依赖：

```json
{
  "scripts": {
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "test:run": "vitest run"
  },
  "devDependencies": {
    "@vitest/ui": "^1.6.0",
    "@vue/test-utils": "^2.4.6",
    "jsdom": "^24.1.1",
    "vitest": "^1.6.0",
    "@vitest/coverage-v8": "^1.6.0"
  }
}
```

## 测试文件结构

```
frontend/tests/
├── setup.ts                    # 测试环境设置
├── utils/
│   └── test-utils.ts          # 测试工具函数
├── api/                        # API测试
│   ├── user.test.ts           # 用户模块测试
│   ├── hospital.test.ts       # 医院模块测试
│   ├── community.test.ts      # 社区模块测试
│   ├── doctor.test.ts         # 医生模块测试
│   ├── message.test.ts        # 私信模块测试
│   ├── collection.test.ts     # 收藏模块测试
│   └── ...                    # 其他API模块测试
├── components/                 # 组件测试
│   ├── user/
│   │   ├── LoginForm.test.ts  # 登录表单测试
│   │   └── UserAvatar.test.ts # 用户头像测试
│   ├── hospital/
│   │   ├── HospitalCard.test.ts
│   │   └── HospitalFilter.test.ts
│   ├── community/
│   │   ├── TopicCard.test.ts
│   │   └── CommentList.test.ts
│   └── ...
└── e2e/                        # 端到端测试（可选）
    └── ...
```

## 运行测试

### 1. 运行所有测试（交互模式）

```bash
npm run test
```

### 2. 运行所有测试（一次性）

```bash
npm run test:run
```

### 3. 运行特定测试文件

```bash
npm run test user.test.ts
```

### 4. 运行测试并查看UI界面

```bash
npm run test:ui
```

### 5. 生成测试覆盖率报告

```bash
npm run test:coverage
```

覆盖率报告将生成在 `coverage/index.html`

## 测试覆盖范围

### API模块测试（tests/api/）

覆盖所有API模块的功能测试：

- ✅ **user.test.ts** - 用户注册、登录、获取信息、更新信息、修改密码、登出
- ✅ **hospital.test.ts** - 医院列表、详情、筛选、搜索、科室、医生
- ✅ **community.test.ts** - 话题列表、详情、发布、评论、点赞、我的话题
- 📝 **doctor.test.ts** - 医生列表、详情、筛选
- 📝 **message.test.ts** - 会话列表、消息历史、发送私信、未读数
- 📝 **collection.test.ts** - 添加收藏、取消收藏、收藏列表
- 📝 **medical-history.test.ts** - 病史列表、添加、修改、删除
- 📝 **query-history.test.ts** - 查询历史列表、记录、删除
- 📝 **area.test.ts** - 省市区树、省份列表、城市列表、区县列表
- 📝 **disease.test.ts** - 疾病分类树、一级分类、二级分类
- 📝 **department.test.ts** - 医院科室列表、科室详情

### 组件测试（tests/components/）

覆盖核心组件的渲染和交互测试：

- ✅ **user/LoginForm.test.ts** - 登录表单
- 📝 **user/RegisterForm.test.ts** - 注册表单
- 📝 **user/UserAvatar.test.ts** - 用户头像
- 📝 **hospital/HospitalCard.test.ts** - 医院卡片
- 📝 **hospital/HospitalFilter.test.ts** - 医院筛选器
- 📝 **doctor/DoctorCard.test.ts** - 医生卡片
- 📝 **community/TopicCard.test.ts** - 话题卡片
- 📝 **community/CommentList.test.ts** - 评论列表
- 📝 **common/Pagination.test.ts** - 分页组件

### 功能测试场景

#### 1. 用户认证功能

- [ ] 用户注册（成功/失败）
- [ ] 用户登录（成功/失败）
- [ ] 自动登录（Token验证）
- [ ] 用户登出
- [ ] 修改个人信息
- [ ] 修改密码
- [ ] 找回密码

#### 2. 医院查询功能

- [ ] 浏览医院列表
- [ ] 按城市筛选医院
- [ ] 按等级筛选医院
- [ ] 按科室筛选医院
- [ ] 按医保定点筛选
- [ ] 搜索医院名称
- [ ] 查看医院详情
- [ ] 查看医院科室
- [ ] 查看医院医生
- [ ] 分页加载

#### 3. 医生查询功能

- [ ] 浏览医生列表
- [ ] 按医院筛选医生
- [ ] 按科室筛选医生
- [ ] 按职称筛选医生
- [ ] 查看医生详情
- [ ] 搜索医生姓名

#### 4. 社区交流功能

- [ ] 浏览话题列表
- [ ] 按板块筛选话题
- [ ] 搜索话题
- [ ] 查看话题详情
- [ ] 发布话题（登录后）
- [ ] 编辑话题（自己的）
- [ ] 删除话题（自己的）
- [ ] 发表评论（登录后）
- [ ] 回复评论（登录后）
- [ ] 点赞话题（登录后）
- [ ] 取消点赞
- [ ] 收藏话题（登录后）
- [ ] 查看我的话题
- [ ] 查看我的评论

#### 5. 用户中心功能

- [ ] 查看个人资料
- [ ] 编辑个人资料
- [ ] 上传头像
- [ ] 管理病史记录
- [ ] 查看收藏列表
- [ ] 取消收藏
- [ ] 查看查询历史
- [ ] 清空历史记录

#### 6. 私信功能

- [ ] 查看会话列表
- [ ] 查看消息历史
- [ ] 发送私信
- [ ] 标记已读
- [ ] 查看未读消息数

## 测试策略

### 单元测试

针对独立函数和工具类进行测试：

```typescript
// 示例：测试日期格式化函数
import { formatDate } from '@/utils/date'

describe('formatDate', () => {
  it('应该正确格式化日期', () => {
    const date = new Date('2025-01-28T10:30:00')
    expect(formatDate(date)).toBe('2025-01-28 10:30:00')
  })
})
```

### API测试

Mock后端API，测试前端与后端的交互：

```typescript
// 示例：测试登录API
import { userApi } from '@/api/user'
import { request } from '@/utils/request'

vi.mock('@/utils/request')

describe('User API', () => {
  it('应该成功登录', async () => {
    const mockResponse = {
      data: {
        code: 200,
        data: { token: 'mock-token', userInfo: {} },
      },
    }
    vi.mocked(request.post).mockResolvedValue(mockResponse)

    const result = await userApi.login({ phone: '13800000000', password: '123456' })
    expect(result.code).toBe(200)
  })
})
```

### 组件测试

测试组件的渲染、交互和状态：

```typescript
// 示例：测试登录表单组件
import { mount } from '@vue/test-utils'
import LoginForm from '@/components/user/LoginForm.vue'

describe('LoginForm', () => {
  it('应该正确渲染', () => {
    const wrapper = mount(LoginForm)
    expect(wrapper.find('form').exists()).toBe(true)
  })

  it('应该验证输入', async () => {
    const wrapper = mount(LoginForm)
    await wrapper.setData({ phone: '' })
    await wrapper.vm.handleLogin()
    expect(wrapper.vm.errors.phone).toBeDefined()
  })
})
```

### 集成测试

测试多个组件或API协同工作：

```typescript
// 示例：测试登录后跳转到首页
describe('Login Flow', () => {
  it('登录成功后应该跳转到首页', async () => {
    const wrapper = mount(LoginForm)
    await wrapper.vm.handleLogin()
    // 验证是否跳转到首页
  })
})
```

## 持续集成

在CI/CD流程中运行测试：

```yaml
# .github/workflows/test.yml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '20'
      - run: npm ci
      - run: npm run test:run
      - run: npm run test:coverage
```

## 测试覆盖率目标

- **整体覆盖率**: ≥ 80%
- **API模块**: ≥ 90%
- **核心组件**: ≥ 85%
- **工具函数**: ≥ 95%

## 注意事项

1. **Mock所有外部依赖**
   - API请求使用 vi.mock
   - Router使用 vi.mock
   - Store使用 vi.mock

2. **异步测试**
   - 使用 async/await
   - 等待DOM更新
   - 等待Promise完成

3. **测试隔离**
   - 每个测试独立运行
   - beforeEach中清理状态
   - 避免测试间相互影响

4. **测试数据**
   - 使用固定的测试数据
   - 不依赖数据库实际数据
   - 使用test-utils中的mockData

## 故障排除

### 问题1：测试超时

**解决方案**：增加超时时间
```typescript
test('慢速测试', async () => { }, { timeout: 10000 })
```

### 问题2：Mock不生效

**解决方案**：确保在import前mock
```typescript
vi.mock('@/api/user', () => ({ ... }))
import { userApi } from '@/api/user'
```

### 问题3：组件找不到

**解决方案**：正确配置stubs或global.components
```typescript
mount(Component, {
  global: {
    stubs: { 'el-button': true },
  },
})
```

## 下一步工作

- [ ] 完成所有API模块测试
- [ ] 完成核心组件测试
- [ ] 添加E2E测试（Playwright）
- [ ] 集成到CI/CD流程
- [ ] 达到80%测试覆盖率目标
