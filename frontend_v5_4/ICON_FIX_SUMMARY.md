# Element Plus 图标导入错误修复

## 问题描述

启动开发服务器时出现错误：
```
The requested module '/node_modules/.vite/deps/@element-plus_icons-vue.js?v=77ba7002'
does not provide an export named 'Hospital'
```

## 原因分析

Element Plus Icons 中**不存在**名为 `Hospital` 的图标。这是一个常见的错误，因为不是所有直观的图标名称都存在于 Element Plus 图标库中。

## 解决方案

将所有使用的 `Hospital` 图标替换为有效的 `OfficeBuilding` 图标（建筑图标，语义相似）。

### 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `src/layouts/AppHeader.vue` | `<Hospital />` → `<OfficeBuilding />` |
| `src/views/home/index.vue` | `icon: Hospital` → `icon: OfficeBuilding` |
| `src/views/auth/Login.vue` | `<Hospital />` → `<OfficeBuilding />` |
| `src/views/auth/Register.vue` | `<Hospital />` → `<OfficeBuilding />` |

### 具体修改

#### 1. AppHeader.vue
```vue
<!-- 修改前 -->
<el-icon :size="32"><Hospital /></el-icon>
import { Hospital, Message, User, Star, Document, Notebook, SwitchButton } from '@element-plus/icons-vue'

<!-- 修改后 -->
<el-icon :size="32"><OfficeBuilding /></el-icon>
import { OfficeBuilding, Message, User, Star, Document, Notebook, SwitchButton } from '@element-plus/icons-vue'
```

#### 2. home/index.vue
```typescript
// 修改前
import { Hospital, User, ChatDotRound, Star } from '@element-plus/icons-vue'
icon: Hospital,

// 修改后
import { OfficeBuilding, User, ChatDotRound, Star } from '@element-plus/icons-vue'
icon: OfficeBuilding,
```

#### 3. auth/Login.vue
```vue
<!-- 修改前 -->
<el-icon :size="48" color="#409eff"><Hospital /></el-icon>
import { Hospital } from '@element-plus/icons-vue'

<!-- 修改后 -->
<el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
import { OfficeBuilding } from '@element-plus/icons-vue'
```

#### 4. auth/Register.vue
```vue
<!-- 修改前 -->
<el-icon :size="48" color="#409eff"><Hospital /></el-icon>
import { Hospital } from '@element-plus/icons-vue'

<!-- 修改后 -->
<el-icon :size="48" color="#409eff"><OfficeBuilding /></el-icon>
import { OfficeBuilding } from '@element-plus/icons-vue'
```

## Element Plus 图标参考

### 常用的建筑/位置相关图标

- ✅ `OfficeBuilding` - 办公楼/建筑
- ✅ `House` - 房屋
- ✅ `School` - 学校
- ✅ `Shop` - 商店
- ✅ `Location` - 位置
- ✅ `LocationFilled` - 位置（实心）

### 如何查找有效图标

1. 访问 [Element Plus Icons 官方文档](https://element-plus.org/zh-CN/component/icon.html)
2. 使用搜索功能查找所需图标
3. 确认图标的确切名称（区分大小写）

### 其他可用的医疗相关图标

- `User` - 用户/人
- `Avatar` - 头像
- `Check` - 检查/确认
- `FirstAidKit` - 急救包（如果存在）
- `Medicine` - 药品（如果存在）

## 验证

重新运行开发服务器，错误应该消失：

```bash
npm run dev
```

应该能够正常启动，访问 http://localhost:3000

## 预防措施

为了避免类似的图标导入错误：

1. **使用官方文档**: 所有图标名称都应该从 [Element Plus Icons](https://element-plus.org/zh-CN/component/icon.html) 确认
2. **IDE 支持**: 使用 VS Code 并安装 Element Plus 相关插件可以获得图标名称补全
3. **类型检查**: TypeScript 可以帮助发现不存在的图标导入

## 其他可能的图标问题

如果遇到其他图标导入错误，检查：
- 图标名称的拼写（区分大小写）
- 图标是否在 Element Plus Icons 库中存在
- import 语句是否正确

---

修复完成后，应用应该能够正常启动并运行！🎉
