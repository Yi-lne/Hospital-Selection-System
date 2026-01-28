# 基于SpringBoot的智能医院选择网站的设计与实现

------

## 一、项目概述

该系统通过集成全国各大医院的信息，如涵盖医院等级、医生专科信息、是否定点医保等信息，帮助用户多条件组合筛选出合适的医院，同时该系统提供社区功能，用户可在社区分享就医经验、医院推荐等。

------



## 二、项目架构

本项目采用**前后端分离**架构（SPA），遵循分层设计原则，确保系统可扩展性、可维护性和易用性，整体架构如下：

- 后端技术栈：SpringBoot + MyBatis + MySQL + JWT + RESTful API
- 前端技术栈：Vue.js + Vue Router + Pinia + Element UI + Axios（Vue单页应用，打包后部署到Nginx）
- 核心设计思想：模块化开发、组件化复用、数据结构化存储、多条件规则匹配筛选

### 1. 后端项目架构：SpringBoot

```
com.chen.HospitalSelection
├── HospitalSelectionApplication.java          // 应用启动类
├── config/                                // 配置类模块
│   ├── SpringSecurityConfig.java          // 安全配置（集成JWT认证授权）
│   ├── MyBatisConfig.java                 // MyBatis配置（映射、分页等）
│   ├── WebMvcConfig.java                  // 跨域、资源映射配置
│   └── SwaggerConfig.java                 // 接口文档配置（可选）
├── controller/                            // 控制层（API接口）
│   ├── UserController.java                // 用户相关接口（注册、登录、个人中心）
│   ├── HospitalController.java            // 医院信息接口（查询、筛选）
│   ├── DoctorController.java              // 医生信息接口（查询、详情）
│   └── CommunityController.java           // 社区功能接口（话题、评论、互动）
├── service/                               // 服务层（业务逻辑）
│   ├── UserService.java
│   ├── HospitalService.java
│   ├── DoctorService.java
│   ├── CommunityService.java
│   ├── FilterService.java                 // 多条件筛选核心服务
│   └── impl/                              // 服务实现类
│       ├── UserServiceImpl.java
│       ├── HospitalServiceImpl.java
│       ├── DoctorServiceImpl.java
│       ├── CommunityServiceImpl.java
│       └── FilterServiceImpl.java
├── mapper/                                // 数据访问层（MyBatis Mapper）
│   ├── UserMapper.java
│   ├── HospitalMapper.java
│   ├── DoctorMapper.java
│   ├── DiseaseMapper.java
│   ├── TopicMapper.java
│   └── CommentMapper.java
├── model/                                 // 数据模型（数据库实体）
│   ├── User.java                          // 用户表实体
│   ├── Hospital.java                      // 医院信息表实体
│   ├── Doctor.java                        // 医生表实体
│   ├── Disease.java                       // 疾病分类表实体
│   ├── Topic.java                         // 社区话题表实体
│   ├── Comment.java                       // 评论表实体
│   └── Collection.java                    // 收藏表实体
├── dto/                                   // 数据传输对象（前端入参）
│   ├── UserLoginDTO.java                  // 登录入参
│   ├── UserRegisterDTO.java               // 注册入参
│   ├── HospitalFilterDTO.java             // 医院筛选条件入参
│   ├── TopicPublishDTO.java               // 话题发布入参
│   └── CommentDTO.java                    // 评论提交入参
├── vo/                                    // 视图对象（后端出参）
│   ├── Result.java                        // 统一返回格式
│   ├── PageResult.java                    // 分页返回格式
│   ├── UserVO.java                        // 用户信息返回
│   ├── HospitalVO.java                    // 医院信息返回
│   ├── DoctorVO.java                      // 医生信息返回
│   └── TopicVO.java                       // 社区话题返回（含评论、互动数）
├── exception/                             // 异常处理
│   ├── GlobalExceptionHandler.java        // 全局异常拦截
│   ├── BusinessException.java             // 业务异常
│   └── AuthException.java                 // 认证授权异常
├── util/                                  // 工具类
│   ├── JwtUtil.java                       // JWT生成与验证
│   ├── PageUtil.java                      // 分页工具
│   └── ValidationUtil.java                // 数据校验工具
└── resources/
    ├── application.yml                    // 主配置文件
    ├── application-dev.yml                // 开发环境配置
    ├── mapper/                            // MyBatis XML映射文件
    │   ├── UserMapper.xml
    │   ├── HospitalMapper.xml
    │   └── ...
    └── static/                            // 静态资源（可选）
```

**后端所有接口统一返回格式：**

```
{
	"code": 200,
	"message": "success",
	"data": {...}
}
```

### 2. 前端项目架构：Vue.js （使用vite创建）

```
src/
├── main.js                                // 入口文件
├── App.vue                                 // 根组件
├── router/                                 // 路由配置（Vue Router）
│   └── index.js                            // 路由规则（公开路由+私有路由）
├── store/                                  // 状态管理（Pinia）
│   ├── index.js
│   ├── modules/
│       ├── userStore.js                    // 用户状态（登录、个人信息）
│       └── filterStore.js                  // 筛选条件缓存
├── views/                                  // 页面级组件
│   ├── Login.vue                           // 登录页
│   ├── Register.vue                        // 注册页
│   ├── Home.vue                            // 首页
│   ├── hospital/
│   │   ├── HospitalList.vue                // 医院列表+筛选页
│   │   └── HospitalDetail.vue              // 医院详情页
│   ├── doctor/
│   │   ├── DoctorList.vue                  // 医生列表页
│   │   └── DoctorDetail.vue                // 医生详情页
│   ├── community/
│   │   ├── CommunityHome.vue               // 社区首页（板块分类）
│   │   ├── TopicList.vue                   // 话题列表页
│   │   ├── TopicDetail.vue                 // 话题详情页（含评论）
│   │   └── TopicPublish.vue                // 话题发布页
│   └── user/
│       ├── UserCenter.vue                  // 个人中心首页
│       ├── UserInfo.vue                    // 个人信息编辑
│       ├── CollectionList.vue              // 我的收藏
│       └── MyTopic.vue                     // 我的话题/评论
├── components/                             // 公共组件
│   ├── layout/
│   │   ├── Navbar.vue                      // 顶部导航栏
│   │   ├── Sidebar.vue                     // 侧边栏（社区/筛选）
│   │   └── Footer.vue                      // 页脚
│   ├── hospital/
│   │   ├── FilterPanel.vue                 // 多条件筛选面板
│   │   ├── HospitalCard.vue                // 医院卡片组件
│   │   └── HospitalFilterResult.vue        // 筛选结果展示
│   ├── community/
│   │   ├── TopicCard.vue                   // 话题卡片组件
│   │   ├── CommentItem.vue                 // 评论项组件
│   │   └── InteractionBar.vue              // 点赞/收藏/评论工具栏
│   └── common/
│       ├── Pagination.vue                  // 分页组件
│       ├── SearchInput.vue                 // 搜索输入框
│       └── Loading.vue                     // 加载组件
├── api/                                    // 后端API请求封装
│   ├── user.js                             // 用户相关请求
│   ├── hospital.js                         // 医院相关请求
│   ├── doctor.js                           // 医生相关请求
│   ├── community.js                        // 社区相关请求
│   └── request.js                          // Axios封装（拦截器、统一配置）
├── utils/                                  // 工具类
│   ├── auth.js                             // 权限工具（Token存储/验证）
│   ├── format.js                           // 数据格式化工具
│   └── validation.js                       // 表单验证工具
├── assets/                                 // 静态资源
│   ├── css/                                // 全局样式
│   ├── img/                                // 图片资源
│   └── icons/                              // 图标资源
└── styles/                                 // 样式文件
    ├── index.css                           // 全局样式入口
    └── variables.css                       // 样式变量
```

------



## 三、功能模块设计：

### 1. 医院多条件筛选模块（核心功能）

#### 核心目标：帮助用户快速匹配符合需求的医院

#### 关键功能：

- 多维度筛选条件：支持疾病类型（按系统 / 具体疾病分类）、医院等级（三甲 / 二甲 / 其他）、地理位置（省市县区三级联动）、专科特色（如心内科、肿瘤科等）、医生资质（主任医师 / 副主任医师等）、医保定点（是 / 否）等条件组合筛选
- 筛选逻辑：基于规则匹配模型，将用户筛选条件与医院特征向量（等级、科室、设备、医生）进行精准匹配
- 结果排序：支持按匹配度、医院等级、用户评价、距离（可选）等维度排序，排序规则透明可切换
- 结果展示：提供列表视图（含核心信息概览）和详情视图（含医院完整信息），支持快速收藏医院
- 筛选记忆：缓存用户历史筛选条件，下次访问自动回显

### 2. 医院 / 医生信息查询模块

#### 核心目标：提供全面、结构化的医疗资源信息

#### 关键功能：

- 医院信息展示：医院简介、重点科室、医疗设备、专家团队、医院地址、联系电话、用户评价等
- 医生信息展示：医生职称、专业特长、学术背景、坐诊时间、所属医院、患者评价等
- 多样化查询：支持关键词搜索（医院名称 / 医生姓名 / 疾病名称）、条件筛选查询、列表浏览查询
- 信息结构化：按医院等级、地区、专科特色进行分类组织，便于用户快速定位

### 3. 病友社区交流模块

#### 核心目标：构建专业、安全的就医经验分享平台

#### 关键功能：

- 板块分层设计：

  - 一级板块：按人体系统 / 疾病大类划分（如心血管区、内分泌区、肿瘤区、儿科区）
  - 二级板块：按具体疾病划分（如高血压、冠心病、糖尿病等）
  - 特色板块：医院评价区、就医经验区、康复护理区

  

- 内容互动功能：话题发布、评论回复、点赞（有用投票）、收藏话题、关注用户 / 话题

- 内容审核机制：机器审核（关键词过滤 + 语义分析）+ 人工审核，拦截违规内容（虚假医疗建议、广告、攻击性言论）

- 用户信用体系：鼓励高质量经验分享，优质内容优先展示

### 4. 用户个人中心模块

#### 核心目标：实现用户信息管理与个性化操作

#### 关键功能：

- 身份认证：手机号注册、密码登录、JWT 令牌验证、密码找回
- 信息管理：个人基本资料编辑、病史信息维护
- 个性化操作：收藏医院 / 话题、查看收藏列表、管理查询历史、查看浏览记录
- 内容管理：查看个人发布的话题 / 评论、编辑 / 删除自有内容、查看互动通知（点赞 / 评论提醒）

### 5. 系统基础支撑模块

#### 核心目标：保障系统稳定、安全、易用

#### 关键功能：

- 权限控制：基于 JWT 的身份认证，未登录用户仅可浏览公开信息，登录后可使用社区互动、收藏等功能
- 性能优化：数据库索引优化（医院名称、疾病名称、科室分类等字段）、查询语句优化、前端懒加载
- 用户体验：响应式界面（适配不同设备）、直观的筛选交互、清晰的信息层级、加载状态提示
- 数据安全：用户隐私数据加密存储、医疗信息脱敏处理、防止 SQL 注入等安全漏洞

------



## 四、数据库设计

### 1. 数据库基本信息

- 名称：hospital_selection
- 密码：root



### 2. 数据库表设计

**（1）系统用户表（sys_user）:**

| 字段名      | 字段类型     | 是否非空 | 默认值   | 注释                                             |
| :---------- | :----------- | :------- | :------- | :----------------------------------------------- |
| id          | bigint       | 是       | 自增     | 主键 ID                                          |
| phone       | char(11)     | 是       | -        | 手机号（唯一，用于注册、登录）                   |
| password    | varchar(100) | 是       | -        | 密码（MD5/BCrypt 加密）                          |
| nickname    | varchar(50)  | 否       | -        | 用户昵称（初次注册随机生成，后续可修改）         |
| avatar      | varchar(255) | 否       | -        | 头像 URL（初次注册统一使用默认头像，后续可修改） |
| gender      | tinyint      | 否       | 0        | 性别（0 = 未知，1 = 男，2 = 女）                 |
| status      | tinyint      | 是       | 1        | 状态（1 = 正常，0 = 禁用）                       |
| is_deleted  | tinyint      | 是       | 0        | 逻辑删除（0 = 未删，1 = 已删）                   |
| create_time | datetime     | 是       | 当前时间 | 创建时间                                         |
| update_time | datetime     | 是       | 当前时间 | 更新时间                                         |



**（2）系统角色表（sys_role）：**

| 字段名      | 字段类型    | 是否非空 | 默认值   | 注释                             |
| :---------- | :---------- | :------- | :------- | :------------------------------- |
| id          | bigint      | 是       | 自增     | 主键 ID                          |
| role_name   | varchar(50) | 是       | -        | 角色名称（如：普通用户、管理员） |
| role_code   | varchar(50) | 是       | -        | 角色编码（如：user、admin）      |
| is_deleted  | tinyint     | 是       | 0        | 逻辑删除                         |
| create_time | datetime    | 是       | 当前时间 | 创建时间                         |



**（3）医院信息表（hospital_info）：**

| 字段名                | 字段类型     | 是否非空 | 默认值   | 注释                                           |
| :-------------------- | :----------- | :------- | :------- | :--------------------------------------------- |
| id                    | bigint       | 是       | 自增     | 主键 ID                                        |
| hospital_name         | varchar(100) | 是       | -        | 医院名称（唯一）                               |
| hospital_level        | varchar(20)  | 是       | -        | 医院等级（如：grade3A = 三甲，grade2A = 二甲） |
| province_code         | varchar(20)  | 是       | -        | 省份编码（关联 area_info.code）                |
| city_code             | varchar(20)  | 是       | -        | 城市编码（关联 area_info.code）                |
| area_code             | varchar(20)  | 是       | -        | 区县编码（关联 area_info.code）                |
| address               | varchar(255) | 是       | -        | 详细地址                                       |
| phone                 | varchar(50)  | 否       | -        | 联系电话                                       |
| specialty             | varchar(255) | 否       | -        | 特色科室（逗号分隔）                           |
| intro                 | text         | 否       | -        | 医院简介                                       |
| is_medical_insurancet | tinyint      | 是       | 0        | 是否医保定点（0 = 否，1 = 是）                 |
| is_deleted            | tinyint      | 是       | 0        | 逻辑删除                                       |
| create_time           | datetime     | 是       | 当前时间 | 创建时间                                       |
| update_time           | datetime     | 是       | 当前时间 | 更新时间                                       |



**（4）医院科室表（hospital_department）：**

| 字段名      | 字段类型    | 是否非空 | 默认值 | 注释                             |
| :---------- | :---------- | :------- | :----- | :------------------------------- |
| id          | bigint      | 是       | 自增   | 主键 ID                          |
| hospital_id | bigint      | 是       | -      | 医院 ID（关联 hospital_info.id） |
| dept_name   | varchar(50) | 是       | -      | 科室名称（如：心内科、肿瘤科）   |
| dept_intro  | text        | 否       | -      | 科室简介                         |
| is_deleted  | tinyint     | 是       | 0      | 逻辑删除                         |



**（5）医生信息表（doctor_info）：**

| 字段名           | 字段类型      | 是否非空 | 默认值   | 注释                                       |
| ---------------- | ------------- | -------- | -------- | ------------------------------------------ |
| id               | bigint        | 是       | 自增     | 主键 ID                                    |
| doctor_name      | varchar(50)   | 是       | -        | 医生姓名                                   |
| hospital_id      | bigint        | 是       | -        | 所属医院 ID                                |
| dept_id          | bigint        | 是       | -        | 所属科室 ID（关联 hospital_department.id） |
| title            | varchar(50)   | 是       | -        | 职称（如：主任医师、副主任医师）           |
| specialty        | varchar(255)  | 否       | -        | 专业特长                                   |
| consultation_fee | decimal(10,2) | 否       | -        | 挂号 / 咨询费                              |
| is_deleted       | tinyint       | 是       | 0        | 逻辑删除                                   |
| update_time      | datetime      | 是       | 当前时间 | 更新时间                                   |



**（6）疾病分类表（disease_type）：**

| 字段名       | 字段类型    | 是否非空 | 默认值 | 注释                                   |
| ------------ | ----------- | -------- | ------ | -------------------------------------- |
| id           | bigint      | 是       | 自增   | 主键 ID                                |
| parent_id    | bigint      | 是       | 0      | 父分类 ID（0 = 一级分类）              |
| disease_name | varchar(50) | 是       | -      | 疾病分类名称（如：心血管疾病、高血压） |
| disease_code | varchar(20) | 是       | -      | 疾病编码（唯一）                       |
| sort         | int         | 否       | 0      | 排序权重（值越大越靠前）               |
| is_deleted   | tinyint     | 是       | 0      | 逻辑删除                               |



**（7）省市区三级联动表（area_info）：**

| 字段名      | 字段类型    | 是否非空 | 默认值 | 注释                                |
| ----------- | ----------- | -------- | ------ | ----------------------------------- |
| id          | bigint      | 是       | 自增   | 主键 ID                             |
| code        | varchar(20) | 是       | -      | 地区编码（国标，如 110000 = 北京）  |
| name        | varchar(50) | 是       | -      | 地区名称（省 / 市 / 区）            |
| parent_code | varchar(20) | 是       | -      | 父级编码（如北京市的父级是 110000） |
| level       | tinyint     | 是       | -      | 层级（1 = 省，2 = 市，3 = 区 / 县） |



**（8）社区话题表（community_topic）：**

| 字段名        | 字段类型     | 是否非空 | 默认值   | 注释                                   |
| ------------- | ------------ | -------- | -------- | -------------------------------------- |
| id            | bigint       | 是       | 自增     | 主键 ID                                |
| user_id       | bigint       | 是       | -        | 发布用户 ID（关联 sys_user.id）        |
| disease_code  | varchar(20)  | 否       | -        | 关联疾病编码（可选，用于分类）         |
| title         | varchar(200) | 是       | -        | 话题标题                               |
| content       | text         | 是       | -        | 话题内容                               |
| like_count    | int          | 是       | 0        | 点赞数                                 |
| comment_count | int          | 是       | 0        | 评论数                                 |
| collect_count | int          | 是       | 0        | 收藏数                                 |
| status        | tinyint      | 是       | 1        | 状态（1 = 正常，0 = 禁用，2 = 审核中） |
| is_deleted    | tinyint      | 是       | 0        | 逻辑删除                               |
| create_time   | datetime     | 是       | 当前时间 | 创建时间                               |
| update_time   | datetime     | 是       | 当前时间 | 更新时间                               |



**（9）评论表（community_comment）：**

| 字段名      | 字段类型     | 是否非空 | 默认值   | 注释                                 |
| ----------- | ------------ | -------- | -------- | ------------------------------------ |
| id          | bigint       | 是       | 自增     | 主键 ID                              |
| topic_id    | bigint       | 是       | -        | 话题 ID（关联 community_topic.id）   |
| user_id     | bigint       | 是       | -        | 评论用户 ID                          |
| parent_id   | bigint       | 是       | 0        | 父评论 ID（0 = 一级评论，>0 = 回复） |
| content     | varchar(500) | 是       | -        | 评论内容                             |
| like_count  | int          | 是       | 0        | 点赞数                               |
| is_deleted  | tinyint      | 是       | 0        | 逻辑删除                             |
| create_time | datetime     | 是       | 当前时间 | 创建时间                             |



**（10）点赞表（community_like）：**

| 字段名      | 字段类型                          | 是否非空 | 默认值   | 注释                           |
| ----------- | --------------------------------- | -------- | -------- | ------------------------------ |
| id          | bigint                            | 是       | 自增     | 主键 ID                        |
| user_id     | bigint                            | 是       | -        | 点赞用户 ID                    |
| target_type | tinyint                           | 是       | -        | 点赞类型（1 = 话题，2 = 评论） |
| target_id   | bigint                            | 是       | -        | 点赞目标 ID（话题 / 评论 ID）  |
| is_deleted  | tinyint                           | 是       | 0        | 逻辑删除                       |
| create_time | datetime                          | 是       | 当前时间 | 创建时间                       |
| unique key  | (user_id, target_type, target_id) | -        | -        | 唯一索引（防止重复点赞）       |



**（11）用户收藏表（user_collection）：**

| **字段名**  | 字段类型                          | 是否非空 | 默认值   | 注释                                     |
| ----------- | --------------------------------- | -------- | -------- | ---------------------------------------- |
| id          | bigint                            | 是       | 自增     | 主键 ID                                  |
| user_id     | bigint                            | 是       | -        | 收藏用户 ID                              |
| target_type | tinyint                           | 是       | -        | 收藏类型（1 = 医院，2 = 医生，3 = 话题） |
| target_id   | bigint                            | 是       | -        | 收藏目标 ID（医院 / 医生 / 话题 ID）     |
| is_deleted  | tinyint                           | 是       | 0        | 逻辑删除（取消收藏 = 标记为 1）          |
| create_time | datetime                          | 是       | 当前时间 | 创建时间                                 |
| unique key  | (user_id, target_type, target_id) | -        | -        | 唯一索引（防止重复收藏）                 |



### 3. 数据库表关系
