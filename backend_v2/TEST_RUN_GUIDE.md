# 后端测试运行指南与结果分析

## 测试运行时间
**执行时间：** 2026-02-01

## 已完成的测试改进工作

### 1. 创建的测试文件

| 文件 | 类型 | 说明 |
|------|------|------|
| `IntegrationTestBase.java` | 基类 | 集成测试基类，使用真实数据库 |
| `controller/UserControllerTest.java` | 改进 | 从Mock测试改为真实集成测试 |
| `controller/DoctorControllerTest.java` | 新增 | 医生控制器集成测试 |
| `controller/MessageControllerTest.java` | 新增 | 私信控制器集成测试 |
| `controller/CollectionControllerTest.java` | 新增 | 收藏控制器集成测试 |

### 2. 测试改进统计

| 指标 | 改进前 | 改进后 |
|------|--------|--------|
| UserController测试 | Mock测试（6个用例） | 集成测试（11个用例） |
| 验证方式 | 只验证Mock调用 | 验证数据库+业务逻辑 |
| 能发现bug | ❌ 不能 | ✅ 能 |
| 新增Controller测试 | 0个 | 3个 |

---

## 测试运行方式

### 方式一：使用Maven命令（推荐）

```bash
# 进入后端目录
cd backend

# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserControllerTest
mvn test -Dtest=DoctorControllerTest
mvn test -Dtest=MessageControllerTest
mvn test -Dtest=CollectionControllerTest

# 运行多个测试类
mvn test -Dtest=UserControllerTest,DoctorControllerTest
```

### 方式二：使用批处理脚本（Windows）

```batch
# 方式1：运行所有测试
cd backend
run_tests.bat

# 方式2：运行特定测试
cd backend
mvn test -Dtest=UserControllerTest > test_output.txt 2>&1
type test_output.txt
```

### 方式三：在IDE中运行

**IDEA：**
1. 右键点击测试类或测试方法
2. 选择 "Run" 或 "Debug"
3. 查看测试结果窗口

**Eclipse：**
1. 右键点击测试类
2. 选择 "Run As" → "JUnit Test"

---

## 预期测试结果

### 成功的情况
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.chen.HospitalSelection.controller.UserControllerTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

### 有失败的情况
```
[INFO] Tests run: 11, Failures: 3, Errors: 1, Skipped: 0

[ERROR] testRegister_Success(com.chen.HospitalSelection.controller.UserControllerTest)
[INFO] ...
Caused by: ...
```

---

## 常见问题排查

### 问题1：编译错误 - 编码问题

**现象：**
```
错误: 编码 GBK 的不可映射字符
```

**解决方案：**
1. 已在 `pom.xml` 中配置 UTF-8 编码：
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

2. 确保 IDE 使用 UTF-8 编码：
   - IDEA: Settings → File Encodings → 全部设置为 UTF-8
   - Eclipse: Project → Properties → Resource → Text file encoding

### 问题2：测试失败 - 数据库连接失败

**现象：**
```
Cannot create PoolableConnectionFactory
Unable to create initial connections of pool
```

**解决方案：**
1. 检查H2数据库配置：`src/test/resources/application-test.yml`
2. 测试使用内存数据库，不需要MySQL
3. 确保依赖中有H2：
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### 问题3：测试失败 - Bean不存在

**现象：**
```
NoSuchBeanDefinitionException: No qualifying bean of type 'xxx'
```

**解决方案：**
1. 确保 `@SpringBootTest` 扫描到所有包
2. 检查 `@ComponentScan` 配置
3. 确保主应用类上有 `@SpringBootApplication`

---

## 测试验证清单

运行测试后，检查以下项目：

### UserControllerTest（11个测试）

- [ ] testRegister_Success - 用户注册成功
- [ ] testRegister_PhoneAlreadyExists - 手机号已存在
- [ ] testRegister_ValidationFailed - 参数校验失败
- [ ] testLogin_Success - 登录成功
- [ ] testLogin_WrongPassword - 密码错误
- [ ] testLogout_Success - 登出成功
- [ ] testGetUserInfo_Success - 获取用户信息
- [ ] testUpdateUserInfo_Success - 更新用户信息
- [ ] testUpdatePassword_Success - 修改密码成功
- [ ] testUpdatePassword_WrongOldPassword - 原密码错误

### DoctorControllerTest（5个测试）

- [ ] testGetDoctorDetail_Success - 获取医生详情
- [ ] testGetDoctorList_Success - 获取医生列表
- [ ] testGetDoctorsByHospital_Success - 按医院查询医生
- [ ] testFilterDoctors_Success - 医生筛选
- [ ] testGetDoctorDetail_NotFound - 医生不存在

### MessageControllerTest（7个测试）

- [ ] testSendMessage_Success - 发送私信成功
- [ ] testSendMessage_ReceiverNotFound - 接收者不存在
- [ ] testSendMessage_CannotSendToSelf - 不能发给自己
- [ ] testGetConversations_Success - 获取会话列表
- [ ] testGetMessageHistory_Success - 获取消息历史
- [ ] testMarkAsRead_Success - 标记已读
- [ ] testGetUnreadCount_Success - 获取未读消息数

### CollectionControllerTest（7个测试）

- [ ] testAddCollection_Hospital - 添加收藏（医院）
- [ ] testAddCollection_AlreadyExists - 重复收藏
- [ ] testCancelCollection_Success - 取消收藏
- [ ] testGetCollectionList_Success - 获取收藏列表
- [ ] testCheckCollected_True - 已收藏
- [ ] testCheckCollected_False - 未收藏
- [ ] testGetCollectionCount_Success - 收藏数量统计

---

## 测试失败时的修复流程

### 步骤1：查看详细错误信息

```bash
cd backend
mvn test -Dtest=UserControllerTest -X
```

`-X` 参数会输出详细的执行过程，帮助定位问题。

### 步骤2：分析错误原因

常见错误类型：
1. **AssertionError** - 断言失败，实际值与预期不符
2. **SQLException** - SQL错误，数据库操作失败
3. **NoSuchBeanException** - Bean不存在，依赖注入问题
4. **ValidationException** - 参数校验失败

### 步骤3：修复后端代码

根据错误信息，定位到具体的服务/控制器代码：

| 错误类型 | 修复位置 | 示例 |
|---------|---------|------|
| 断言失败 | Service层 | 修复业务逻辑 |
| SQL错误 | Mapper.xml | 修复SQL语句 |
| 参数校验失败 | Controller/DTO | 添加@Valid注解或校验逻辑 |
| 依赖注入失败 | 配置类 | 添加@Component或@Configuration |

### 步骤4：重新运行测试

```bash
mvn test -Dtest=UserControllerTest
```

### 步骤5：验证修复

确认测试通过后：
1. 检查业务功能是否正常
2. 验证数据库操作是否正确
3. 确认没有引入新的bug

---

## 当前测试状态

### 已有测试（需改进）

| 测试类 | 类型 | 状态 |
|--------|------|------|
| UserServiceTest | Mock测试 | 保留作为单元测试示例 |
| HospitalServiceTest | Mock测试 | 保留 |
| CommunityServiceTest | Mock测试 | 保留 |
| DoctorServiceTest | Mock测试 | 保留 |
| MessageServiceTest | Mock测试 | 保留 |
| CollectionServiceTest | Mock测试 | 保留 |
| ... | Mock测试 | 保留 |

| HospitalControllerTest | Mock测试 | ⚠️ 需要改进为集成测试 |
| CommunityControllerTest | Mock测试 | ⚠️ 需要改进为集成测试 |

### 新增集成测试

| 测试类 | 测试用例数 | 类型 | 状态 |
|--------|----------|------|------|
| UserControllerTest | 11 | 真实集成测试 | ✅ 完成 |
| DoctorControllerTest | 5 | 真实集成测试 | ✅ 完成 |
| MessageControllerTest | 7 | 真实集成测试 | ✅ 完成 |
| CollectionControllerTest | 7 | 真实集成测试 | ✅ 完成 |

---

## 下一步工作

### 立即执行

1. **运行测试**
   ```bash
   cd backend
   mvn test -Dtest=UserControllerTest,DoctorControllerTest,MessageControllerTest,CollectionControllerTest
   ```

2. **分析测试结果**
   - 如果全部通过 → 验证功能正常
   - 如果有失败 → 查看错误信息

3. **修复失败的后端代码**
   - 定位bug
   - 修复代码
   - 重新测试

4. **记录修复内容**
   - 记录发现的bug
   - 记录修复方案
   - 更新文档

### 短期计划

1. **改进现有Mock测试**
   - HospitalControllerTest
   - CommunityControllerTest

2. **添加缺失的Controller测试**
   - AreaControllerTest
   - DepartmentControllerTest
   - DiseaseControllerTest
   - MedicalHistoryControllerTest
   - QueryHistoryControllerTest
   - RoleControllerTest

3. **添加Service集成测试**
   - UserServiceIntegrationTest
   - HospitalServiceIntegrationTest
   - CommunityServiceIntegrationTest

---

## 测试文件位置

```
backend/src/test/java/com/chen/HospitalSelection/
├── IntegrationTestBase.java              ← 集成测试基类
├── controller/
│   ├── UserControllerTest.java          ← 改进：11个集成测试用例
│   ├── HospitalControllerTest.java     ← 保留：Mock测试（需改进）
│   ├── CommunityControllerTest.java   ← 保留：Mock测试（需改进）
│   ├── DoctorControllerTest.java       ← 新增：5个集成测试用例
│   ├── MessageControllerTest.java     ← 新增：7个集成测试用例
│   └── CollectionControllerTest.java  ← 新增：7个集成测试用例
└── service/
    ├── UserServiceTest.java          ← 保留：Mock单元测试
    ├── HospitalServiceTest.java      ← 保留：Mock单元测试
    ├── CommunityServiceTest.java   ← 保留：Mock单元测试
    └── ... (其他Service Mock测试)
```

---

## 技术要点

### 集成测试配置

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
```

**说明：**
- `@SpringBootTest` - 启动完整的Spring容器
- `@AutoConfigureMockMvc` - 配置MockMvc
- `@ActiveProfiles("test")` - 使用test配置文件
- `@Transactional` - 测试后自动回滚，保证测试间独立

### 测试数据库配置

**文件：** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
```

**特点：**
- 使用H2内存数据库
- MySQL兼容模式
- 测试后数据丢失（事务回滚）
- 不需要外部数据库

---

## 总结

### 已完成

1. ✅ 创建集成测试基类
2. ✅ 改进UserControllerTest（6→11个用例）
3. ✅ 创建DoctorControllerTest（5个用例）
4. ✅ 创建MessageControllerTest（7个用例）
5. ✅ 创建CollectionControllerTest（7个用例）
6. ✅ 配置UTF-8编码支持

### 测试改进

**从Mock测试到集成测试的转变：**
- ❌ Mock测试：只验证Mock被调用，不能发现真实bug
- ✅ 集成测试：验证完整功能链路，能发现真实bug

**预期收益：**
- 测试失败时能暴露后端代码的真实问题
- 修复后能确保功能正常工作
- 提高代码质量和可靠性

### 运行测试

**命令：**
```bash
cd backend
mvn test
```

**预期：**
- 有30个集成测试用例执行
- 部分测试可能失败（暴露bug）
- 需要修复后端代码（不是测试代码）
- 最终所有测试通过

---

**文档创建时间：** 2026-02-01
**测试改进进度：** 30% (4/14个Controller有集成测试)
**下一步：** 运行测试并修复发现的后端bug
