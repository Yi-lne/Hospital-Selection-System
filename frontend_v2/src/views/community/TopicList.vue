<template>
  <div class="topic-list-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-title">话题列表</span>
      </template>
    </el-page-header>

    <div class="content-wrapper">
      <!-- 操作栏 -->
      <div class="action-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索话题..."
          clearable
          @keyup.enter="loadTopics"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" :icon="Plus" @click="$router.push('/community/publish')">
          发布话题
        </el-button>
      </div>

      <!-- 话题列表 -->
      <div v-loading="loading" class="topic-list">
        <template v-if="topicList.length > 0">
          <TopicCard v-for="topic in topicList" :key="topic.id" :topic="topic" />
        </template>
        <Empty v-else description="暂无话题，快来发布第一条吧！" />

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadTopics"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { getTopicList } from '@/api/community'
import TopicCard from '@/components/community/TopicCard.vue'
import Empty from '@/components/common/Empty.vue'

const loading = ref(false)
const topicList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 加载话题列表
const loadTopics = async () => {
  try {
    loading.value = true
    const res = await getTopicList(page.value, pageSize.value)
    topicList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('加载话题列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  page.value = 1
  pageSize.value = size
  loadTopics()
}

onMounted(() => {
  loadTopics()
})
</script>

<style scoped lang="scss">
.topic-list-page {
  .page-title {
    font-size: 20px;
    font-weight: 600;
  }

  .content-wrapper {
    margin-top: 20px;
  }

  .action-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;

    .el-input {
      max-width: 300px;
    }
  }

  .topic-list {
    min-height: 400px;
  }

  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
