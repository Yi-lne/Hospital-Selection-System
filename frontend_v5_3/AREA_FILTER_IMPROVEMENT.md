# 地区筛选多级支持 - 实现说明

## 修改时间
2026-02-06

## 需求

用户希望能够灵活地按地区筛选医院：
1. ✅ 筛选全省的医院（只选择省）
2. ✅ 筛选某省某市的医院（选择省+市）
3. ✅ 筛选某省某市某区/县的医院（选择省+市+区）

## 问题

之前的实现：
- 总是取级联选择器的最后一个值（区县代码）
- 用户必须选择到区/县级别才能看到筛选结果
- 无法只筛选全省或全市的医院

## 解决方案

### 1. 前端组件修改

#### 文件: `frontend/src/components/hospital/FilterPanel.vue`

**修改点1：扩展筛选数据结构**
```javascript
// 添加省份和城市编码字段
const filterData = reactive({
  diseaseCode: undefined,
  hospitalLevel: undefined,
  areaCode: undefined,      // 区县编码
  provinceCode: undefined,  // ✅ 新增：省份编码
  cityCode: undefined,      // ✅ 新增：城市编码
  isMedicalInsurance: undefined,
  keyDepartments: undefined,
  sortBy: 'default'
})
```

**修改点2：改进地区处理逻辑**
```javascript
// 处理地区编码（支持多级筛选）
if (Array.isArray(params.areaCode) && params.areaCode.length > 0) {
  const level = params.areaCode.length

  if (level === 1) {
    // 只选择了省份 → 筛选全省医院
    params.provinceCode = params.areaCode[0]
    params.cityCode = undefined
    params.areaCode = undefined
  } else if (level === 2) {
    // 选择了省份+城市 → 筛选全市医院
    params.provinceCode = params.areaCode[0]
    params.cityCode = params.areaCode[1]
    params.areaCode = undefined
  } else if (level === 3) {
    // 选择了省份+城市+区县 → 筛选该区医院
    params.provinceCode = params.areaCode[0]
    params.cityCode = params.areaCode[1]
    params.areaCode = params.areaCode[2]
  }
}
```

#### 文件: `frontend/src/views/hospital/HospitalList.vue`

**修改：地区变化处理**
```javascript
// 地区改变（支持多级筛选：省/省+市/省+市+区）
const handleAreaChange = (value) => {
  if (value && value.length > 0) {
    const level = value.length

    if (level === 1) {
      // 只选择了省份
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: undefined,
        areaCode: undefined
      })
    } else if (level === 2) {
      // 选择了省份+城市
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: value[1],
        areaCode: undefined
      })
    } else if (level === 3) {
      // 选择了省份+城市+区县
      hospitalStore.setFilterParams({
        provinceCode: value[0],
        cityCode: value[1],
        areaCode: value[2]
      })
    }
  } else {
    // 清空地区选择
    hospitalStore.setFilterParams({
      provinceCode: undefined,
      cityCode: undefined,
      areaCode: undefined
    })
  }

  loadHospitals()
}
```

**修改：筛选条件判断**
```javascript
const hasFilterConditions = () => {
  const params = hospitalStore.filterParams
  return !!(
    params.provinceCode ||  // ✅ 新增
    params.cityCode ||      // ✅ 新增
    params.areaCode ||
    params.level ||
    params.diseaseCode
  )
}
```

#### 文件: `frontend/src/stores/modules/hospital.js`

**修改：扩展筛选状态**
```javascript
const filterParams = ref({
  page: 1,
  pageSize: 10,
  areaCode: undefined,       // 区县编码
  provinceCode: undefined,   // ✅ 新增：省份编码
  cityCode: undefined,       // ✅ 新增：城市编码
  level: undefined,
  diseaseCode: undefined,
  name: undefined
})
```

**修改：API参数生成**
```javascript
const apiParams = computed(() => {
  const hospitalLevel = filterParams.value.level

  const params = {
    page: filterParams.value.page,
    pageSize: filterParams.value.pageSize,
    provinceCode: filterParams.value.provinceCode,  // ✅ 新增
    cityCode: filterParams.value.cityCode,          // ✅ 新增
    areaCode: filterParams.value.areaCode,
    hospitalLevel,
    diseaseCode: filterParams.value.diseaseCode
  }

  return params
})
```

### 2. 后端支持（已存在）

#### 文件: `backend/src/main/resources/mapper/HospitalMapper.xml`

后端 SQL 已经支持多级地区筛选：

```xml
<select id="selectByCondition" resultMap="BaseResultMap">
  SELECT <include refid="Base_Column_List"/>
  FROM hospital_info
  <where>
    is_deleted = 0
    <if test="hospitalLevel != null and hospitalLevel != ''">
      AND hospital_level = #{hospitalLevel}
    </if>
    <if test="provinceCode != null and provinceCode != ''">
      AND province_code = #{provinceCode}
    </if>
    <if test="cityCode != null and cityCode != ''">
      AND city_code = #{cityCode}
    </if>
    <if test="areaCode != null and areaCode != ''">
      AND area_code = #{areaCode}
    </if>
    <!-- 其他条件... -->
  </where>
  ORDER BY rating DESC, review_count DESC
</select>
```

SQL 使用 `<if>` 条件判断，可以灵活组合：
- 只传 `provinceCode` → 筛选全省
- 传 `provinceCode + cityCode` → 筛选全市
- 传 `provinceCode + cityCode + areaCode` → 筛选该区

## 使用场景

### 场景1：筛选全省医院

**操作步骤**：
1. 在地区级联选择器中选择"北京市"
2. 点击"应用筛选"

**效果**：
- 显示北京市所有区县的医院
- API 请求参数：`{ provinceCode: "110000" }`

### 场景2：筛选全市医院

**操作步骤**：
1. 在地区级联选择器中选择"北京市" → "市辖区"
2. 点击"应用筛选"

**效果**：
- 显示北京市市辖区所有医院（不限制具体哪个区）
- API 请求参数：`{ provinceCode: "110000", cityCode: "110100" }`

### 场景3：筛选区县医院

**操作步骤**：
1. 在地区级联选择器中选择"北京市" → "市辖区" → "东城区"
2. 点击"应用筛选"

**效果**：
- 只显示北京市东城区的医院
- API 请求参数：`{ provinceCode: "110000", cityCode: "110100", areaCode: "110101" }`

## 数据流程

```
用户选择地区
    ↓
el-cascader 返回数组
例如：["110000", "110100", "110101"]
    ↓
前端判断数组长度
    ├─ 长度=1 → 省份筛选
    ├─ 长度=2 → 城市筛选
    └─ 长度=3 → 区县筛选
    ↓
设置 filterParams
    ├─ provinceCode = array[0]
    ├─ cityCode = array[1]
    └─ areaCode = array[2]
    ↓
生成 apiParams
    ↓
发送请求到后端
    ↓
后端 SQL 使用 <if> 条件筛选
    ↓
返回筛选结果
```

## 测试验证

### 测试1：省份筛选

1. 访问医院列表页
2. 在地区选择器中选择"浙江省"
3. 点击"应用筛选"
4. 预期：显示浙江省所有城市的医院

### 测试2：城市筛选

1. 访问医院列表页
2. 在地区选择器中选择"浙江省" → "杭州市"
3. 点击"应用筛选"
4. 预期：显示杭州市所有区县的医院

### 测试3：区县筛选

1. 访问医院列表页
2. 在地区选择器中选择"浙江省" → "杭州市" → "西湖区"
3. 点击"应用筛选"
4. 预期：只显示杭州市西湖区的医院

### 测试4：清除筛选

1. 完成上述任一筛选
2. 点击地区选择器的清除按钮
3. 预期：显示全国所有医院，provinceCode、cityCode、areaCode 均为 undefined

## 修改文件清单

| 文件 | 状态 | 说明 |
|------|------|------|
| `frontend/src/components/hospital/FilterPanel.vue` | ✅ 修改 | 添加多级地区处理逻辑 |
| `frontend/src/views/hospital/HospitalList.vue` | ✅ 修改 | 改进地区变化处理 |
| `frontend/src/stores/modules/hospital.js` | ✅ 修改 | 扩展筛选状态和API参数 |
| `backend/src/main/resources/mapper/HospitalMapper.xml` | ✅ 无需修改 | SQL已支持多级筛选 |

## 注意事项

1. **级联选择器配置**：
   - 必须设置 `change-on-select` 属性，允许只选择部分级别
   - 当前配置已正确：`<el-cascader change-on-select />`

2. **数据格式**：
   - 地区数据格式：`["省代码", "市代码", "区代码"]`
   - 后端接收三个独立参数：`provinceCode`、`cityCode`、`areaCode`

3. **SQL 条件判断**：
   - 后端使用 `<if test="provinceCode != null">` 判断
   - 可以只传其中一个或多个参数
   - 三个条件是 AND 关系，可以灵活组合

4. **用户体验**：
   - 用户可以随时调整筛选粒度
   - 清除选择后会重置所有地区参数
   - 筛选结果即时更新

## 优势

1. **灵活性**：用户可以根据需要选择筛选粒度
2. **易用性**：不需要强制选择到最细级别
3. **准确性**：不同级别的筛选都能得到正确结果
4. **扩展性**：如果需要支持更多级别（如街道），可以轻松扩展

## 后续优化建议

1. **显示筛选标签**：在页面上显示当前生效的筛选条件（如"浙江省 杭州市"）
2. **快速清除**：为每个筛选条件添加快速清除按钮
3. **筛选历史**：记住用户的筛选偏好
4. **智能推荐**：根据用户位置自动推荐附近地区的医院
