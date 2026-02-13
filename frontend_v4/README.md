# 医院选择系统前端

基于 Vue 3 + TypeScript + Element Plus 构建的医院选择系统前端应用。

## 技术栈

- **框架**: Vue 3.4+ (Composition API)
- **构建工具**: Vite 5.x
- **UI框架**: Element Plus 2.5+
- **状态管理**: Pinia 2.2+
- **路由**: Vue Router 4.3+
- **HTTP客户端**: Axios 1.7+
- **语言**: JavaScript (ES6+)
- **CSS预处理**: SCSS

## 项目结构

```
frontend/
├── public/                     # 静态资源
├── src/
│   ├── api/                    # API接口封装
│   ├── assets/                 # 资源文件
│   │   └── styles/             # 全局样式
│   ├── components/             # 公共组件
│   │   ├── common/             # 通用组件
│   │   ├── hospital/           # 医院相关组件
│   │   ├── doctor/             # 医生相关组件
│   │   ├── community/          # 社区相关组件
│   │   └── user/               # 用户相关组件
│   ├── composables/            # 组合式函数
│   ├── layouts/                # 布局组件
│   ├── router/                 # 路由配置
│   ├── stores/                 # Pinia状态管理
│   ├── types/                  # TypeScript类型定义
│   ├── utils/                  # 工具函数
│   ├── views/                  # 页面组件
│   │   ├── auth/               # 认证页面
│   │   ├── home/               # 首页
│   │   ├── hospital/           # 医院页面
│   │   ├── doctor/             # 医生页面
│   │   ├── community/          # 社区页面
│   │   ├── user/               # 用户中心
│   │   ├── message/            # 消息页面
│   │   └── error/              # 错误页面
│   ├── App.vue
│   └── main.ts
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 功能模块

### 1. 认证模块
- 用户登录
- 用户注册
- 找回密码
- 个人资料管理

### 2. 医院模块
- 医院列表（支持分页、筛选）
- 医院详情
- 医院收藏

### 3. 医生模块
- 医生列表（支持分页、筛选）
- 医生详情
- 医生收藏

### 4. 社区模块
- 话题列表
- 话题详情
- 发布话题
- 评论功能
- 点赞功能

### 5. 用户中心
- 个人资料
- 我的收藏
- 我的话题
- 病史管理

### 6. 消息模块
- 会话列表
- 实时聊天

## 开发指南

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 环境配置

### 开发环境
- API地址: http://localhost:8088/api
- 端口: 3000

### 生产环境
- API地址: https://api.yourdomain.com/api

## API接口

所有API接口定义在 `src/api/` 目录下，按功能模块分类：

- `user.ts` - 用户相关接口
- `hospital.ts` - 医院相关接口
- `doctor.ts` - 医生相关接口
- `community.ts` - 社区相关接口
- `message.ts` - 消息相关接口
- `collection.ts` - 收藏相关接口
- `area.ts` - 地区相关接口
- `disease.ts` - 疾病相关接口
- `department.ts` - 科室相关接口
- `medical-history.ts` - 病史相关接口
- `query-history.ts` - 查询历史接口

## 状态管理

Pinia Store模块：

- `user` - 用户状态
- `app` - 应用全局状态
- `hospital` - 医院筛选状态
- `community` - 社区状态
- `message` - 消息状态

## 路由配置

- 公开路由：首页、登录、注册、医院/医生列表、社区浏览
- 需要登录：个人中心、收藏、发帖、私信

## 样式说明

- 使用SCSS预处理器
- 全局样式变量定义在 `src/assets/styles/variables.scss`
- Element Plus样式覆盖在 `src/assets/styles/element-override.scss`

## 类型定义

TypeScript类型定义在 `src/types/` 目录下：

- `api.d.ts` - API通用类型
- `user.d.ts` - 用户类型
- `hospital.d.ts` - 医院类型
- `doctor.d.ts` - 医生类型
- `community.d.ts` - 社区类型
- `message.d.ts` - 消息类型
- `other.d.ts` - 其他业务类型

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## License

MIT
