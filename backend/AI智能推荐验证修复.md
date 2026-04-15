# AI智能推荐输入验证修复

## 问题描述
在AI智能推荐功能中，输入乱码、无厘头的文字（如"asdfjkl"、"$$$###"、"12345"）也能成功推荐医院，这是明显的逻辑漏洞。

## 根本原因分析

### 1. **缺少输入验证**
- `buildPrompt()` 方法直接拼接用户输入，没有做任何验证
- 乱码、无意义字符也被发送给AI

### 2. **AI强制返回JSON**
- 提示词要求"只返回JSON"
- 即使输入无意义，AI也会编造合理的JSON响应
- 例如：输入"asdfghjkl" → AI可能返回 `{"deptName": "神经内科", "reasoning": "用户可能需要神经内科..."}`

### 3. **缺少相关性校验**
- `parseAIResponse()` 只解析JSON，不验证返回条件是否与用户输入相关

### 4. **缺少置信度评估**
- 没有判断AI是否真正理解了用户意图

## 修复方案

### ✅ 修复1：优化AI提示词

**新增字段**：
```json
{
  "isValidQuery": "用户查询是否有意义且与医疗相关（true/false）"
}
```

**新增规则**：
```
7. 【关键】如果用户查询是乱码、无意义、与医疗完全无关，请将isValidQuery设为false，所有其他字段设为null
8. 【关键】只有在用户明确提到地区、等级、医保等信息时才填写对应字段，不要猜测或编造
```

### ✅ 修复2：增加AI响应验证 `parseAIResponse()`

**新增检查**：
```java
// 检查AI是否认为查询无效
if (result.has("isValidQuery") && !result.get("isValidQuery").isNull()) {
    boolean isValid = result.get("isValidQuery").asBoolean(false);
    if (!isValid) {
        log.warn("AI判断用户查询无效：{}", userQuery);
        throw new IllegalArgumentException("无法理解您的查询，请输入与医院、科室、症状或疾病相关的描述");
    }
}
```

### ✅ 修复3：增加结果合理性验证 `isFilterResultValid()`

**验证逻辑**：
```java
private boolean isFilterResultValid(HospitalFilterDTO filter, String userQuery) {
    // 1. 检查是否所有关键字段都为空
    boolean hasAnyCondition = filter.getProvinceCode() != null ||
            filter.getCityCode() != null ||
            filter.getAreaCode() != null ||
            filter.getHospitalLevel() != null ||
            filter.getDeptName() != null ||
            filter.getKeyDepartments() != null ||
            filter.getIsMedicalInsurance() != null;

    if (!hasAnyCondition) {
        return false;
    }

    // 2. 短查询的额外验证
    if (userQuery.trim().length() < 4 && hasAnyCondition) {
        if (filter.getDeptName() == null && filter.getHospitalLevel() == null) {
            return false;
        }
    }

    return true;
}
```

### ✅ 修复4：优化异常处理逻辑

**关键修改**：
```java
public HospitalFilterDTO analyzeQuery(String userQuery) {
    log.info("开始AI分析用户查询：{}", userQuery);

    try {
        // 1. 构建提示词
        String prompt = buildPrompt(userQuery);
        
        // 2. 调用AI API
        String aiResponse = callZhipuAPI(prompt);
        
        // 3. 解析响应
        HospitalFilterDTO filter = parseAIResponse(aiResponse, userQuery);
        
        // 4. 验证结果合理性
        if (!isFilterResultValid(filter, userQuery)) {
            throw new IllegalArgumentException("无法理解您的查询...");
        }
        
        return filter;
        
    } catch (IllegalArgumentException e) {
        // 业务异常：直接向上抛出，让Controller返回400错误
        throw e;
    } catch (Exception e) {
        // 系统异常：返回空筛选条件，用户可以手动筛选
        log.error("AI分析失败", e);
        return new HospitalFilterDTO();
    }
}
```

**为什么去掉 `isValidUserQuery()` 输入验证？**

1. **AI自身能判断**：通过 `isValidQuery` 字段让AI判断输入是否有效
2. **避免过度限制**：用户可能用口语化表达（如"肚子疼怎么办"），前端验证可能误杀
3. **简化逻辑**：减少一层验证，避免重复错误提示
4. **统一错误处理**：所有无效输入都通过AI判断，返回一致的错误信息

### ✅ 修复5：优化Controller异常处理

**修改文件**：`HospitalController.java`

**改进**：
```java
@PostMapping("/ai-recommend")
public Result<PageResult<HospitalSimpleVO>> aiRecommend(@RequestBody @Valid AIQueryRequestDTO request) {
    try {
        PageResult<HospitalSimpleVO> result = hospitalService.aiRecommendHospitals(request);
        return Result.success(result, "AI推荐成功");
    } catch (IllegalArgumentException e) {
        // 用户输入无效或AI无法理解 - 返回400
        log.warn("AI推荐参数错误：{}", e.getMessage());
        return Result.error(400, e.getMessage());
    } catch (Exception e) {
        // 系统异常 - 返回500
        log.error("AI推荐失败", e);
        return Result.error(500, "AI推荐失败：" + e.getMessage());
    }
}
```

## 修复后的验证流程

```
用户输入
   ↓
[1] 构建AI提示词 (buildPrompt)
   └─ 包含 isValidQuery 字段和验证规则
   ↓
[2] 调用AI API (callZhipuAPI)
   └─ 返回包含 isValidQuery 的JSON
   ↓
[3] 解析AI响应 (parseAIResponse)
   ├─ 检查 isValidQuery == false → 抛出 IllegalArgumentException
   └─ 继续解析筛选条件
   ↓
[4] 结果合理性验证 (isFilterResultValid)
   ├─ 失败 → 抛出 IllegalArgumentException
   └─ 成功 → 返回筛选结果
   ↓
[5] Service层异常处理
   ├─ IllegalArgumentException → 向上抛出
   └─ Exception → 返回空筛选条件
   ↓
[6] Controller异常处理
   ├─ IllegalArgumentException → 400错误（用户输入问题）
   └─ Exception → 500错误（系统异常）
```

**关键改进**：
- ✅ **去掉了 `isValidUserQuery()` 输入验证**，完全依赖AI的 `isValidQuery` 判断
- ✅ **简化了异常处理**，避免捕获后重新抛出导致异常被吃掉
- ✅ **统一的错误提示**，不会出现重复提示

## 测试用例

### ❌ 应该被拒绝的输入

| 测试输入 | 拒绝原因 | 预期错误码 |
|---------|---------|-----------|
| `asdfjkl` | AI判断无医疗相关关键词 | 400 |
| `$$$###` | AI判断无意义 | 400 |
| `12345` | AI判断纯数字无意义 | 400 |
| `!@#$%` | AI判断无意义 | 400 |
| `a` | AI判断输入太短 | 400 |
| `今天天气真好` | AI判断与医疗无关 | 400 |

### ✅ 应该被接受的输入

| 测试输入 | 预期结果 |
|---------|---------|
| `头痛去哪个医院` | 返回神经内科相关医院 |
| `北京最好的三甲医院` | 返回北京地区的三甲医院 |
| `心内科` | 返回心内科相关医院 |
| `胸痛` | 返回心内科相关医院 |
| `附近医保定点医院` | 返回医保定点医院 |

## 修改的文件

1. ✅ `ZhipuAIService.java`
   - 修改 `analyzeQuery()` 方法，去掉输入验证，优化异常处理
   - 修改 `buildPrompt()` 方法，增加 `isValidQuery` 字段
   - 修改 `parseAIResponse()` 方法，增加有效性检查
   - 新增 `isFilterResultValid()` 方法

2. ✅ `HospitalController.java`
   - 优化 `aiRecommend()` 方法的异常处理

## 编译状态
✅ 编译成功（`mvn compile -q`）

## 后续建议

1. **增加输入日志**：记录被拒绝的输入，用于分析用户行为
2. **优化医疗关键词库**：根据实际使用情况扩充关键词
3. **增加用户提示**：在前端给出更友好的错误提示
4. **考虑缓存机制**：对常见查询进行缓存，减少AI调用
5. **增加置信度评分**：在返回结果中增加置信度字段

## 作者
陈文滔

## 更新日期
2026-04-14
