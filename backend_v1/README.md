# 医院选择系统 - 后端项目

## 项目概述

基于SpringBoot + MyBatis + MySQL + JWT的医院选择系统后端服务。

## 技术栈

- **SpringBoot**: 2.7.14
- **MyBatis**: 2.3.1
- **MySQL**: 8.0+
- **JWT**: 0.11.5
- **Swagger**: 3.0.0
- **Lombok**: 最新版
- **PageHelper**: 1.4.6

## 项目结构

```
backend/
├── pom.xml                                          # Maven配置
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
│   │   │   │   └── impl/                           # 服务实现类
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
│   │   │   │   ├── Comment.java
│   │   │   │   ├── Like.java
│   │   │   │   ├── Collection.java
│   │   │   │   ├── Message.java
│   │   │   │   ├── MedicalHistory.java
│   │   │   │   └── QueryHistory.java
│   │   │   ├── dto/                                # 数据传输对象（15个）
│   │   │   │   ├── UserLoginDTO.java
│   │   │   │   ├── UserRegisterDTO.java
│   │   │   │   ├── UserUpdateDTO.java
│   │   │   │   ├── PasswordUpdateDTO.java
│   │   │   │   ├── HospitalFilterDTO.java
│   │   │   │   ├── DoctorFilterDTO.java
│   │   │   │   ├── DiseaseQueryDTO.java
│   │   │   │   ├── TopicPublishDTO.java
│   │   │   │   ├── TopicUpdateDTO.java
│   │   │   │   ├── CommentDTO.java
│   │   │   │   ├── MessageSendDTO.java
│   │   │   │   ├── MedicalHistoryDTO.java
│   │   │   │   ├── CollectionDTO.java
│   │   │   │   ├── PageQueryDTO.java
│   │   │   │   └── IdDTO.java
│   │   │   ├── vo/                                 # 视图对象（19个）
│   │   │   │   ├── Result.java                      # 统一返回格式
│   │   │   │   ├── PageResult.java                  # 分页返回格式
│   │   │   │   ├── UserVO.java
│   │   │   │   ├── UserProfileVO.java
│   │   │   │   ├── HospitalVO.java
│   │   │   │   ├── HospitalSimpleVO.java
│   │   │   │   ├── DoctorVO.java
│   │   │   │   ├── DoctorSimpleVO.java
│   │   │   │   ├── DepartmentVO.java
│   │   │   │   ├── DiseaseVO.java
│   │   │   │   ├── AreaVO.java
│   │   │   │   ├── TopicVO.java
│   │   │   │   ├── TopicDetailVO.java
│   │   │   │   ├── CommentVO.java
│   │   │   │   ├── MessageVO.java
│   │   │   │   ├── MessageUnreadVO.java
│   │   │   │   ├── CollectionVO.java
│   │   │   │   ├── MedicalHistoryVO.java
│   │   │   │   └── QueryHistoryVO.java
│   │   │   ├── exception/                          # 异常处理（5个）
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── AuthException.java
│   │   │   │   ├── ParameterException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── util/                               # 工具类（6个）
│   │   │       ├── JwtUtil.java
│   │   │       ├── PasswordUtil.java
│   │   │       ├── PageUtil.java
│   │   │       ├── ValidationUtil.java
│   │   │       ├── DateUtil.java
│   │   │       └── JsonUtil.java
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置
│   │       └── mapper/                              # MyBatis XML映射（15个）
│   │           ├── UserMapper.xml
│   │           ├── RoleMapper.xml
│   │           ├── UserRoleMapper.xml
│   │           ├── HospitalMapper.xml
│   │           ├── DepartmentMapper.xml
│   │           ├── DoctorMapper.xml
│   │           ├── DiseaseMapper.xml
│   │           ├── AreaMapper.xml
│   │           ├── TopicMapper.xml
│   │           ├── CommentMapper.xml
│   │           ├── LikeMapper.xml
│   │           ├── CollectionMapper.xml
│   │           ├── MessageMapper.xml
│   │           ├── MedicalHistoryMapper.xml
│   │           └── QueryHistoryMapper.xml
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

### 2. 修改配置

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hospital_selection?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的数据库用户名
    password: root          # 修改为你的数据库密码

jwt:
  secret: hospital-selection-secret-key-2024-very-long-and-secure  # 修改为你的密钥
  expiration: 604800000     # 7天
```

### 3. 运行项目

```bash
# 使用Maven运行
cd backend
mvn spring-boot:run

# 或使用IDE运行HospitalSelectionApplication.java
```

### 4. 访问

- **应用地址**: http://localhost:8080/api
- **Swagger文档**: http://localhost:8080/api/swagger-ui/

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

#### 医院相关
- GET `/api/hospital/list` - 医院列表（分页）
- POST `/api/hospital/filter` - 医院多条件筛选（核心功能）
- GET `/api/hospital/{id}` - 医院详情

#### 医生相关
- GET `/api/doctor/list` - 医生列表（分页）
- GET `/api/doctor/{id}` - 医生详情
- POST `/api/doctor/filter` - 医生筛选

#### 社区相关
- GET `/api/community/topics` - 话题列表
- POST `/api/community/topic` - 发布话题（需要登录）
- POST `/api/community/comment` - 发表评论（需要登录）
- POST `/api/community/like/topic` - 点赞话题（需要登录）

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

### 1. 多条件筛选医院

支持以下筛选条件组合：
- 疾病类型
- 医院等级
- 地理位置（省/市/区）
- 医保定点
- 重点科室
- 评分范围

### 2. JWT认证

- 用户登录后返回JWT Token
- Token有效期7天
- 支持Token刷新

### 3. 分页查询

- 所有列表接口支持分页
- 使用PageHelper插件
- 默认每页10条，最大100条

### 4. 统一异常处理

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

### 事务管理

在Service方法上添加 `@Transactional` 注解：

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

## 注意事项

1. **数据库连接**: 确保MySQL服务已启动
2. **字符编码**: 数据库和表使用utf8mb4字符集
3. **JWT密钥**: 生产环境请修改为复杂的密钥
4. **跨域配置**: 开发环境已配置CORS，生产环境请修改
5. **文件上传**: 默认上传到本地磁盘，生产环境建议使用云存储

## 常见问题

### Q1: 启动报错"Access denied for user"

A: 检查application.yml中的数据库用户名和密码是否正确。

### Q2: Swagger无法访问

A: 检查SpringSecurityConfig.java中是否正确配置了Swagger相关路径的访问权限。

### Q3: 接口返回401

A: 需要登录的接口请在Header中添加：`Authorization: Bearer <token>`

## 联系方式

- 作者：陈文滔
- 项目：基于SpringBoot的智能医院选择网站的设计与实现
- 版本：1.0.0
