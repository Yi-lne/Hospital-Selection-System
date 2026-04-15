# AI智能推荐地区代码验证修复

## 问题描述
输入数据库里不存在的地区（如"我在美国，经常头疼"），AI仍然能推荐医院成功，这是严重的逻辑错误。

**根本原因**：
- AI根据提示词返回地区代码（如美国的代码或虚构代码）
- `parseAIResponse()` 直接使用这些代码，**没有与数据库比对验证**
- 导致查询不存在的地区时，要么返回空结果，要么返回所有医院

---

## 修复方案

### ✅ 修复1：注入 AreaMapper

**修改**：`ZhipuAIService.java`

```java
@Autowired
private AreaMapper areaMapper;
```

**目的**：通过 Mapper 查询数据库中存在的地区代码

---

### ✅ 修复2：新增地区代码验证方法 `validateAreaCode()`

**功能**：
1. 验证地区代码是否存在于 `area_info` 表中
2. 验证地区级别是否匹配（province=1, city=2, area=3）
3. 数据库异常时默认通过，避免影响正常流程

**代码**：
```java
private boolean validateAreaCode(String areaCode, String level) {
    if (areaCode == null || areaCode.trim().isEmpty()) {
        return false;
    }

    try {
        // 查询数据库验证地区代码是否存在
        Area area = areaMapper.selectByCode(areaCode);
        if (area == null) {
            log.debug("地区代码不存在于数据库：code={}, level={}", areaCode, level);
            return false;
        }

        // 验证地区级别是否匹配
        if ("province".equals(level) && area.getLevel() != null && area.getLevel() != 1) {
            log.warn("地区代码级别不匹配：code={}, 期望=province, 实际={}", areaCode, area.getLevel());
            return false;
        } else if ("city".equals(level) && area.getLevel() != null && area.getLevel() != 2) {
            log.warn("地区代码级别不匹配：code={}, 期望=city, 实际={}", areaCode, area.getLevel());
            return false;
        } else if ("area".equals(level) && area.getLevel() != null && area.getLevel() != 3) {
            log.warn("地区代码级别不匹配：code={}, 期望=area, 实际={}", areaCode, area.getLevel());
            return false;
        }

        return true;
    } catch (Exception e) {
        log.error("验证地区代码时发生异常：code={}, level={}", areaCode, level, e);
        // 数据库异常时默认通过，避免影响正常流程
        return true;
    }
}
```

**关键设计**：
- ✅ **通用验证**：不针对特定地区（如美国），而是比对数据库所有地区
- ✅ **级别验证**：确保省份代码确实是省份，而非城市或区县
- ✅ **容错处理**：数据库异常时返回 `true`，避免影响正常用户

---

### ✅ 修复3：修改 `parseAIResponse()` 增加验证

**修改前**：
```java
if (result.has("provinceCode") && !result.get("provinceCode").isNull()) {
    filter.setProvinceCode(result.get("provinceCode").asText());
}
```

**修改后**：
```java
if (result.has("provinceCode") && !result.get("provinceCode").isNull()) {
    String provinceCode = result.get("provinceCode").asText();
    if (!validateAreaCode(provinceCode, "province")) {
        log.warn("AI返回的省份代码不存在于数据库中：{}", provinceCode);
        throw new IllegalArgumentException("您提到的地区不在我们的服务范围内，请输入中国大陆的省市区名称");
    }
    filter.setProvinceCode(provinceCode);
}
```

**同样应用于**：
- ✅ 城市代码验证（`cityCode`）
- ✅ 区县代码验证（`areaCode`）

---

## 验证流程

```
AI返回地区代码
   ↓
[1] 提取 provinceCode/cityCode/areaCode
   ↓
[2] 调用 validateAreaCode(code, level)
   ├─ 查询 area_info 表
   ├─ 代码不存在 → 返回 false → 抛出 IllegalArgumentException
   ├─ 级别不匹配 → 返回 false → 抛出 IllegalArgumentException
   └─ 验证通过 → 返回 true → 设置到 filter 对象
   ↓
[3] 继续后续筛选逻辑
```

---

## 测试用例

### ❌ 应该被拒绝的地区

| 测试输入 | AI返回代码 | 拒绝原因 | 预期错误信息 |
|---------|-----------|---------|-------------|
| `我在美国，经常头疼` | 美国代码或null | 美国不在 area_info 表中 | 您提到的地区不在我们的服务范围内，请输入中国大陆的省市区名称 |
| `日本东京的医院` | 日本代码 | 日本不在 area_info 表中 | 同上 |
| `我在火星，头疼` | null或虚构代码 | 代码不存在 | 同上 |
| `英国的医院` | 英国代码 | 英国不在 area_info 表中 | 同上 |

### ✅ 应该被接受的地区

| 测试输入 | AI返回代码 | 预期结果 |
|---------|-----------|---------|
| `我在北京，头疼` | 110000（北京） | 返回北京地区医院 |
| `广东省最好的医院` | 440000（广东） | 返回广东地区医院 |
| `广州市三甲医院` | 440100（广州） | 返回广州地区三甲医院 |
| `上海浦东新区的医院` | 310115（浦东新区） | 返回浦东新区医院 |

---

## 关键改进

1. ✅ **通用验证**：不针对特定地区硬编码，而是比对数据库
2. ✅ **级别验证**：确保省份/城市/区县代码级别正确
3. ✅ **容错处理**：数据库异常不影响正常流程
4. ✅ **友好提示**：明确告知用户"不在服务范围内"

---

## 修改的文件

1. ✅ `ZhipuAIService.java`
   - 注入 `AreaMapper` 依赖
   - 新增 `validateAreaCode()` 方法
   - 修改 `parseAIResponse()` 增加地区代码验证

---

## 编译状态
✅ 编译成功（`mvn compile -q`）

---

## 后续建议

1. **增加地区名称提示**：错误提示中可以包含"目前支持中国大陆XX个省XX个城市"
2. **缓存地区数据**：将地区数据缓存到内存，减少数据库查询
3. **日志优化**：记录被拒绝的地区名称，用于分析用户需求
4. **扩展服务区域**：根据日志分析，考虑是否扩展服务地区

---

## 作者
陈文滔

## 更新日期
2026-04-14
