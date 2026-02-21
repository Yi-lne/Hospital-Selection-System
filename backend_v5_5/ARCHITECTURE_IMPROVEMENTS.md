# 后端架构改进总结

## 改进日期
2026-01-31

## 改进范围
本次改进主要针对后端架构中的P0（严重）问题，确保系统性能和稳定性得到显著提升。

---

## 一、内存分页问题修复（P0 - 必须修复）

### 问题描述
所有Service实现使用`selectAll() + 内存分页`，存在严重的性能问题和内存溢出风险。

### 解决方案
使用PageHelper实现物理分页，将分页逻辑下推到数据库层面。

### 修改的文件

#### 1. HospitalServiceImpl.java
**修改方法**:
- `getHospitalList()` - 使用PageHelper.startPage()
- `filterHospitals()` - 使用selectByCondition + PageHelper
- `searchHospitals()` - 使用PageHelper物理分页

**修改示例**:
```java
// 修改前（内存分页）
List<Hospital> allList = hospitalMapper.selectAll();
long total = allList.size();
int offset = (dto.getPage() - 1) * dto.getPageSize();
List<Hospital> pageList = allList.subList(offset, offset + pageSize);

// 修改后（物理分页）
PageHelper.startPage(dto.getPage(), dto.getPageSize());
List<Hospital> hospitalList = hospitalMapper.selectAll();
PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);
return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), ...);
```

#### 2. UserServiceImpl.java
**修改方法**:
- `getUserList()` - 使用PageHelper物理分页

#### 3. DoctorServiceImpl.java
**修改方法**:
- `getDoctorList()` - 使用PageHelper物理分页
- `filterDoctors()` - 使用selectBySimpleCondition + PageHelper
- `searchDoctors()` - 使用PageHelper物理分页

#### 4. CommunityServiceImpl.java
**修改方法**:
- `getTopicList()` - 使用PageHelper物理分页
- `getTopicsByDisease()` - 使用PageHelper物理分页
- `getCommentList()` - 使用PageHelper物理分页
- `getMyTopics()` - 使用PageHelper物理分页
- `getMyComments()` - 使用PageHelper物理分页
- `getHotTopics()` - 使用PageHelper物理分页

#### 5. MessageServiceImpl.java
**修改方法**:
- `getMessageHistory()` - 使用PageHelper物理分页

#### 6. CollectionServiceImpl.java
**修改方法**:
- `getCollectionList()` - 使用PageHelper物理分页

#### 7. QueryHistoryServiceImpl.java
**修改方法**:
- `getQueryHistoryList()` - 使用PageHelper物理分页
- `getHospitalQueryHistory()` - 使用PageHelper物理分页
- `getDoctorQueryHistory()` - 使用PageHelper物理分页
- `getTopicQueryHistory()` - 使用PageHelper物理分页

#### 8. MedicalHistoryServiceImpl.java
**修改方法**:
- `getMedicalHistoryPage()` - 使用PageHelper物理分页

### 新增/修改的Mapper方法

#### HospitalMapper.java
```java
// 新增动态条件查询方法
List<Hospital> selectByCondition(
    @Param("hospitalLevel") String hospitalLevel,
    @Param("provinceCode") String provinceCode,
    @Param("cityCode") String cityCode,
    @Param("areaCode") String areaCode,
    @Param("isMedicalInsurance") Integer isMedicalInsurance,
    @Param("keyDepartments") String keyDepartments
);
```

#### DoctorMapper.java
```java
// 新增简单条件查询方法
List<Doctor> selectBySimpleCondition(
    @Param("hospitalId") Long hospitalId,
    @Param("deptId") Long deptId,
    @Param("title") String title
);
```

#### DoctorMapper.xml
新增 `selectBySimpleCondition` 查询：
```xml
<select id="selectBySimpleCondition" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM doctor_info
    <where>
        is_deleted = 0
        <if test="hospitalId != null">
            AND hospital_id = #{hospitalId}
        </if>
        <if test="deptId != null">
            AND dept_id = #{deptId}
        </if>
        <if test="title != null and title != ''">
            AND title = #{title}
        </if>
    </where>
    ORDER BY rating DESC, review_count DESC
</select>
```

---

## 二、N+1查询问题修复（P0 - 必须修复）

### 问题描述
- MessageServiceImpl.getConversations() 在循环中查询用户信息
- CommunityServiceImpl.convertToCommentVO() 对每条评论查询用户信息

### 解决方案
使用MyBatis关联查询（LEFT JOIN）一次性获取关联数据，避免循环查询。

### 修改的文件

#### 1. Comment.java（Model）
新增字段（transient，不持久化到数据库）：
```java
private transient String userNickname;
private transient String userAvatar;
```

#### 2. CommentMapper.xml
已有的 `selectByTopicId` 方法使用 `CommentWithUserResultMap`，已经包含了LEFT JOIN查询用户信息。

#### 3. CommunityServiceImpl.java
修改 `convertToCommentVO()` 方法：
```java
// 修改前（N+1查询）
User user = userMapper.selectById(comment.getUserId());
if (user != null) {
    vo.setNickname(user.getNickname());
    vo.setAvatar(user.getAvatar());
}

// 修改后（使用已加载的用户信息）
if (comment.getUserNickname() != null) {
    vo.setNickname(comment.getUserNickname());
    vo.setAvatar(comment.getUserAvatar());
} else {
    // 向后兼容：如果没有加载用户信息，则查询数据库
    User user = userMapper.selectById(comment.getUserId());
    if (user != null) {
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
    }
}
```

#### 4. Message.java（Model）
新增字段（transient，不持久化到数据库）：
```java
private transient String senderNickname;
private transient String senderAvatar;
private transient String receiverNickname;
private transient String receiverAvatar;
```

#### 5. MessageMapper.java
新增方法：
```java
// 查询用户发送的所有私信（包含用户信息）
List<Message> selectBySenderWithUser(@Param("senderId") Long senderId);

// 查询用户接收的所有私信（包含用户信息）
List<Message> selectByReceiverWithUser(@Param("receiverId") Long receiverId);

// 查询两个用户之间的聊天记录（包含用户信息）
List<Message> selectBetweenUsersWithUser(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
```

#### 6. MessageMapper.xml
新增查询（使用MessageWithUsersResultMap）：
```xml
<!-- 查询用户发送的所有私信（包含用户信息） -->
<select id="selectBySenderWithUser" resultMap="MessageWithUsersResultMap">
    SELECT
        m.id, m.sender_id, m.receiver_id, m.content, m.is_read, m.is_deleted, m.create_time,
        s.nickname as sender_nickname, s.avatar as sender_avatar,
        r.nickname as receiver_nickname, r.avatar as receiver_avatar
    FROM user_message m
    LEFT JOIN sys_user s ON m.sender_id = s.id AND s.is_deleted = 0
    LEFT JOIN sys_user r ON m.receiver_id = r.id AND r.is_deleted = 0
    WHERE m.sender_id = #{senderId} AND m.is_deleted = 0
    ORDER BY m.create_time DESC
</select>

<!-- 查询用户接收的所有私信（包含用户信息） -->
<select id="selectByReceiverWithUser" resultMap="MessageWithUsersResultMap">
    SELECT
        m.id, m.sender_id, m.receiver_id, m.content, m.is_read, m.is_deleted, m.create_time,
        s.nickname as sender_nickname, s.avatar as sender_avatar,
        r.nickname as receiver_nickname, r.avatar as receiver_avatar
    FROM user_message m
    LEFT JOIN sys_user s ON m.sender_id = s.id AND s.is_deleted = 0
    LEFT JOIN sys_user r ON m.receiver_id = r.id AND r.is_deleted = 0
    WHERE m.receiver_id = #{receiverId} AND m.is_deleted = 0
    ORDER BY m.create_time DESC
</select>

<!-- 查询两个用户之间的聊天记录（包含用户信息） -->
<select id="selectBetweenUsersWithUser" resultMap="MessageWithUsersResultMap">
    SELECT
        m.id, m.sender_id, m.receiver_id, m.content, m.is_read, m.is_deleted, m.create_time,
        s.nickname as sender_nickname, s.avatar as sender_avatar,
        r.nickname as receiver_nickname, r.avatar as receiver_avatar
    FROM user_message m
    LEFT JOIN sys_user s ON m.sender_id = s.id AND s.is_deleted = 0
    LEFT JOIN sys_user r ON m.receiver_id = r.id AND r.is_deleted = 0
    WHERE ((m.sender_id = #{userId1} AND m.receiver_id = #{userId2})
        OR (m.sender_id = #{userId2} AND m.receiver_id = #{userId1}))
    AND m.is_deleted = 0
    ORDER BY m.create_time ASC
</select>
```

#### 7. MessageServiceImpl.java
修改 `getConversations()` 方法：
```java
// 修改前（N+1查询）
List<Message> sentMessages = messageMapper.selectBySenderId(userId);
List<Message> receivedMessages = messageMapper.selectByReceiverId(userId);
// ... 在循环中调用 userMapper.selectById(otherUserId)

// 修改后（使用JOIN查询）
List<Message> sentMessages = messageMapper.selectBySenderWithUser(userId);
List<Message> receivedMessages = messageMapper.selectByReceiverWithUser(userId);
// ... 优先使用 message.getSenderNickname() 等已加载的字段
```

修改 `getMessageHistory()` 方法：
```java
// 修改前
List<Message> messageList = messageMapper.selectBetweenUsers(currentUserId, otherUserId);

// 修改后
List<Message> messageList = messageMapper.selectBetweenUsersWithUser(currentUserId, otherUserId);
```

---

## 三、硬编码消除（P0 - 必须修复）

### 问题描述
数据库密码、JWT密钥、文件路径等敏感配置直接硬编码在application.yml中。

### 解决方案
使用Spring Boot的环境变量支持，通过 `${VAR_NAME:default}` 语法支持环境变量覆盖。

### 修改的文件

#### application.yml
```yaml
# 数据源配置
datasource:
  url: ${DB_URL:jdbc:mysql://localhost:3306/hospital_selection?...}
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:root}

# JWT配置
jwt:
  secret: ${JWT_SECRET:hospital-selection-secret-key-2024-very-long-and-secure}
  expiration: ${JWT_EXPIRATION:604800000}

# 文件上传配置
file:
  upload-path: ${FILE_UPLOAD_PATH:D:/upload/hospital-selection}
  access-url: ${FILE_ACCESS_URL:/uploads}
```

### 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/hospital_selection |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root |
| JWT_SECRET | JWT签名密钥 | hospital-selection-secret-key-2024-very-long-and-secure |
| JWT_EXPIRATION | JWT过期时间（毫秒） | 604800000 (7天) |
| FILE_UPLOAD_PATH | 文件上传保存路径 | D:/upload/hospital-selection |
| FILE_ACCESS_URL | 文件访问URL前缀 | /uploads |

---

## 改进效果

### 性能提升
1. **内存分页 → 物理分页**:
   - 减少内存占用：不再需要将全表数据加载到内存
   - 提升查询速度：数据库层分页比内存分页更高效
   - 避免OOM风险：大数据量场景下不会内存溢出

2. **N+1查询 → JOIN查询**:
   - 减少数据库往返次数：从 N+1 次查询减少到 1 次
   - 显著提升响应速度：特别是在会话列表、评论列表等场景
   - 降低数据库负载：减少网络IO和数据库CPU消耗

### 安全性提升
1. **敏感配置外部化**:
   - 支持环境变量覆盖，避免将敏感信息提交到代码仓库
   - 不同环境可以使用不同的配置（开发/测试/生产）

### 可维护性提升
1. **代码质量改进**:
   - 使用更标准的分页方式
   - 利用MyBatis的关联查询能力
   - 代码更清晰、更易理解

---

## 向后兼容性

所有改进都保持了向后兼容：
- 环境变量配置有默认值，现有配置无需修改即可运行
- Service接口签名未改变，前端无需修改
- 新增的Mapper方法是对原有方法的补充，未删除现有方法

---

## 后续优化方向（P1/P2）

### P1 - 近期实施
1. 添加批量操作接口
2. 完善Service层异常处理
3. 添加全局拦截器（日志、性能监控）
4. 增强参数验证

### P2 - 后续优化
1. 引入缓存机制
2. 优化数据库索引
3. 完善业务逻辑（推荐算法等）

---

## 测试建议

1. **功能测试**:
   - 测试所有分页接口，确保数据正确返回
   - 测试会话列表、评论列表，确保用户信息正确显示

2. **性能测试**:
   - 对比改进前后的接口响应时间
   - 测试大数据量场景（1000+ 条记录）

3. **集成测试**:
   - 前后端联调，确保前端功能不受影响
   - 验证环境变量配置是否生效
