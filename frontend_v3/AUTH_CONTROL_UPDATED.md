# 登录访问控制 - 详细实现说明

## 修改时间
2026-02-05

## 功能说明

### 未登录用户权限

✅ **可以访问的页面**：
- 首页 (`/home`)
- 医院列表 (`/hospital`)
- 医生列表 (`/doctor`)
- 科室医生列表 (`/doctor/department/:deptId`)
- 社区交流 (`/community`)
- 话题列表 (`/community/topics`)
- 话题详情 (`/community/topic/:id`)
- 板块分类 (`/community/boards`)
- 登录页 (`/login`)
- 注册页 (`/register`)

❌ **无法访问/执行的操作**：
- 查看医院详情页 (`/hospital/:id`) - 需要登录
- 查看医生详情页 (`/doctor/:id`) - 需要登录
- 收藏医院/医生 - 需要登录
- 发布话题 - 需要登录
- 评论话题 - 需要登录
- 点赞话题/评论 - 需要登录
- 个人中心相关页面 - 需要登录
- 消息功能 - 需要登录

### 登录用户权限

✅ **所有功能都可以使用**：
- 所有未登录用户可访问的页面
- 医院详情页
- 医生详情页
- 收藏功能
- 发布话题
- 评论功能
- 点赞功能
- 个人中心
- 消息功能

## 实现方案

### 1. 路由守卫配置 ✅

**文件**: `frontend/src/router/routes.js`

#### 需要登录的页面路由（添加 `requiresAuth: true`）：

```javascript
// 医院详情 - 需要登录
{
  path: '/hospital/:id',
  name: 'HospitalDetail',
  meta: { title: '医院详情', requiresAuth: true }
}

// 医生详情 - 需要登录
{
  path: '/doctor/:id',
  name: 'DoctorDetail',
  meta: { title: '医生详情', requiresAuth: true }
}

// 发布话题 - 需要登录
{
  path: '/community/publish',
  name: 'PublishTopic',
  meta: { title: '发布话题', requiresAuth: true }
}

// 个人中心 - 需要登录
{
  path: '/user',
  name: 'UserCenter',
  redirect: '/user/center',
  meta: { requiresAuth: true }
}

// 所有用户相关页面 - 需要登录
{
  path: '/user/center',
  name: 'UserCenter',
  meta: { title: '个人中心', requiresAuth: true }
}
// ... 其他用户页面
```

#### 公开访问的路由（无 `requiresAuth`）：

```javascript
// 首页 - 公开
{
  path: '/home',
  name: 'Home',
  meta: { title: '首页' }
}

// 医院列表 - 公开
{
  path: '/hospital',
  name: 'HospitalList',
  meta: { title: '医院列表' }
}

// 医生列表 - 公开
{
  path: '/doctor',
  name: 'DoctorList',
  meta: { title: '医生列表' }
}

// 社区 - 公开
{
  path: '/community',
  name: 'Community',
  meta: { title: '社区交流' }
}

// 话题详情 - 公开
{
  path: '/community/topic/:id',
  name: 'TopicDetail',
  meta: { title: '话题详情' }
}
```

### 2. 权限检查工具 ✅

**文件**: `frontend/src/utils/auth.js`

添加了 `checkAuth()` 方法，用于在组件内部检查登录状态：

```javascript
/**
 * 检查权限，如果未登录则提示并跳转到登录页
 * @param {Object} options - 配置选项
 * @param {String} options.action - 操作名称（用于提示信息）
 * @param {Object} options.router - Vue Router 实例
 * @param {String} options.redirect - 登录后重定向的路径（可选）
 * @returns {Boolean} - 是否已登录
 */
static checkAuth(options = {}) {
  const { action = '执行此操作', router, redirect } = options

  if (!this.isLoggedIn()) {
    ElMessage.warning(`请先登录后再${action}`)

    // 跳转到登录页
    if (router) {
      const currentPath = redirect || router.currentRoute.value.fullPath
      router.push({
        name: 'Login',
        query: { redirect: currentPath }
      })
    }

    return false
  }

  return true
}
```

### 3. 组件权限控制 ✅

#### 3.1 医院卡片组件 - 收藏功能

**文件**: `frontend/src/components/hospital/HospitalCard.vue`

```javascript
import { AuthManager } from '@/utils/auth'

const toggleCollect = () => {
  // 检查登录状态
  if (!AuthManager.checkAuth({
    action: '收藏医院',
    router
  })) {
    return
  }

  // TODO: 调用实际的收藏API
  isCollected.value = !isCollected.value
  ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏')
}
```

#### 3.2 社区首页 - 发布话题按钮

**文件**: `frontend/src/views/community/CommunityHome.vue`

```javascript
import { AuthManager } from '@/utils/auth'

const goToPublish = () => {
  // 检查登录状态
  if (!AuthManager.checkAuth({
    action: '发布话题',
    router
  })) {
    return
  }

  router.push('/community/publish')
}
```

#### 3.3 话题详情页 - 评论和点赞

**文件**: `frontend/src/views/community/TopicDetail.vue`

已经实现了登录检查：

```javascript
// 点赞话题
const toggleLike = () => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }
  handleLike()
}

// 点赞评论
const toggleLikeComment = async (comment: Comment) => {
  if (!userStore.isLogin) {
    ElMessage.warning('请先登录')
    return
  }
  // ... 执行点赞操作
}

// 评论输入框 - 只在登录时显示
<div v-if="userStore.isLogin" class="comment-input-area">
  <!-- 评论输入框 -->
</div>
```

### 4. 登录页提示 ✅

**文件**: `frontend/src/views/auth/Login.vue`

根据用户尝试访问的页面显示不同的提示信息：

```javascript
const requiresAuthMessage = computed(() => {
  const redirect = route.query.redirect as string
  if (!redirect) return ''

  // 根据重定向路径显示不同的提示消息
  if (redirect.includes('/hospital/') && redirect.match(/\/hospital\/\d+/)) {
    return '查看医院详情需要登录，请先登录您的账号'
  } else if (redirect.includes('/doctor/') && redirect.match(/\/doctor\/\d+/)) {
    return '查看医生详情需要登录，请先登录您的账号'
  } else if (redirect.includes('/community/publish')) {
    return '发布话题需要登录，请先登录您的账号'
  } else if (redirect.includes('/user')) {
    return '访问个人中心需要登录，请先登录您的账号'
  } else if (redirect.includes('/message')) {
    return '查看消息需要登录，请先登录您的账号'
  } else {
    return '请先登录您的账号'
  }
})
```

## 工作流程

### 场景1：未登录用户查看医院详情

```
未登录用户点击医院卡片
        ↓
路由跳转 /hospital/1
        ↓
路由守卫检测到 requiresAuth: true
        ↓
检测到未登录 → 重定向到 /login?redirect=/hospital/1
        ↓
登录页显示："查看医院详情需要登录，请先登录您的账号"
        ↓
用户登录成功
        ↓
自动跳转回 /hospital/1
```

### 场景2：未登录用户收藏医院

```
未登录用户点击收藏按钮
        ↓
HospitalCard 组件调用 AuthManager.checkAuth()
        ↓
检测到未登录 → 显示提示："请先登录后再收藏医院"
        ↓
自动跳转到 /login?redirect=/hospital
        ↓
登录页显示："查看医院详情需要登录..."
        ↓
用户登录成功
        ↓
自动跳转回 /hospital
```

### 场景3：未登录用户发布话题

```
未登录用户点击"发布话题"按钮
        ↓
CommunityHome 组件调用 AuthManager.checkAuth()
        ↓
检测到未登录 → 显示提示："请先登录后再发布话题"
        ↓
自动跳转到 /login?redirect=/community/publish
        ↓
登录页显示："发布话题需要登录，请先登录您的账号"
        ↓
用户登录成功
        ↓
自动跳转到发布话题页面
```

### 场景4：未登录用户浏览医院列表

```
未登录用户访问 /hospital
        ↓
路由守卫检查 requiresAuth (未设置)
        ↓
允许访问，正常加载医院列表
        ↓
用户可以浏览医院卡片
        ↓
点击"查看详情"按钮
        ↓
触发路由跳转到 /hospital/:id
        ↓
路由守卫拦截 → 跳转到登录页
```

## 测试验证

### 测试1：未登录浏览医院列表

1. **清除登录状态**
   ```javascript
   localStorage.clear()
   sessionStorage.clear()
   ```

2. **访问医院列表**
   - 访问: `http://localhost:5173/hospital`
   - 预期: ✅ 可以正常访问，显示医院列表
   - 预期: ✅ 可以看到医院卡片

3. **点击医院卡片**
   - 点击任意医院卡片
   - 预期: ❌ 跳转到登录页
   - 预期提示: "查看医院详情需要登录，请先登录您的账号"

### 测试2：未登录浏览医生列表

1. **访问医生列表**
   - 访问: `http://localhost:5173/doctor`
   - 预期: ✅ 可以正常访问，显示医生列表

2. **点击医生卡片**
   - 点击任意医生卡片
   - 预期: ❌ 跳转到登录页
   - 预期提示: "查看医生详情需要登录，请先登录您的账号"

### 测试3：未登录查看社区话题

1. **访问社区**
   - 访问: `http://localhost:5173/community`
   - 预期: ✅ 可以正常访问

2. **查看话题详情**
   - 点击任意话题
   - 预期: ✅ 可以查看话题详情
   - 预期: ✅ 可以看到评论列表
   - 预期: ❌ 无法评论（显示"登录后发表评论"）
   - 预期: ❌ 无法点赞（点击提示"请先登录"）

### 测试4：未登录收藏医院

1. **在医院列表页**
   - 点击任意医院卡片的"收藏"按钮
   - 预期: ❌ 显示提示："请先登录后再收藏医院"
   - 预期: 自动跳转到登录页

### 测试5：未登录发布话题

1. **在社区首页**
   - 点击"发布话题"按钮
   - 预期: ❌ 显示提示："请先登录后再发布话题"
   - 预期: 自动跳转到登录页
   - 预期提示: "发布话题需要登录，请先登录您的账号"

### 测试6：登录用户完整功能

1. **登录账号**
   - 使用: `13800138001` / `123456`

2. **测试所有功能**
   - ✅ 可以查看医院详情
   - ✅ 可以查看医生详情
   - ✅ 可以收藏医院
   - ✅ 可以发布话题
   - ✅ 可以评论
   - ✅ 可以点赞

## 测试账号

| 类型 | 手机号 | 密码 | 说明 |
|-----|--------|------|------|
| 管理员 | 13800000000 | admin123 | 系统管理员账号 |
| 测试用户1 | 13800138001 | 123456 | 普通用户 |
| 测试用户2 | 13800138002 | 123456 | 普通用户 |
| 测试用户3 | 13800138003 | 123456 | 普通用户 |

## 修改文件清单

| 文件 | 修改类型 | 说明 |
|------|---------|------|
| `frontend/src/router/routes.js` | ✅ 修改 | 只为详情页添加 requiresAuth |
| `frontend/src/utils/auth.js` | ✅ 修改 | 添加 checkAuth() 方法 |
| `frontend/src/components/hospital/HospitalCard.vue` | ✅ 修改 | 收藏功能添加登录检查 |
| `frontend/src/views/community/CommunityHome.vue` | ✅ 修改 | 发布话题按钮添加登录检查 |
| `frontend/src/views/auth/Login.vue` | ✅ 修改 | 更新提示信息逻辑 |
| `frontend/src/router/guards.js` | ✅ 无需修改 | 路由守卫已正确实现 |

## 用户体验优化

1. **友好的提示信息**
   - 根据用户尝试访问的页面显示不同的提示
   - 清楚说明为什么需要登录

2. **自动跳转回原页面**
   - 登录成功后自动跳转到用户原本想访问的页面
   - 避免用户手动寻找原页面

3. **渐进式访问控制**
   - 允许用户浏览列表，激发兴趣
   - 只在需要详细信息或互动时要求登录
   - 提高用户转化率

4. **清晰的视觉反馈**
   - 登录用户可以看到收藏按钮、评论输入框等
   - 未登录用户看到"登录后..."的提示
   - 清楚区分已登录和未登录状态

## 注意事项

1. **公开访问的好处**：
   - 用户可以先浏览内容，了解系统功能
   - 搜索引擎可以索引列表页面
   - 降低用户使用门槛，提高注册转化率

2. **需要登录的理由**：
   - 保护用户隐私（个人中心、消息）
   - 防止恶意操作（刷评论、刷点赞）
   - 记录用户行为（收藏、浏览历史）
   - 提供个性化服务

3. **后续可能需要添加登录检查的功能**：
   - 医生详情页的收藏按钮
   - 医院详情页的收藏按钮
   - 举报功能
   - 分享功能

## 总结

现在的访问控制策略是**渐进式的**：
- ✅ 未登录用户可以自由浏览，了解系统
- ✅ 只在需要详细信息或互动时才要求登录
- ✅ 登录后可以享受完整功能
- ✅ 提供友好的用户体验和清晰的反馈

这种策略既能保护敏感功能，又能降低用户使用门槛，是一个平衡的设计方案。
