# 医院选择系统 - 后端项目

## 项目概述

基于SpringBoot + MyBatis + MySQL + JWT的医院选择系统后端服务。

**最新更新** (2026-01-31):
- ✅ 修复所有Service层的内存分页问题，改用物理分页
- ✅ 修复N+1查询问题，使用JOIN查询优化性能
- ✅ 支持环境变量配置，增强安全性

详细改进内容请查看 [ARCHITECTURE_IMPROVEMENTS.md](ARCHITECTURE_IMPROVEMENTS.md)

## 技术栈

- **SpringBoot**: 2.7.14
- **MyBatis**: 2.3.1
- **PageHelper**: 1.4.6 (物理分页)
- **MySQL**: 8.0+
- **JWT**: 0.11.5
- **Swagger**: 3.0.0
- **Lombok**: 最新版

## 项目结构

```
backend/
├── pom.xml                                          # Maven配置
├── ARCHITECTURE_IMPROVEMENTS.md                     # 架构改进文档
├── src/
│   ├── main/
│   │   ├── java/com/chen/HospitalSelection/
│   │   │   ├── HospitalSelectionApplication.java  # 主启动类
│   │   │   ├── config/                             # 配置层（4个）
│   │   │   │   ├── SpringSecurityConfig.java       # Spring Security + JWT配置
│   │   │   │   ├── MyBatisConfig.java              # MyBatis + 分页配置
│   │   │   │   ├── WebMvcConfig.java               # Web MVC配置
│   │   │   │   └── SwaggerConfig.java              # Swagger API文档配置
│   │   │   ├── controller/                         # 控制层（12个）
│   │   │   │   ├── UserController.java              # 用户接口
│   │   │   │   ├── HospitalController.java          # 医院接口
│   │   │   │   ├── DoctorController.java            # 医生接口
│   │   │   │   ├── DepartmentController.java        # 科室接口
│   │   │   │   ├── DiseaseController.java           # 疾病接口
│   │   │   │   ├── AreaController.java              # 地区接口
│   │   │   │   ├── CommunityController.java         # 社区接口
│   │   │   │   ├── MessageController.java           # 私信接口
│   │   │   │   ├── CollectionController.java        # 收藏接口
│   │   │   │   ├── MedicalHistoryController.java    # 病史接口
│   │   │   │   ├── QueryHistoryController.java      # 查询历史接口
│   │   │   │   └── RoleController.java              # 角色接口
│   │   │   ├── service/                            # 服务层（12个接口+12个实现）
│   │   │   │   ├── UserService.java
│   │   │   │   ├── HospitalService.java
│   │   │   │   ├── DoctorService.java
│   │   │   │   ├── DepartmentService.java
│   │   │   │   ├── DiseaseService.java
│   │   │   │   ├── AreaService.java
│   │   │   │   ├── CommunityService.java
│   │   │   │   ├── MessageService.java
│   │   │   │   ├── CollectionService.java
│   │   │   │   ├── MedicalHistoryService.java
│   │   │   │   ├── QueryHistoryService.java
│   │   │   │   ├── RoleService.java
│   │   │   │   └── impl/                           # 服务实现类（已优化性能）
│   │   │   │       ├── UserServiceImpl.java
│   │   │   │       ├── HospitalServiceImpl.java
│   │   │   │       ├── DoctorServiceImpl.java
│   │   │   │       ├── DepartmentServiceImpl.java
│   │   │   │       ├── DiseaseServiceImpl.java
│   │   │   │       ├── AreaServiceImpl.java
│   │   │   │       ├── CommunityServiceImpl.java
│   │   │   │       ├── MessageServiceImpl.java
│   │   │   │       ├── CollectionServiceImpl.java
│   │   │   │       ├── MedicalHistoryServiceImpl.java
│   │   │   │       ├── QueryHistoryServiceImpl.java
│   │   │   │       └── RoleServiceImpl.java
│   │   │   ├── mapper/                             # 数据访问层（16个）
│   │   │   │   ├── UserMapper.java
│   │   │   │   ├── RoleMapper.java
│   │   │   │   ├── UserRoleMapper.java
│   │   │   │   ├── HospitalMapper.java
│   │   │   │   ├── DepartmentMapper.java
│   │   │   │   ├── DoctorMapper.java
│   │   │   │   ├── DiseaseMapper.java
│   │   │   │   ├── AreaMapper.java
│   │   │   │   ├── TopicMapper.java
│   │   │   │   ├── CommentMapper.java
│   │   │   │   ├── LikeMapper.java
│   │   │   │   ├── CollectionMapper.java
│   │   │   │   ├── MessageMapper.java
│   │   │   │   ├── MedicalHistoryMapper.java
│   │   │   │   ├── QueryHistoryMapper.java
│   │   │   │   └── FilterMapper.java
│   │   │   ├── model/                              # 实体层（15个）
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── Hospital.java
│   │   │   │   ├── Department.java
│   │   │   │   ├── Doctor.java
│   │   │   │   ├── Disease.java
│   │   │   │   ├── Area.java
│   │   │   │   ├── Topic.java
│   │   │   │   ├── Comment.java                   # 已优化，包含用户信息字段
│   │   │   │   ├── Like.java
│   │   │   │   ├── Collection.java
│   │   │   │   ├── Message.java                   # 已优化，包含用户信息字段
│   │   │   │   ├── MedicalHistory.java
│   │   │   │   └── QueryHistory.java
│   │   │   ├── dto/                                # 数据传输对象
│   │   │   ├── vo/                                 # 视图对象
│   │   │   ├── exception/                          # 异常处理
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── util/                               # 工具类
│   │   │       ├── JwtUtil.java
│   │   │       └── PasswordUtil.java
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置（支持环境变量）
│   │       └── mapper/                              # MyBatis XML映射
│   └── test/                                        # 测试代码
└── README.md                                        # 项目说明
```

## 快速开始

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE hospital_selection DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导入数据
USE hospital_selection;
SOURCE sql/init_database.sql;

-- 导入模拟数据（可选）
python simulated_data/run_all.py
```

### 2. 配置设置

#### 方式一：直接修改配置文件（开发环境）

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_selection?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的数据库用户名
    password: root          # 修改为你的数据库密码

jwt:
  secret: your-secret-key-here    # 修改为你的密钥
  expiration: 604800000           # 7天

file:
  upload-path: D:/upload/hospital-selection    # 文件保存路径
```

#### 方式二：使用环境变量（推荐用于生产环境）

```bash
# Windows
set DB_URL=jdbc:mysql://localhost:3306/hospital_selection
set DB_USERNAME=root
set DB_PASSWORD=your_password
set JWT_SECRET=your-production-secret-key
set FILE_UPLOAD_PATH=/path/to/upload

# Linux/Mac
export DB_URL=jdbc:mysql://localhost:3306/hospital_selection
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-production-secret-key
export FILE_UPLOAD_PATH=/path/to/upload
```

**支持的环境变量**:

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/hospital_selection |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root |
| JWT_SECRET | JWT签名密钥 | hospital-selection-secret-key-2024 |
| JWT_EXPIRATION | JWT过期时间（毫秒） | 604800000 (7天) |
| FILE_UPLOAD_PATH | 文件上传保存路径 | D:/upload/hospital-selection |
| FILE_ACCESS_URL | 文件访问URL前缀 | /uploads |

### 3. 运行项目

```bash
# 使用Maven运行
cd backend
mvn spring-boot:run

# 或使用IDE运行HospitalSelectionApplication.java
```

### 4. 访问

- **应用地址**: http://localhost:8088/api
- **Swagger文档**: http://localhost:8088/api/swagger-ui/

## API接口文档

### 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {...}
}
```

### 分页返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10,
    "list": [...]
  }
}
```

### 核心接口

#### 用户相关
- POST `/api/user/register` - 用户注册
- POST `/api/user/login` - 用户登录
- GET `/api/user/info` - 获取用户信息（需要登录）
- PUT `/api/user/info` - 更新用户信息（需要登录）
- PUT `/api/user/password` - 修改密码（需要登录）
- POST `/api/user/password/reset` - 找回密码

#### 医院相关
- GET `/api/hospital/list` - 医院列表（分页，物理分页）
- POST `/api/hospital/filter` - 医院多条件筛选（核心功能，已优化）
- GET `/api/hospital/{id}` - 医院详情
- GET `/api/hospital/search` - 搜索医院（物理分页）
- GET `/api/hospital/search/suggest` - 搜索建议

#### 医生相关
- GET `/api/doctor/list` - 医生列表（分页，物理分页）
- GET `/api/doctor/{id}` - 医生详情
- POST `/api/doctor/filter` - 医生筛选（已优化）

#### 社区相关
- GET `/api/community/topics` - 话题列表（物理分页）
- GET `/api/community/topic/{id}` - 话题详情
- POST `/api/community/topic` - 发布话题（需要登录）
- PUT `/api/community/topic/{id}` - 修改话题（需要登录）
- DELETE `/api/community/topic/{id}` - 删除话题（需要登录）
- POST `/api/community/comment` - 发表评论（需要登录）
- DELETE `/api/community/comment/{id}` - 删除评论（需要登录）
- POST `/api/community/like/topic` - 点赞话题（需要登录）
- DELETE `/api/community/like/topic` - 取消点赞话题（需要登录）

#### 私信相关
- GET `/api/message/conversations` - 会话列表（已优化，解决N+1查询）
- GET `/api/message/history/{otherUserId}` - 聊天记录（物理分页，已优化）
- POST `/api/message/send` - 发送私信（需要登录）
- PUT `/api/message/read/{id}` - 标记已读（需要登录）

更多接口请查看Swagger文档。

## 数据库表说明

系统共15张表，分为5个模块：

1. **用户权限模块**（3张）
   - sys_user - 用户表
   - sys_role - 角色表
   - sys_user_role - 用户角色关联表

2. **医院医生模块**（3张）
   - hospital_info - 医院信息表
   - hospital_department - 医院科室表
   - doctor_info - 医生信息表

3. **基础数据模块**（2张）
   - disease_type - 疾病分类表
   - area_info - 省市区表

4. **社区交流模块**（3张）
   - community_topic - 社区话题表
   - community_comment - 评论表
   - community_like - 点赞表

5. **用户数据模块**（4张）
   - user_collection - 用户收藏表
   - user_message - 用户私信表
   - user_medical_history - 用户病史表
   - user_query_history - 查询历史表

## 核心功能

### 1. 多条件筛选医院（已优化）

支持以下筛选条件组合：
- 疾病类型
- 医院等级
- 地理位置（省/市/区）
- 医保定点
- 重点科室
- 评分范围

**性能优化**：使用动态SQL在数据库层面进行过滤，结合物理分页。

### 2. JWT认证

- 用户登录后返回JWT Token
- Token有效期7天（可配置）
- 支持环境变量配置密钥

### 3. 物理分页（已优化）

- 所有列表接口使用PageHelper物理分页
- 数据库层分页，避免内存溢出
- 支持大数据量场景

### 4. N+1查询优化（已优化）

- 会话列表使用JOIN查询，避免循环查询用户信息
- 评论列表优化，减少数据库往返次数
- Message和Comment模型新增用户信息字段（transient）

### 5. 统一异常处理

- 全局异常拦截器
- 统一返回格式
- 详细的错误信息

## 开发说明

### 添加新接口

1. 在Mapper中定义方法
2. 在Mapper XML中编写SQL
3. 在Service接口中定义业务方法
4. 在ServiceImpl中实现业务逻辑
5. 在Controller中定义接口

### 性能优化建议

1. **分页查询**：使用PageHelper进行物理分页
   ```java
   PageHelper.startPage(dto.getPage(), dto.getPageSize());
   List<T> list = mapper.selectByCondition(params);
   PageInfo<T> pageInfo = new PageInfo<>(list);
   ```

2. **避免N+1查询**：使用MyBatis关联查询
   ```xml
   <select id="selectWithUser" resultMap="WithUserResultMap">
       SELECT
           t.*, u.nickname as user_nickname, u.avatar as user_avatar
       FROM table_name t
       LEFT JOIN sys_user u ON t.user_id = u.id
       WHERE t.id = #{id}
   </select>
   ```

3. **事务管理**：在Service方法上添加 `@Transactional` 注解
   ```java
   @Override
   @Transactional
   public void publishTopic(Long userId, TopicPublishDTO dto) {
       // 事务操作
   }
   ```

### 日志使用

使用Lombok的 `@Slf4j` 注解：

```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public void login(String phone) {
        log.info("用户登录: {}", phone);
    }
}
```

## 架构改进记录

### P0 - 已完成（2026-01-31）

1. **内存分页问题修复** ✅
   - 修复所有Service实现类的内存分页问题
   - 改用PageHelper实现物理分页
   - 影响文件：HospitalServiceImpl, UserServiceImpl, DoctorServiceImpl等

2. **N+1查询问题修复** ✅
   - 优化MessageServiceImpl.getConversations()
   - 优化CommunityServiceImpl.convertToCommentVO()
   - 新增关联查询方法到Mapper

3. **硬编码消除** ✅
   - 数据库配置支持环境变量
   - JWT密钥支持环境变量
   - 文件路径支持环境变量

### 后续优化方向（P1/P2）

1. **P1 - 近期实施**
   - 添加批量操作接口
   - 完善Service层异常处理
   - 添加全局拦截器（日志、性能监控）
   - 增强参数验证

2. **P2 - 后续优化**
   - 引入缓存机制（Redis）
   - 优化数据库索引
   - 完善业务逻辑（推荐算法等）

详细内容请查看 [ARCHITECTURE_IMPROVEMENTS.md](ARCHITECTURE_IMPROVEMENTS.md)

## 注意事项

1. **数据库连接**: 确保MySQL服务已启动
2. **字符编码**: 数据库和表使用utf8mb4字符集
3. **JWT密钥**: 生产环境请使用环境变量配置复杂密钥
4. **跨域配置**: 开发环境已配置CORS，生产环境请修改
5. **文件上传**: 默认上传到本地磁盘，生产环境建议使用云存储
6. **环境变量**: 生产环境建议使用环境变量配置敏感信息

## 常见问题

### Q1: 启动报错"Access denied for user"

A: 检查环境变量或application.yml中的数据库用户名和密码是否正确。

### Q2: Swagger无法访问

A: 检查SpringSecurityConfig.java中是否正确配置了Swagger相关路径的访问权限。

### Q3: 接口返回401

A: 需要登录的接口请在Header中添加：`Authorization: Bearer <token>`

### Q4: 分页查询性能慢

A: 已使用物理分页优化，如仍有问题请检查数据库索引。

## 测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=HospitalServiceTest

# 查看测试覆盖率报告
mvn clean test jacoco:report
```

## 部署

### 打包

```bash
mvn clean package -DskipTests
```

### 运行jar包

```bash
java -jar target/HospitalSelection-1.0.0.jar

# 或指定配置文件
java -jar target/HospitalSelection-1.0.0.jar --spring.config.location=file:./application-prod.yml
```

## 联系方式

- 作者：陈文滔
- 项目：基于SpringBoot的智能医院选择网站的设计与实现
- 版本：1.1.0
- 更新日期：2026-01-31
