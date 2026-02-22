<template>
  <div class="ai-recommendation">
    <el-button
      type="primary"
      @click="showDialog = true"
      class="ai-trigger-btn"
      size="large"
    >
      <el-icon class="ai-icon"><MagicStick /></el-icon>
      AI智能筛选
    </el-button>

    <el-dialog
      v-model="showDialog"
      title="AI智能筛选"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="ai-dialog-content">
        <el-alert
          type="info"
          :closable="false"
          show-icon
          class="info-alert"
        >
          告诉我您的症状和所在地，AI为您筛选合适的医院
        </el-alert>

        <div class="example-tips">
          <span class="tip-label">可以这样问：</span>
          <el-tag
            v-for="(example, index) in examples"
            :key="index"
            class="example-tag"
            @click="fillExample(example)"
          >
            {{ example }}
          </el-tag>
        </div>

        <el-input
          v-model="userQuery"
          type="textarea"
          :rows="4"
          placeholder="例如：我在北京朝阳区，经常头痛，想找三甲医院神经内科"
          maxlength="500"
          show-word-limit
        />
      </div>

      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSearch"
          :loading="loading"
          :disabled="!userQuery.trim()"
        >
          智能筛选
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, Search } from '@element-plus/icons-vue'
import { aiRecommendHospitals } from '@/api/hospital'

const emit = defineEmits(['recommend-success'])

const showDialog = ref(false)
const userQuery = ref('')
const loading = ref(false)

const examples = [
  '我在北京，经常头痛',
  '上海最好的骨科',
  '广州治疗骨折的三甲医院',
  '杭州儿科医院推荐'
]

const fillExample = (example) => {
  userQuery.value = example
}

const handleSearch = async () => {
  if (!userQuery.value.trim()) {
    ElMessage.warning('请输入查询内容')
    return
  }

  if (userQuery.value.length < 2) {
    ElMessage.warning('查询内容至少2个字')
    return
  }

  if (userQuery.value.length > 500) {
    ElMessage.warning('查询内容不能超过500字')
    return
  }

  loading.value = true

  try {
    const res = await aiRecommendHospitals({
      query: userQuery.value.trim(),
      pageNum: 1,
      pageSize: 10
    })

    if (res.code === 200) {
      const total = res.data.total || 0
      if (total > 0) {
        ElMessage.success(`为您找到 ${total} 家医院`)
        showDialog.value = false

        emit('recommend-success', {
          list: (res.data.list || []).map((item) => ({
            id: item.id,
            name: item.hospitalName || item.name,
            level: item.hospitalLevel || item.level,
            province: item.provinceName || item.province,
            city: item.cityName || item.city,
            address: item.address,
            rating: item.rating,
            avatar: null
          })),
          total: total
        })

        userQuery.value = ''
      } else {
        ElMessage.warning('未找到匹配的医院，请尝试其他关键词')
      }
    } else {
      ElMessage.error(res.message || 'AI推荐失败，请稍后重试')
    }
  } catch (error) {
    console.error('AI推荐失败', error)
    ElMessage.error(error.message || 'AI推荐失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-trigger-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-size: 16px;
  padding: 12px 24px;
  border-radius: 8px;
  transition: all 0.3s;
  width: 100%;
  margin-bottom: 16px;
}

.ai-trigger-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.ai-icon {
  margin-right: 6px;
  font-size: 18px;
}

.info-alert {
  margin-bottom: 16px;
}

.example-tips {
  margin: 16px 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.tip-label {
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}

.example-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.example-tag:hover {
  transform: scale(1.05);
  background-color: #667eea;
  color: white;
}
</style>
