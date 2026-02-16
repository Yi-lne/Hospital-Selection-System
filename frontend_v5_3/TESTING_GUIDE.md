# 前端测试执行与问题修复指南

## 第一步：安装测试依赖

进入前端目录并安装测试相关依赖：

```bash
cd frontend
npm install
```

这将自动安装以下测试依赖：
- vitest - 测试运行器
- @vitest/ui - 测试UI界面
- @vue/test-utils - Vue组件测试工具
- jsdom - DOM环境模拟
- @vitest/coverage-v8 - 代码覆盖率工具

## 第二步：运行测试

### 1. 运行所有测试（开发模式）

```bash
npm run test
```

这将启动交互式测试模式，可以：
- 实时查看测试结果
- 代码改动时自动重新运行
- 输入 `q` 退出

### 2. 运行所有测试（一次性）

```bash
npm run test:run
```

一次性运行所有测试并退出。

### 3. 生成测试覆盖率报告

```bash
npm run test:coverage
```

测试完成后会在 `coverage/` 目录生成HTML报告：

```bash
# 在Windows上打开报告
start coverage/index.html

# 在Mac上打开报告
open coverage/index.html

# 在Linux上打开报告
xdg-open coverage/index.html
```

## 第三步：查看测试结果

### 成功示例

```
✓ tests/api/user.test.ts (12)
✓ tests/api/hospital.test.ts (15)
✓ tests/api/community.test.ts (18)

Test Files  3 passed (3)
     Tests  45 passed (45)
  Start at  14:23:15
  Duration  2.34s
```

### 失败示例

```
✗ tests/api/user.test.ts (1)
   ✓ 用户注册 (2)
   ✗ 登录失败应该返回错误信息 (1)
     Error: 用户名或密码错误
```

## 第四步：修复测试失败的问题

### 问题类型1：API接口不匹配

**症状**：测试失败，提示API路径或参数不正确

**解决方案**：
1. 查看后端Controller确认正确的API路径
2. 检查`src/api/`目录下的API函数是否正确
3. 更新API函数或测试代码

**示例**：
```typescript
// 如果后端接口是 GET /api/hospital/list
// 但前端调用的是 POST /api/hospitals
// 需要修改前端API函数

// 错误的写法
export function getHospitalList(params: PageQueryDTO) {
  return request.post('/hospitals', params)
}

// 正确的写法
export function getHospitalList(params: PageQueryDTO) {
  return request.get('/hospital/list', { params })
}
```

### 问题类型2：数据格式不匹配

**症状**：测试失败，提示返回数据格式不符合预期

**解决方案**：
1. 检查后端返回的实际数据格式
2. 更新TypeScript类型定义
3. 更新测试代码中的mock数据

**示例**：
```typescript
// 如果后端返回的是 hospital_name
// 但前端期望的是 hospitalName
// 需要统一命名风格

// 错误的类型定义
interface Hospital {
  hospital_name: string
  hospital_level: string
}

// 正确的类型定义（驼峰命名）
interface Hospital {
  hospitalName: string
  hospitalLevel: string
}
```

### 问题类型3：缺少必要的字段

**症状**：测试失败，提示某个字段不存在或为undefined

**解决方案**：
1. 检查后端Entity和VO类是否包含该字段
2. 检查数据库表是否有该字段
3. 必要时需要询问是否应该修改后端

**示例**：
```typescript
// 如果前端期望医院有 'provinceName' 字段
// 但后端只返回了 'provinceCode'
// 需要在前端通过 area_code 查询 province_name

// 或者请求后端添加 provinceName 字段到 HospitalVO
```

### 问题类型4：认证失败

**症状**：测试失败，提示401未登录

**解决方案**：
1. 检查测试是否正确mock了Token
2. 确保测试环境的认证逻辑正确
3. 使用固定的测试账号

**示例**：
```typescript
// 在测试中mock token
const mockToken = 'test-token'
vi.mocked(localStorage.getItem).mockReturnValue(mockToken)
```

## 第五步：调试技巧

### 1. 只运行特定的测试文件

```bash
npm run test user.test.ts
```

### 2. 只运行特定的测试用例

```typescript
describe.only('用户登录', () => {
  it('应该成功登录', () => { })
  // 只运行这个describe下的测试
})
```

### 3. 在测试中添加console.log

```typescript
it('应该成功登录', async () => {
  const result = await userApi.login(loginData)
  console.log('返回结果:', result)
  expect(result.code).toBe(200)
})
```

### 4. 使用test.only调试单个测试

```typescript
it.only('应该成功登录', async () => {
  // 只运行这一个测试
})
```

## 第六步：修复实际的前端代码

### 场景1：修复API调用

如果测试发现API调用有问题：

1. **定位问题**：查看是哪个API函数有问题
2. **检查后端**：确认后端接口路径和参数
3. **修改前端**：更新 `src/api/` 目录下的API函数

**示例**：
```typescript
// src/api/user.ts
export async function getUserInfo(): Promise<Result<UserVO>> {
  return request.get('/user/info')  // 确保路径正确
}
```

### 场景2：修复类型定义

如果测试发现类型不匹配：

1. **定位问题**：查看是哪个类型定义有问题
2. **检查后端**：确认后端VO类的字段定义
3. **修改前端**：更新 `src/types/` 目录下的类型定义

**示例**：
```typescript
// src/types/user.d.ts
export interface UserVO {
  id: number
  phone: string
  nickname?: string
  avatar?: string
  gender?: number
  // 确保字段与后端一致
}
```

### 场景3：修复组件逻辑

如果测试发现组件逻辑有问题：

1. **定位问题**：查看是哪个组件的哪个方法有问题
2. **检查逻辑**：确认业务逻辑是否正确
3. **修改前端**：更新组件代码

**示例**：
```typescript
// src/components/user/LoginForm.vue
const handleLogin = async () => {
  // 验证表单
  if (!validateForm()) return

  // 调用API
  const result = await userApi.login(form)
  if (result.code === 200) {
    // 保存token
    userStore.login(result.data.token)
    // 跳转首页
    router.push('/')
  } else {
    ElMessage.error(result.message)
  }
}
```

## 需要询问用户意见的情况

以下情况**必须先询问用户**，不能擅自修改后端：

### 1. 数据实体字段不匹配

**症状**：前端期望的字段与后端Entity/VO不一致

**示例**：
- 前端期望：`provinceName`
- 后端返回：`provinceCode`

**询问内容**：
```
前端测试发现：医院列表需要省份名称字段，但后端只返回了省份编码。
是否需要在HospitalVO中添加provinceName字段？
或者在前端通过provinceCode查询provinceName？
```

### 2. 业务逻辑不一致

**症状**：前后端对同一功能的实现逻辑不一致

**示例**：
- 前端期望：收藏时自动记录到查询历史
- 后端实现：收藏和查询历史是独立的

**询问内容**：
```
前端测试发现：收藏功能是否需要自动记录到查询历史？
目前后端是独立实现的，请确认业务逻辑。
```

### 3. 缺少必要的API接口

**症状**：前端需要某个接口，但后端没有提供

**示例**：
- 前端需要：批量删除医院收藏
- 后端只有：单个删除收藏

**询问内容**：
```
前端需要批量删除收藏的接口，目前后端只有单个删除。
是否需要添加批量删除接口：DELETE /api/collection/batch？
```

## 测试执行流程总结

```
┌─────────────────────────────────────────────────────┐
│ 1. 安装依赖                                         │
│    npm install                                      │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 2. 运行测试                                         │
│    npm run test:run                                 │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 3. 查看结果                                         │
│    ✓ 全部通过 → 继续下一步                          │
│    ✗ 有失败 → 进入修复流程                          │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 4. 修复失败用例                                     │
│    ├─ API不匹配 → 检查并修复API函数                │
│    ├─ 类型不匹配 → 检查并修复类型定义              │
│    ├─ 缺少字段 → 检查后端，询问用户意见            │
│    └─ 逻辑错误 → 检查并修复业务逻辑                │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 5. 重新运行测试                                     │
│    npm run test:run                                 │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 6. 生成覆盖率报告                                   │
│    npm run test:coverage                            │
└─────────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────────┐
│ 7. 完成测试                                         │
│    覆盖率 ≥ 80% → 测试完成                          │
│    覆盖率 < 80% → 补充更多测试用例                  │
└─────────────────────────────────────────────────────┘
```

## 当前测试覆盖情况

### 已完成的测试

- ✅ 测试配置文件（vitest.config.ts）
- ✅ 测试环境设置（tests/setup.ts）
- ✅ 测试工具函数（tests/utils/test-utils.ts）
- ✅ 用户API测试（tests/api/user.test.ts）
- ✅ 医院API测试（tests/api/hospital.test.ts）
- ✅ 社区API测试（tests/api/community.test.ts）
- ✅ 登录组件测试（tests/components/LoginForm.test.ts）
- ✅ 测试文档（tests/README.md）

### 待完成的测试

- 📝 医生API测试（doctor.test.ts）
- 📝 私信API测试（message.test.ts）
- 📝 收藏API测试（collection.test.ts）
- 📝 病史API测试（medical-history.test.ts）
- 📝 查询历史API测试（query-history.test.ts）
- 📝 地区API测试（area.test.ts）
- 📝 疾病API测试（disease.test.ts）
- 📝 科室API测试（department.test.ts）
- 📝 更多组件测试

## 注意事项

1. **不要随意修改后端**：除非数据实体明显错误，否则只能查看
2. **先询问用户**：发现需要修改后端时，说明原因并询问意见
3. **保持沟通**：及时向用户报告测试进度和发现的问题
4. **记录问题**：将发现的问题记录下来，便于后续修复

## 下一步

执行测试并修复发现的问题：

```bash
cd frontend
npm install
npm run test:run
```

根据测试结果，按照上述流程修复发现的问题。
